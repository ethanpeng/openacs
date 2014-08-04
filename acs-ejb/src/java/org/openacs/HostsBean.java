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
package org.openacs;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.*;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openacs.utils.cvtHex;

public abstract class HostsBean implements EntityBean, HostsLocalBusiness {

    private EntityContext context;

    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click on the + sign on the left to edit the code.">
    // TODO Consider creating Transfer Object to encapsulate data
    // TODO Review finder methods
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(EntityContext aContext) {
        context = aContext;
    }

    /**
     * @see javax.ejb.EntityBean#ejbActivate()
     */
    public void ejbActivate() {
    }

    /**
     * @see javax.ejb.EntityBean#ejbPassivate()
     */
    public void ejbPassivate() {
    }

    /**
     * @see javax.ejb.EntityBean#ejbRemove()
     */
    public void ejbRemove() {
    }

    /**
     * @see javax.ejb.EntityBean#unsetEntityContext()
     */
    public void unsetEntityContext() {
        context = null;
    }

    /**
     * @see javax.ejb.EntityBean#ejbLoad()
     */
    public void ejbLoad() {
    }

    /**
     * @see javax.ejb.EntityBean#ejbStore()
     */
    public void ejbStore() {
    }
    // </editor-fold>

    public abstract String getSerialno();

    public abstract void setSerialno(String serialno);

    public abstract String getUrl();

    public abstract void setUrl(String url);

    public abstract String getConfigname();

    public abstract void setConfigname(String configname);

    public abstract String getCurrentsoftware();

    public abstract void setCurrentsoftware(String currentsoftware);

    public abstract Timestamp getSfwupdtime();

    public abstract void setSfwupdtime(Timestamp sfwupdtime);

    public abstract String getSfwupdres();

    public abstract void setSfwupdres(String sfwupdres);

    public abstract String getCfgupdres();

    public abstract void setCfgupdres(String cfgupdres);

    public abstract Timestamp getLastcontact();

    public abstract void setLastcontact(Timestamp lastcontact);

    public abstract Timestamp getCfgupdtime();

    public abstract void setCfgupdtime(Timestamp cfgupdtime);

    public abstract java.lang.String getHardware();

    public abstract void setHardware(java.lang.String hardware);

    public abstract java.lang.String getCfgversion();

    public abstract void setCfgversion(java.lang.String cfgversion);

    public abstract Object getId();

    public abstract void setId(Object id);

    public java.lang.Object ejbCreate(Integer hwid, String serialno, String url) throws CreateException {
        /*
        if (oui == null) {
        throw new CreateException("The field \"oui\" must not be null");
        }
        if (serialno == null) {
        throw new CreateException("The field \"serialno\" must not be null");
        }
        if (url == null) {
        throw new CreateException("The field \"url\" must not be null");
        }
         */
        // TODO add additional validation code, throw CreateException if data is not valid
        setHwid(hwid);
        setSerialno(serialno);
        setUrl(url);

        Timestamp never = new Timestamp(24 * 3600 * 1000);
        setCfgupdtime(never);
        setSfwupdtime(never);
        setLastcontact(never);
        setReboot(false);
        return null;
    }

    public void ejbPostCreate(Integer hwid, String serialno, String url) {
        // TODO populate relationships here if appropriate
    }

    public abstract byte[] getProps();

    public abstract void setProps(byte[] props);

    public abstract Integer getHwid();

    public abstract void setHwid(Integer hwid);

    public abstract String getUsername();

    public abstract void setUsername(String username);

    public abstract String getPassword();

    public abstract void setPassword(String password);

    public abstract Integer getAuthtype();

    public abstract void setAuthtype(Integer authtype);

    public abstract HardwareModelLocal getModel();

    public abstract void setModel(HardwareModelLocal m);

    public abstract String getCustomerid();

    public abstract void setCustomerid(String customerid);

    public abstract String getConrequser();

    public abstract void setConrequser(String conrequser);

    public abstract String getConreqpass();

    public abstract void setConreqpass(String conerqpass);

    /*
    public void RequestConnection (int timeout) throws Exception {
    String url = getUrl();
    String user = getConrequser();
    String pass = getConreqpass();
    
    if (user != null && pass != null) {
    url = url.substring(0, 7) + user +":" + pass + "@" + url.substring(7);
    }
    
    try {
    //                URL url = new URL((user!=null && pass != null) ? cpeurl);
    URL u = new URL(url);
    URLConnection httpconn = u.openConnection();
    httpconn.setReadTimeout(timeout);
    httpconn.setConnectTimeout(timeout);
    httpconn.setUseCaches(false);
    httpconn.getContent();
    } catch (Exception ex) {
    if (!ex.getMessage().equals("no content-type")) throw ex;
    return;
    }
    }
     */
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    public void RequestConnectionUDP(String url, String user, String pass) throws Exception {
        DatagramSocket s = new DatagramSocket(null);
        s.setReuseAddress(true);
        s.bind(new InetSocketAddress(Application.getSTUNport()));
        String ts = Long.toString(Calendar.getInstance().getTimeInMillis());
        String id = ts;
        Random rnd = new Random();
        byte[] nonceArray = new byte[16];
        rnd.nextBytes(nonceArray);

        String cn = cvtHex.cvtHex(nonceArray);
        url = url.substring(6);
        String[] u = url.split(":");

        SecretKeySpec signingKey = new SecretKeySpec(pass.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        String data = ts + id + user + cn;
        byte[] rawHmac = mac.doFinal(data.getBytes());
        String signature = cvtHex.cvtHex(rawHmac);
        String req = "GET http://" + url + "?ts=" + ts + "&id=" + id + "&un=" + user + "&cn=" + cn + "&sig=" + signature + " HTTP/1.1\r\n\r\n";

        byte[] breq = req.getBytes();
        DatagramPacket packet = new DatagramPacket(breq, breq.length);
        packet.setAddress(InetAddress.getByName(u[0]));
        packet.setPort(Integer.parseInt(u[1]));
        s.send(packet);
    }

    public void RequestConnectionHttp(String url, String user, String pass) throws Exception {
        HttpClient client = new HttpClient();
        if (user != null && pass != null) {
            client.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, pass));
        }
        GetMethod get = new GetMethod(url);
        get.setDoAuthentication(true);

        try {
            int status = client.executeMethod(get);
            if (status != 200) {
                System.out.println(status + "\n" + get.getResponseBodyAsString());
                throw new Exception("Failed: status=" + status);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            get.releaseConnection();
        }
    }

    public void RequestConnection(int timeout) throws Exception {
        String url = getUrl();
        String user = getConrequser();
        String pass = getConreqpass();

        if (url.startsWith("udp://")) {
            RequestConnectionUDP(url, user, pass);
        } else {
            RequestConnectionHttp(url, user, pass);
        }
    }

    public abstract Boolean getCfgforce();

    public abstract void setCfgforce(Boolean cfgforce);

    public abstract String getProfileName();

    public abstract void setProfileName(String profileName);

    public abstract Collection<HostPropertyLocal> getProperties();

    public abstract void setProperties(Collection<HostPropertyLocal> properties);

    public abstract DeviceProfileLocal getProfile();

    public abstract void setProfile(DeviceProfileLocal profile);

    public abstract Collection<Host2ServiceLocal> getServices();

    public abstract void setServices(Collection<Host2ServiceLocal> svcs);

    public abstract Boolean getForcePasswords();

    public abstract void setForcePasswords(Boolean forcePasswords);

    public abstract Boolean getReboot();

    public abstract void setReboot(Boolean reboot);
}
