/*
 * 
 * Copyright 2007-2012 Audrius Valunas
 * 
 * This file is part of OpenACS.

 * OpenACS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OpenACS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OpenACS.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.openacs.web;

import java.awt.Graphics2D;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.Thread.State;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import org.openacs.ConfigurationLocal;
import org.openacs.HostsLocal;
import org.openacs.utils.Ejb;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import javax.ejb.RemoveException;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.openacs.ATMErrorsStatsLocal;
import org.openacs.ATMErrorsStatsPK;
import org.openacs.BackupLocal;
import org.openacs.BackupLocalHome;
import org.openacs.BackupPK;
import org.openacs.CPELocal;
import org.openacs.DSLStatsLocal;
import org.openacs.DSLStatsLocalHome;
import org.openacs.datamodel.Parameter;
import org.openacs.HardwareModelLocal;
import org.openacs.HostsLocalHome;
import org.openacs.DataModelNode;
import org.openacs.Host2ServiceLocal;
import org.openacs.Host2ServiceLocalHome;
import org.openacs.Host2ServicePK;
import org.openacs.ServiceLocal;
import org.openacs.ServiceLocalHome;
import org.openacs.ServicePropertyLocal;
import org.openacs.ServicePropertyLocalHome;
import org.openacs.message.GetParameterNamesResponse;
import org.openacs.message.GetParameterValuesResponse;

public class HostsBean extends JsfBeanBase implements Serializable {

    protected static final String VALUE_MUST_BE_CHANGED = "MUST BE CHANGED";
    private static final String CONFIG_NONE = "None";

    /** Creates a new instance of jsfHostsBean */
    public HostsBean() {
    }

    private class ConnectionRequestWorker extends Thread {

        private HostsLocal host;
        private String result;
        private long startTime;
        private int timeout = 5000;

        ConnectionRequestWorker(HostsLocal h) {
            host = h;
        }

        @Override
        public void run() {
            result = "In progress";
            startTime = new Date().getTime();
            try {
                host.RequestConnection(timeout);
            } catch (Exception ex) {
                result = ex.getMessage();
                System.out.println("Conreq exception " + ex.getClass().getName() + ":" + ex.getMessage());
                return;
            }
            result = "Ok";
        }
        /*
        private String cpeurl;
        private String result;
        private long startTime;
        private int timeout = 5000;
        
        ConnectionRequestWorker(String cpeurl, String user, String pass) {
        this.cpeurl = cpeurl;
        if (user != null && pass != null) {
        String auth = user +":" + pass + "@";
        this.cpeurl = cpeurl.substring(0, 7) + user +":" + pass + "@" + cpeurl.substring(7);
        }
        }
        
        @Override
        public void run() {
        result = "In progress";
        startTime = new Date().getTime();
        System.out.println ("Conreq run, url="+cpeurl);
        try {
        //                URL url = new URL((user!=null && pass != null) ? cpeurl);
        URL url = new URL(cpeurl);
        URLConnection httpconn = url.openConnection();
        httpconn.setReadTimeout(timeout);
        httpconn.setConnectTimeout(timeout);
        httpconn.getContent();
        } catch (Exception ex) {
        result = ex.getMessage();
        if (result.equals("no content-type")) result = "Ok";
        System.out.println ("Conreq exception "+ex.getClass().getName()+":"+ex.getMessage());
        return;
        }
        result = "Ok";
        }
         */

        public String getResult() {
            return result;
        }

        public long getCurrentValue() {
            if (getState() == State.TERMINATED) {
                return 101;
            } else {
                return (new Date().getTime() - startTime) * 100 / timeout;
            }
        }
    }

    public class Service {

        private String name;
        private Integer instance;
        private Integer id;
        private ArrayList<Property> props = new ArrayList<Property>();

        public Service(Integer id, String name, Integer instance) {
            this.name = name;
            this.instance = instance;
            this.id = id;
        }

        public Property[] getProperties() {
            return props.toArray(new Property[props.size()]);
        }

        public void addProperty(Property prop) {
            props.add(prop);
        }

        public String getName() {
            return name;
        }

        public int getInstance() {
            return instance;
        }

        public Integer getId() {
            return id;
        }
    }
    private ConnectionRequestWorker crw;

    public String requestReboot() {
        try {
            HostsLocal h = Ejb.lookupHostsBean().findByHwidAndSn(hwid, sn);
            h.setReboot(true);
            connectionRequest();
        } catch (FinderException ex) {
        }
        return null;
    }

    public String connectionRequest() {
        if (crw == null || crw.getState() == State.TERMINATED) {
            try {
                System.out.println("Connection request START");
                HostsLocal h = Ejb.lookupHostsBean().findByHwidAndSn(hwid, sn);
//            crw = new ConnectionRequestWorker(url, conreqUser, conreqPass);
                crw = new ConnectionRequestWorker(h);
                crw.start();
            } catch (FinderException ex) {
                Logger.getLogger(HostsBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Connection request no......");
        }
        return null;
    }

    public boolean getInProgress() {
        boolean res;
        if (crw == null) {
            res = false;
        } else {
            res = (crw.getState() != State.TERMINATED);
            //System.out.println ("GetInProgress="+res);
        }
        return res;
    }

    public String getReqRes() {
        String res;
        if (crw == null) {
            res = "unknown";
        } else {
            res = (crw.getResult());
            //System.out.println ("getReqRes="+res);
        }
        return res;
    }

    public long getCurrentValue() {
        long res = -1;
        if (crw != null) {
            res = crw.getCurrentValue();
        }
        //System.out.println ("getCurrentValue = "+res);
        return res;
    }

    public String Save() throws IOException {
//        System.out.println ("Save cid="+customerid);
        HostsLocal h;
        try {
//            h = Ejb.lookupHostsBean().findByPrimaryKey(new HostsPK(ouiIn, snIn));
            h = Ejb.lookupHostsBean().findByHwidAndSn(hwid, sn);
            if (CONFIG_NONE.equals(configname)) {
                h.setConfigname(null);
            } else {
                h.setConfigname(configname);
            }

            h.setUsername(user);
            h.setPassword(password);
            h.setAuthtype(authType);

            h.setProps(proptext.getBytes());
            h.setCustomerid(customerid);
            h.setConrequser(conreqUser);
            h.setConreqpass(conreqPass);
            h.setCustomerid(customerid);
            h.setProfileName(profileName);
            h.setForcePasswords(forcePasswordSync);

            props.Save();
            setInfoMessage("Saved.");
        } catch (FinderException ex) {
        } catch (CreateException ex) {
            setErrorMessage(ex.getMessage());
        } catch (RemoveException ex) {
            setErrorMessage(ex.getMessage());
        }
        return null;
    }

    public String newItem() {
        HostsLocalHome hh = Ejb.lookupHostsBean();
        try {
//            HostsLocal h = hh.create(ouiIn, snIn, "");
            HostsLocal h = hh.create(hwid, snIn, "");
        } catch (CreateException ex) {
            setErrorMessage("Create failed: " + ex.getMessage());
//            ex.printStackTrace();
//            throw new RuntimeException("Already exists?");
            return null;
        }
        findItem();
        setInfoMessage("Created successfully.");
        return "config";
    }

    public String editItem() {
        //System.out.println("EDITITEM");
        return null;
    }

    public String deleteItem() {
        //System.out.println("DELTEITEM");
        dslstatslist = null;
        return null;
    }

    public Collection getConfigs() {
        try {
//            Iterator cfgs = Ejb.lookupConfigurationBean().findAll().iterator();
            Iterator cfgs = Ejb.lookupConfigurationBean().findByHwid(hwid).iterator();
            ArrayList<SelectItem> a = new ArrayList<SelectItem>();
            a.add(new SelectItem(CONFIG_NONE));
            while (cfgs.hasNext()) {
                ConfigurationLocal cfg = (ConfigurationLocal) cfgs.next();
                a.add(new SelectItem(cfg.getName()));
            }
            return a;
        } catch (FinderException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Collection getHardwareList() {
        try {
            Iterator cfgs = Ejb.lookupHardwareModelBean().findAll().iterator();
            ArrayList<SelectItem> a = new ArrayList<SelectItem>();
            while (cfgs.hasNext()) {
                HardwareModelLocal model = (HardwareModelLocal) cfgs.next();
                a.add(new SelectItem(((Integer) model.getId()).toString(), model.getDisplayName()));
                if (hwid == null) {
                    hwid = (Integer) model.getId();
                }
            }
            return a;
        } catch (FinderException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    private String customerid_in;

    public void setCustomerid_in(String customerid_in) {
        this.customerid_in = customerid_in;
    }

    public String getCustomerid_in() {
        return customerid_in;
    }
    private Collection<HostsLocal> findResult;

    public Collection getFindResult() {
        return findResult;
    }

    public int getFindResultLength() {
        int r = 0;
        if (findResult != null) {
            r = findResult.size();
        }
//        System.out.print("getFindResultLength = " + r);
        return r;
    }
    ArrayList<Service> services;

    private void loadItem(HostsLocal h) {
        id = (Integer) h.getId();
        hwid = h.getHwid();
        sn = h.getSerialno();
        url = h.getUrl();
        lastcontact = h.getLastcontact();
        sfwupdres = h.getSfwupdres();
        sfwupdtime = h.getSfwupdtime();
        currversion = h.getCurrentsoftware();
        cfgupdres = h.getCfgupdres();
        cfgupdtime = h.getCfgupdtime();
        configname = h.getConfigname();
        hardware = h.getHardware();
        cfgversion = h.getCfgversion();

        user = h.getUsername();
        password = h.getPassword();
        authType = (h.getAuthtype() != null) ? h.getAuthtype() : 0;
        customerid = h.getCustomerid();
        conreqUser = h.getConrequser();
        conreqPass = h.getConreqpass();
        profileName = h.getProfileName();
        forcePasswordSync = h.getForcePasswords();
        if (forcePasswordSync == null) {
            forcePasswordSync = false;
        }

        try {
            dslstats = Ejb.lookupDSLStatsBean().findByCpeAndTime((Integer) h.getId(), lastcontact);
        } catch (FinderException ex) {
            dslstats = null;
            //Logger.getLogger(HostsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        BackupLocalHome blh = Ejb.lookupBackupBean();
        try {
            lastBackup = blh.getTimeOfLastBackup(id);
        } catch (FinderException ex) {
            lastBackup = null;
        }


        byte[] p = h.getProps();
        proptext = (p != null) ? new String(p) : "";

        LoadProperties(h);
        //setPath("InternetGatewayDevice");
    }

    protected void LoadProperties(HostsLocal h) {
        //try {
            /*
        UserTransaction utx = null;
        InitialContext ctx = new InitialContext();
        Object tx = ctx.lookup("java:comp/UserTransaction");
        System.out.println("tx " + tx.getClass().getName() + " " + tx);
        utx = (UserTransaction) tx;
        utx.begin();
        h = Ejb.lookupHostsBean().findByPrimaryKey(id);
         */

        props = new HostPropertySet(id);
        try {
            props.Load(Ejb.lookupHostPropertyBean().findByHost(id));
            //setPath("InternetGatewayDevice");
        } catch (FinderException ex) {
            System.out.println("HostsBean::Loaditem exception on props" + ex);
        }

        ServiceLocalHome serviceHome = Ejb.lookupServiceBean();
        Host2ServiceLocalHome h2sHome = Ejb.lookupHost2ServiceBean();
        ServicePropertyLocalHome svcpropHome = Ejb.lookupServicePropertyBean();
        services = new ArrayList<Service>();
        //Iterator<Host2ServiceLocal> h2ss = h.getServices().iterator();
        Iterator<Host2ServiceLocal> h2ss = null;
        try {
            System.out.println("find h2s: id=" + id);
            h2ss = h2sHome.findByHostId(id).iterator();
        } catch (FinderException ex) {
            System.out.println("find h2s: " + ex);
            return;
        }
        while (h2ss.hasNext()) {
            Host2ServiceLocal h2s = h2ss.next();
            //ServiceLocal svc = h2s.getService();
            System.out.println("Find service: " + h2s.getServiceid());
            ServiceLocal svc;
            try {
                svc = serviceHome.findByPrimaryKey(h2s.getServiceid());
            } catch (FinderException ex) {
                Logger.getLogger(HostsBean.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            System.out.println("Find service: " + svc.getName());
            //Iterator<ServicePropertyLocal> svcprops = svc.getProperties().iterator();
            Service s = new Service((Integer) svc.getId(), svc.getName(), h2s.getInstance());
            Iterator<ServicePropertyLocal> svcprops = null;
            try {
                svcprops = svcpropHome.findByServiceId((Integer) svc.getId()).iterator();
                while (svcprops.hasNext()) {
                    ServicePropertyLocal svcprop = svcprops.next();
                    System.out.println("svc prop " + svcprop.getName() + " " + svcprop.getIsparam());
                    if (!svcprop.getIsparam()) {
                        continue;
                    }
                    String name = svcprop.getName();
                    name = name.replace("{i}", h2s.getInstance().toString());

                    Property prop = props.getProperties().get(name);
                    if (prop == null) {
                        prop = new Property(name, VALUE_MUST_BE_CHANGED, true);
                        props.getProperties().put(name, prop);
                    } else {
                        prop.setHidden(true);
                    }
                    s.addProperty(prop);
                }
            } catch (FinderException ex) {
                System.out.println("Find svc props  ex=" + ex);
            }
            System.out.println("Add service " + s);
            services.add(s);
        }
        /*
        } catch (Exception ex) {
        
        Logger.getLogger(HostsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
         */
        /*
        finally {
        if (utx != null) {
        try {
        utx.commit();
        } catch (Exception ex) {
        Logger.getLogger(HostsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        }
         */
    }
    Integer svcidin;

    public void setSvcidin(Integer svcidin) {
        this.svcidin = svcidin;
    }

    public Integer getSvcidin() {
        return svcidin;
    }
    Integer svcinstancein;

    public void setSvcinstancein(Integer svcinstance) {
        this.svcinstancein = svcinstance;
    }

    public Integer getSvcinstance() {
        return svcinstancein;
    }

    public Service[] getServices() {
        return services.toArray(new Service[services.size()]);
    }

    protected void LoadProperties() {
        try {
            HostsLocal h = Ejb.lookupHostsBean().findByPrimaryKey(id);
            LoadProperties(h);
        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
        }
    }

    protected Integer findUnusedInstance() {
        Integer instance = null;
        for (int i = 1; i < 1000; i++) {
            boolean f = false;
            for (Service s : services) {
                if (!s.getId().equals(svcidin)) {
                    continue;
                }
                if (s.getInstance() == i) {
                    f = true;
                    break;
                }
            }
            if (!f) {
                instance = i;
                break;
            }
        }
        return instance;
    }

    protected String getServiceForm() {
        ServiceLocal service;
        try {
            service = Ejb.lookupServiceBean().findByPrimaryKey(svcidin);
        } catch (FinderException ex) {
            Logger.getLogger(HostsBean.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        String formname = service.getType() + "_assign.xhtml";
        try {
            InputStream rs = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(formname);
            if (rs != null) {
                return service.getType();
            }
        } catch (Exception e) {
        }
        return null;
    }

    public String ServiceAssign() {

        String form = getServiceForm();
        if (form != null) {
            return form;
        }

        Integer instance = findUnusedInstance();
        if (instance == null) {
            setErrorMessage("Failed to find unused instance number");
            return null;
        }
        Host2ServiceLocal svci;
        try {
            svci = Ejb.lookupHost2ServiceBean().create(id, svcidin, instance);
            //Iterator<ServicePropertyLocal> ps = svci.getService().getProperties().iterator();
            Iterator<ServicePropertyLocal> ps = Ejb.lookupServicePropertyBean().findByServiceId(svcidin).iterator();
            while (ps.hasNext()) {
                ServicePropertyLocal prop = ps.next();
                if (prop.getIsparam()) {
                    String name = prop.getName().replace("{i}", instance.toString());
                    String value = prop.getValue();
                    if (value == null || value.equals("")) {
                        value = VALUE_MUST_BE_CHANGED;
                    }
                    props.Add(new Property(name, value, true));
                }
            }
            Save();
            LoadProperties();
        } catch (Exception ex) {
            setErrorMessage(ex.getMessage());
            return null;
        }
        LoadProperties();
        return null;
    }

    public String ServiceUnassign() {
        try {
            ServiceLocal svc = Ejb.lookupServiceBean().findByPrimaryKey(svcidin);
            Iterator<ServicePropertyLocal> ps = Ejb.lookupServicePropertyBean().findByServiceId(svcidin).iterator();
//            Iterator<ServicePropertyLocal> ps = svc.getProperties().iterator();
            while (ps.hasNext()) {
                ServicePropertyLocal prop = ps.next();
                if (prop.getIsparam()) {
                    String name = prop.getName().replace("{i}", svcinstancein.toString());
                    props.Remove(name);
                }
            }
            Host2ServiceLocalHome h2shome = Ejb.lookupHost2ServiceBean();
            h2shome.remove(new Host2ServicePK(id, svcidin, svcinstancein));
            Save();
            LoadProperties();
        } catch (Exception ex) {
            setErrorMessage(ex.getMessage());
            return null;
        }
        return null;
    }
    private int id;

    public int getId() {
        return id;
    }
    protected Timestamp lastBackup;

    public Timestamp getLastBackup() {
        return lastBackup;
    }

    public void setId(int id) {
        System.out.println("setId=" + id);
        this.id = id;
    }

    public String getItem() {
        //System.out.println ("getItem");
        HostsLocal h;
        try {
            h = Ejb.lookupHostsBean().findByPrimaryKey(id);
            findResult = new ArrayList<HostsLocal>();
            findResult.add(h);
            loadItem(h);
        } catch (FinderException ex) {
            Logger.getLogger(HostsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "cpegetitem";
    }
    private DSLStatsLocal dslstats = null;

    public String findItem() {
        //System.out.println ("findItem");
        dslstatslist = null;
        url = null;
        thecfg = null;

        timeFrom = new Date();
        timeFrom.setTime(timeFrom.getTime() - 7 * 24 * 3600 * 1000);
        timeTo = new Date();

        AtmErrorStatsCurrentDay = null;
        AtmErrorStatsQuaterHour = null;
        AtmErrorStatsTotal = null;
        AtmErrorStatsShowTime = null;

        HostsLocal h;
        try {
//            h = Ejb.lookupHostsBean().findByPrimaryKey(new HostsPK(ouiIn, snIn));
            if (customerid_in != null && !customerid_in.equals("")) {
                //System.out.println ("findItemByCustomerId ="+customerid_in);
                findResult = Ejb.lookupHostsBean().findByCustomerId(customerid_in);
                sn = null;
                if (findResult.size() == 0) {
                    /*
                    FacesContext ctx = FacesContext.getCurrentInstance();
                    ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "CPE not found in DB", "Nu detales"));
                     */
                    setErrorMessage("CPE not found in DB");
                    return "notfound";
                } else if (findResult.size() == 1) {
                    loadItem((HostsLocal) findResult.iterator().next());
                    return "foundone";
                } else {
                    return "foundmany";
                }
            } else {
                //System.out.println ("findItemBySn");
                h = Ejb.lookupHostsBean().findByHwidAndSn(hwid, snIn);
                findResult = new ArrayList<HostsLocal>();
                findResult.add(h);
            }
        } catch (FinderException ex) {
            sn = null;
            findResult = new ArrayList<HostsLocal>();
            setErrorMessage("CPE not found in DB");
            /*
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "CPE not found in DB", "Nu detales"));
             */
            return "notfound";
        }
        loadItem(h);

        return "foundone";
    }
    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    private int authType;

    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }
    /**
     * Holds value of property sn.
     */
    private String sn;

    /**
     * Getter for property sn.
     * @return Value of property sn.
     */
    public String getSn() {
        return this.sn;
    }

    /**
     * Setter for property sn.
     * @param sn New value of property sn.
     */
    public void setSn(String sn) {
        this.sn = sn;
    }
    private static final int COUNT_SUGGESTIONS = 20;

    public Collection autocompleteSerialNo(Object v) {
        //System.out.println ("CPE autocompleteSerialNo");
        ArrayList<String> a = new ArrayList<String>(COUNT_SUGGESTIONS);
        Iterator it;
        try {
            //System.out.println ("AUTOCOMPLETE hwid="+hwid+" v="+v);
            it = Ejb.lookupHostsBean().findByPartialSN(hwid, (String) v).iterator();
        } catch (FinderException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        for (int i = 0; i < COUNT_SUGGESTIONS && it.hasNext(); i++) {
            a.add(i, ((HostsLocal) it.next()).getSerialno());
        }
        return a;
    }
    /**
     * Holds value of property snIn.
     */
    private String snIn;

    /**
     * Getter for property sn_in.
     * @return Value of property sn_in.
     */
    public String getSnIn() {
        return this.snIn;
    }

    /**
     * Setter for property sn_in.
     * @param sn_in New value of property sn_in.
     */
    public void setSnIn(String snIn) {
        this.snIn = snIn;
    }
    /**
     * Holds value of property ouiIn.
     */
    private Integer hwid;

    /**
     * Getter for property oui_in.
     * @return Value of property oui_in.
     */
    public Integer getHwid() {
        return this.hwid;
    }

    /**
     * Setter for property oui_in.
     * @param oui_in New value of property oui_in.
     */
    public void setHwid(Integer hwid) {
        //System.out.println ("SETHWID="+hwid);
        this.hwid = hwid;
    }
    /**
     * Holds value of property url.
     */
    private String url;

    /**
     * Getter for property url.
     * @return Value of property url.
     */
    public String getUrl() {
        return this.url;
    }

    public String getWebUIUrl() {
        if (this.url != null) {
            try {
                URL url = new URL(this.url);
                return "http://" + url.getHost();
            } catch (MalformedURLException ex) {
            }
        }
        return "";
    }

    /**
     * Setter for property url.
     * @param url New value of property url.
     */
    public void setUrl(String url) {
        this.url = url;
    }
    /**
     * Holds value of property lastcontact.
     */
    private Timestamp lastcontact;

    /**
     * Getter for property lastcontact.
     * @return Value of property lastcontact.
     */
    public Timestamp getLastcontact() {
        return this.lastcontact;
    }

    /**
     * Setter for property lastcontact.
     * @param lastcontact New value of property lastcontact.
     */
    public void setLastcontact(Timestamp lastcontact) {
        this.lastcontact = lastcontact;
    }
    /**
     * Holds value of property swfupdres.
     */
    private String sfwupdres;

    /**
     * Getter for property swfupdres.
     * @return Value of property swfupdres.
     */
    public String getSfwupdres() {
        return this.sfwupdres;
    }

    /**
     * Setter for property swfupdres.
     * @param swfupdres New value of property swfupdres.
     */
    public void setSfwupdres(String swfupdres) {
        this.sfwupdres = swfupdres;
    }
    /**
     * Holds value of property sfwupdtime.
     */
    private Timestamp sfwupdtime;

    /**
     * Getter for property sfwupdtime.
     * @return Value of property sfwupdtime.
     */
    public String getSfwupdtime() {
        if (this.sfwupdtime != null && this.sfwupdtime.getTime() > 24 * 3600 * 1000 * 10) {
            return this.sfwupdtime.toString();
        }
        return "Never";
    }

    /**
     * Setter for property sfwupdtime.
     * @param sfwupdtime New value of property sfwupdtime.
     */
    public void setSfwupdtime(Timestamp sfwupdtime) {
        this.sfwupdtime = sfwupdtime;
    }
    /**
     * Holds value of property currversion.
     */
    private String currversion;

    /**
     * Getter for property currversion.
     * @return Value of property currversion.
     */
    public String getCurrversion() {
        return this.currversion;
    }

    /**
     * Setter for property currversion.
     * @param currversion New value of property currversion.
     */
    public void setCurrversion(String currversion) {
        this.currversion = currversion;
    }
    /**
     * Holds value of property configname.
     */
    private String configname;

    /**
     * Getter for property configname.
     * @return Value of property configname.
     */
    public String getConfigname() {
        return this.configname;
    }

    /**
     * Setter for property configname.
     * @param configname New value of property configname.
     */
    public void setConfigname(String configname) {
        this.configname = configname;
    }
    /**
     * Holds value of property cfgupdres.
     */
    private String cfgupdres;

    /**
     * Getter for property cfgupdres.
     * @return Value of property cfgupdres.
     */
    public String getCfgupdres() {
        return this.cfgupdres;
    }

    /**
     * Setter for property cfgupdres.
     * @param cfgupdres New value of property cfgupdres.
     */
    public void setCfgupdres(String cfgupdres) {
        this.cfgupdres = cfgupdres;
    }
    /**
     * Holds value of property cfgupdtime.
     */
    private Timestamp cfgupdtime;

    /**
     * Getter for property cfgupdtime.
     * @return Value of property cfgupdtime.
     */
    public String getCfgupdtime() {
        if (this.cfgupdtime != null && this.cfgupdtime.getTime() > 24 * 3600 * 1000 * 10) {
            return this.cfgupdtime.toString();
        }
        return "Never";
    }

    /**
     * Setter for property cfgupdtime.
     * @param cfgupdtime New value of property cfgupdtime.
     */
    public void setCfgupdtime(Timestamp cfgupdtime) {
        this.cfgupdtime = cfgupdtime;
    }
    /**
     * Holds value of property hardware.
     */
    private String hardware;

    /**
     * Getter for property hardware.
     * @return Value of property hardware.
     */
    public String getHardware() {
        return this.hardware;
    }

    /**
     * Setter for property hardware.
     * @param hardware New value of property hardware.
     */
    public void setHardware(String hardware) {
        this.hardware = hardware;
    }
    /**
     * Holds value of property cfgversion.
     */
    private String cfgversion;

    /**
     * Getter for property cfgversion.
     * @return Value of property cfgversion.
     */
    public String getCfgversion() {
        return this.cfgversion;
    }

    /**
     * Setter for property cfgversion.
     * @param cfgversion New value of property cfgversion.
     */
    public void setCfgversion(String cfgversion) {
        this.cfgversion = cfgversion;
    }
    private Boolean forcePasswordSync;

    public Boolean getForcePasswordSync() {
        return forcePasswordSync;
    }

    public void setForcePasswordSync(Boolean forcePasswordSync) {
        this.forcePasswordSync = forcePasswordSync;
    }
    private String customerid;

    public String getCustomerId() {
        return customerid;
    }

    public void setCustomerId(String customerid) {
        this.customerid = customerid;
    }

    public class Params {

        public Params(String name, String value, boolean writable) {
            this.name = name;
            this.value = value;
            this.writable = writable;
        }
        private String name;
        private boolean writable;
        private String value;

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        public boolean getWritable() {
            return this.writable;
        }

        @Override
        public String toString() {
            return name + (writable ? "(W)" : "(R)") + "=" + value;
        }
    }
    private String proptext;

    public void setProptext(String proptext) {
        this.proptext = proptext;
    }

    public String getProptext() {
        return proptext;
    }
    private String pathNames = ".";

    public void setPathNames(String pathNames) {
        this.pathNames = pathNames;
        names = null;
    }

    public String getPathNames() {
        return this.pathNames;
    }

    public String startBrowser() {
        findItem();
        pathNames = ".";
        names = null;
        return null;
    }
    private ArrayList<Params> names = new ArrayList<Params>();

    public ArrayList getNames() {
        if (names == null) {
            CPELocal cpe = Ejb.lookupCPEBean();

            HostsLocalHome hh = Ejb.lookupHostsBean();
            HostsLocal h;
            try {
                h = hh.findByHwidAndSn(hwid, sn);
            } catch (FinderException ex) {
                Logger.getLogger(HostsBean.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

            GetParameterNamesResponse r = cpe.GetParameterNames(h, pathNames, true);
            String ns[] = new String[r.names.size()];

            int count = 0;
            Enumeration k = r.names.keys();
            while (k.hasMoreElements()) {
                String n = (String) k.nextElement();
                if (!n.endsWith(".")) {
                    ns[count++] = n;
                    //System.out.println("Add " + n);
                }
            }
            //System.out.println("Total " + count);
            String ns2[] = new String[count];
            System.arraycopy(ns, 0, ns2, 0, count);
            //System.out.println("Total " + ns.length);
//            GetParameterValuesResponse v = cpe.GetParameterValues(oui, sn, ns2);
            GetParameterValuesResponse v = null;
            k = r.names.keys();
            names = new ArrayList<Params>();

            while (k.hasMoreElements()) {
                String n = (String) k.nextElement();
                names.add(new Params(n, n.endsWith(".") ? "" : (String) v.values.get(n), r.names.get(n).equals("1") ? true : false));
            }
        }
        return this.names;
    }
    private boolean browser = false;

    public void setBrowser(boolean browser) {
        this.browser = browser;
    }

    public boolean getBrowser() {
        return this.browser;
    }
    private boolean details = false;

    public void setDetails(boolean details) {
        this.details = details;
    }

    public boolean getDetails() {
        return this.details;
    }
    private String conreqUser;

    public String getConreqUser() {
        return conreqUser;
    }

    public void setConreqUser(String conreqUser) {
        this.conreqUser = conreqUser;
    }
    private String conreqPass;

    public String getConreqPass() {
        return conreqPass;
    }

    public void setConreqPass(String conreqPass) {
        this.conreqPass = conreqPass;
    }
    private Date timeFrom;

    /**
     * Get the value of timeFrom
     *
     * @return the value of timeFrom
     */
    public Date getTimeFrom() {
        return timeFrom;
    }

    /**
     * Set the value of timeFrom
     *
     * @param timeFrom new value of timeFrom
     */
    public void setTimeFrom(Date timeFrom) {
        this.timeFrom = timeFrom;
    }
    protected Date timeTo;

    /**
     * Get the value of timeTo
     *
     * @return the value of timeTo
     */
    public Date getTimeTo() {
        return timeTo;
    }

    /**
     * Set the value of timeTo
     *
     * @param timeTo new value of timeTo
     */
    public void setTimeTo(Date timeTo) {
        this.timeTo = timeTo;
    }
    private Collection<DSLStatsLocal> dslstatslist;

    public Collection<DSLStatsLocal> getDslStats() {
        return dslstatslist;
    }

    public String findStats() {
        Timestamp tf = new Timestamp(timeFrom.getTime());
        Timestamp tt = new Timestamp(timeTo.getTime());
        try {
//            System.out.println ("DSLstats: hwid="+hwid+" sn="+sn+" tf="+tf+" tt="+tt);
            HostsLocal h = Ejb.lookupHostsBean().findByHwidAndSn(hwid, sn);
            DSLStatsLocalHome s = Ejb.lookupDSLStatsBean();
            dslstatslist = s.findByCpeAndTime2((Integer) h.getId(), tf, tt);
        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
        }
        return null;
    }

    public boolean isDslStatsReady() {
        return dslstatslist != null && !dslstatslist.isEmpty();
    }
    //-----------------------------------------------------------------------
    private boolean multiValue = false;

    public boolean isMultiValue() {
        //System.out.println("isMultiValue: " + multiValue);
        return multiValue;
    }
    private DataModelNode thecfg = null;
    private DataModelNode curcfg = null;

    public DataModelNode getCurrentObject() {
        return curcfg;
    }
    private String[] parameterNames = null;

    public String[] getHeaders() {
        getCfg();
        return parameterNames;
    }
    private Object[][] parameterValues = null;
    private String currentPath = "InternetGatewayDevice";

    private DataModelNode getCfg() {
        if (thecfg == null) {
            parameterNames = null;
            parameterValues = null;
            currentPath = "";
            thecfg = new DataModelNode(null);
            InputStream in;
            try {
                //in = new FileInputStream("c:/temp/tr.txt");
                BackupLocalHome blh = Ejb.lookupBackupBean();
                Timestamp t = blh.getTimeOfLastBackup(id);

                BackupLocal bl = blh.findByPrimaryKey(new BackupPK(id, t, BackupLocal.TYPE_VENDOR_INDEPENDANT));
                in = new ByteArrayInputStream(bl.getCfg());
                thecfg.load(in);
                setPath(currentPath = thecfg.getName());
            } /*catch (FileNotFoundException ex) {
            Logger.getLogger(HostsBean.class.getName()).log(Level.SEVERE, null, ex);
            }*/ catch (FinderException ex) {
                setErrorMessage("No saved values found. \nCheck settings in profile or wait for device contacting ACS.");
//                Logger.getLogger(HostsBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return thecfg;
    }

    public synchronized Object[] getDataModelNodes() {
        return getCfg().getValues(currentPath).toArray();
    }

    public String domodeltree() {
        System.out.println("domodeltree");
        return null;
    }

    public Object[][] getCols() {
        getCfg();
        return parameterValues;
    }

    public int getRowCount() {
        Object[][] o = getCols();
        return (o != null) ? o.length : 0;
    }

    public void setPath(String p) {
        curcfg = thecfg.getValue(p);
        parameterNames = curcfg.getParamNames();
        parameterValues = curcfg.getParamValues(parameterNames);
        currentPath = p;
        /*
        if (parameterNames.length == 2 && !curcfg.isMultiInstance()) {
        for (Object[] o : parameterValues) {
        try {
        String s = (String) o[1];
        if (s != null) {
        o[1] = s.replaceAll("\n", "<br/>");
        }
        } catch (ClassCastException e) {
        }
        }
        }
         */
    }

    public String getPath() {
        return currentPath;
    }

    public Path[] getPathArray() {
        Path p = new Path();
        return p.fromString(currentPath);
        //return "InternetGatewayDevice.DeviceInfo.VendorConfigFile".split("\\.");
    }

    public String getLineStatus() {
        return (dslstats == null) ? null : dslstats.getStatus();
    }

    public String getModulationType() {
        return (dslstats == null) ? null : dslstats.getModulationType();
    }

    public Integer getDownstreamAttenuation() {
        return (dslstats == null) ? null : dslstats.getDownstreamAttenuation();
    }

    public Integer getDownstreamCurrRate() {
        return (dslstats == null) ? null : dslstats.getDownstreamCurrRate();
    }

    public Integer getDownstreamMaxRate() {
        return (dslstats == null) ? null : dslstats.getDownstreamMaxRate();
    }

    public Integer getDownstreamNoiseMargin() {
        return (dslstats == null) ? null : dslstats.getDownstreamNoiseMargin();
    }

    public Integer getDownstreamPower() {
        return (dslstats == null) ? null : dslstats.getDownstreamPower();
    }

    public Integer getUpstreamAttenuation() {
        return (dslstats == null) ? null : dslstats.getUpstreamAttenuation();
    }

    public Integer getUpstreamCurrRate() {
        return (dslstats == null) ? null : dslstats.getUpstreamCurrRate();
    }

    public Integer getUpstreamMaxRate() {
        return (dslstats == null) ? null : dslstats.getUpstreamMaxRate();
    }

    public Integer getUpstreamNoiseMargin() {
        return (dslstats == null) ? null : dslstats.getUpstreamNoiseMargin();
    }

    public Integer getUpstreamPower() {
        return (dslstats == null) ? null : dslstats.getUpstreamPower();
    }
    protected String profileName;

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
    private Hashtable<String, String> AtmErrorStatsCurrentDay;
    private Hashtable<String, String> AtmErrorStatsQuaterHour;
    private Hashtable<String, String> AtmErrorStatsTotal;
    private Hashtable<String, String> AtmErrorStatsShowTime;
    private Hashtable<String, String> AtmErrorStatsLastShowTime;

    private Hashtable<String, String> getAtmErrorStats(Hashtable<String, String> m, int type) {
        if (m == null) {
            HostsLocal h;
            try {
                h = Ejb.lookupHostsBean().findByHwidAndSn(hwid, sn);
                ATMErrorsStatsLocal s = Ejb.lookupATMErrorsStatsBean().findByPrimaryKey(new ATMErrorsStatsPK((Integer) h.getId(), h.getLastcontact(), type));
                m = ejb2map(s);
                m.remove("hostid");
                m.remove("time");
                m.remove("type");
            } catch (FinderException ex) {
                m = new Hashtable<String, String>();
            }

        }
        return m;
    }

    public Hashtable<String, String> getAtmErrorStatsShowTime() {
        return AtmErrorStatsShowTime = getAtmErrorStats(AtmErrorStatsShowTime, ATMErrorsStatsLocal.TYPE_SHOWTIME);
    }

    public Hashtable<String, String> getAtmErrorStatsLastShowTime() {
        return AtmErrorStatsLastShowTime = getAtmErrorStats(AtmErrorStatsLastShowTime, ATMErrorsStatsLocal.TYPE_LASTSHOWTIME);
    }

    public Hashtable<String, String> getAtmErrorStatsQuarterHour() {
        return AtmErrorStatsQuaterHour = getAtmErrorStats(AtmErrorStatsQuaterHour, ATMErrorsStatsLocal.TYPE_QUARTERHOUR);
    }

    public Hashtable<String, String> getAtmErrorStatsCurrentDay() {
        return AtmErrorStatsCurrentDay = getAtmErrorStats(AtmErrorStatsCurrentDay, ATMErrorsStatsLocal.TYPE_CURRENTDAY);
    }

    public Hashtable<String, String> getAtmErrorStatsTotal() {
        return AtmErrorStatsTotal = getAtmErrorStats(AtmErrorStatsTotal, ATMErrorsStatsLocal.TYPE_TOTAL);
    }
    /*
    private void drawGrid(Graphics2D g2d, int x, int y, int width, int height, int countGridX, int countGridY) {
    
    g2d.setPaint(Color.BLACK);
    g2d.drawRect(x, y, width - 1, height - 1);
    for (int cx = 1; cx < countGridX; cx++) {
    int xpos = width * cx / countGridX;
    g2d.drawLine(xpos, y, xpos, y + height);
    }
    for (int cy = 1; cy < countGridY; cy++) {
    int ypos = height * cy / countGridY;
    g2d.drawLine(x, ypos, x + width, ypos);
    }
    
    }
    
    private int max(int i1, int i2) {
    return (i1 > i2) ? i1 : i2;
    }
    
    private int min(int i1, int i2) {
    return (i1 < i2) ? i1 : i2;
    }
    
    private int getScaledValue(int min, int max, int value, int height) {
    int r = (int) ((value - min) * height / (max - min));
    //        System.out.println ("y="+r+"value="+value+" ("+min+","+max+") height="+height);
    return r;
    }
    
    private int getScaledPos(long min, long max, long value, int width) {
    int r = (int) ((value - min) * width / (max - min));
    //System.out.println ("x="+r+"value="+value+" ("+min+","+max+") width="+width);
    return r;
    }
     */

    private void getData(String n1, String n2, Timestamp tf, Timestamp tt, int[] y1, int[] y2) {

        for (int ix = 0; ix < y1.length; ix++) {
            y1[ix] = y2[ix] = Integer.MIN_VALUE;
        }
        try {
            double ts = ((double) (y1.length - 2)) / (tt.getTime() - tf.getTime());
            Method m1 = DSLStatsLocal.class.getMethod("get" + n1);
            Method m2 = DSLStatsLocal.class.getMethod("get" + n2);
            for (DSLStatsLocal d : getDslStats()) {
                Object v1 = m1.invoke(d);
                Object v2 = m2.invoke(d);
                int i = (int) ((d.getTime().getTime() - tf.getTime()) * ts + 1);
                if (v1 != null) {
                    y1[i] = (Integer) v1;
                }
                if (v2 != null) {
                    y2[i] = (Integer) v2;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(HostsBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void lineParamsGraphCurrentRate(Graphics2D g2d, Object obj) {
        lineParamsGraph(g2d, obj, "DownstreamCurrRate", "UpstreamCurrRate", 1, 1);
    }

    public void lineParamsGraphMaxRate(Graphics2D g2d, Object obj) {
        lineParamsGraph(g2d, obj, "DownstreamMaxRate", "UpstreamMaxRate", 1, 1);
    }

    public void lineParamsGraphPower(Graphics2D g2d, Object obj) {
        lineParamsGraph(g2d, obj, "DownstreamPower", "UpstreamPower", 0.1, 0.1);
    }

    public void lineParamsGraphAttenuation(Graphics2D g2d, Object obj) {
        lineParamsGraph(g2d, obj, "DownstreamAttenuation", "UpstreamAttenuation", 0.1, 0.1);
    }

    public void lineParamsGraphNoiseMargin(Graphics2D g2d, Object obj) {
        lineParamsGraph(g2d, obj, "DownstreamNoiseMargin", "UpstreamNoiseMargin", 0.1, 0.1);
    }

    public void lineParamsGraph(Graphics2D g2d, Object obj, String n1, String n2, double scalex, double scaley) {
        int[] y1 = new int[502];
        int[] y2 = new int[502];

        Timestamp tf = new Timestamp(timeFrom.getTime());
        Timestamp tt = new Timestamp(timeTo.getTime());
        getData(n1, n2, tf, tt, y1, y2);
        /*
        for (int i =1; i < y1.length - 1; i++) {
        y1 [i] = (int) (Math.sin(i / 50.0) * 500);
        y2 [i] = (int) (Math.cos(i / 50.0) * 500);
        }
         */
        Graph g = new Graph();
        g.graph(g2d, y1, y2, tf, tt, scalex, scaley);

    }

    public DeviceProfileBean getProfile() {
        DeviceProfileBean prf = new DeviceProfileBean();
        prf.setName(profileName);
        prf.load();
        return prf;
    }

    public boolean getMultiInstance() {
        if (curcfg != null) {
            return curcfg.isMultiInstance();
        }
        return false;
    }
    private HostPropertySet props;

    public /*Map<String, Property>*/ Property[] getProperties() {

        System.out.print("HostsBean::getProperties id=" + id);
        /*
        for (Map.Entry<String,Property> p : props.getProperties().entrySet()) {
        System.out.print("HostsBean::getProperties "+p.getKey()+" "+p.getValue());
        }
        return props.getProperties();
         * */
        ArrayList<Property> a = new ArrayList<Property>(props.getProperties().size());
        for (Map.Entry<String, Property> p : props.getProperties().entrySet()) {
            if (!p.getValue().isHidden()) {
                a.add(p.getValue());
            }
            System.out.print("HostsBean::getProperties " + p.getKey() + " " + p.getValue());
        }

        return a.toArray(new Property[0]);
    }
    String propertyToRemove = "";

    public void setPropertyToRemove(String propertyToRemove) {
        this.propertyToRemove = propertyToRemove;
    }

    public String RemoveProperty() {
        props.Remove(propertyToRemove);
        return null;
    }
    String propname = "";

    public void setPropname(String propname) {
        this.propname = propname;
    }

    public String getPropname() {
        return this.propname;
    }
    String propvalue = "";

    public void setPropvalue(String propvalue) {
        this.propvalue = propvalue;
    }

    public String getPropvalue() {
        return this.propvalue;
    }

    public String AddProperty() {
        //props.Add(propname, propvalue);
//        propname = DataModel.getNormalizedName("", propname);
        DataModelValidationResult r = DataModelJsfBean.Validate(Parameter.getNormalizedName("", propname), propvalue);
        if (r.isOk()) {
            props.Add(new Property(Parameter.getNameWithoutRoot(propname), propvalue));
            if (r.getMessage() != null) {
                setWarningMessage(r.getMessage());
            }
        } else {
            if (r.getMessage() != null) {
                setErrorMessage(r.getMessage());
            } else {
                setErrorMessage("Uknown validation error");
            }
        }
        propname = propvalue = "";
        return null;
    }

    public String Remove() {
        try {
            Ejb.lookupHostsBean().remove(id);
        } catch (Exception ex) {
            setErrorMessage(ex.getMessage());
            return null;
        }
        setInfoMessage("Removed");
        hwid = null;
        sn = snIn = null;
        customerid = customerid_in = null;
        return "cperemoved";
    }
}
