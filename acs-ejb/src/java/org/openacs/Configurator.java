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

import org.openacs.datamodel.Parameter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import org.openacs.datamodel.Type;
import org.openacs.js.Script;
import org.openacs.message.AddObject;
import org.openacs.message.AddObjectResponse;
import org.openacs.message.DeleteObject;
import org.openacs.message.Download;
import org.openacs.message.Fault;
import org.openacs.message.GetParameterNames;
import org.openacs.message.GetParameterNamesResponse;
import org.openacs.message.GetParameterValues;
import org.openacs.message.GetParameterValuesResponse;
import org.openacs.message.GetRPCMethods;
import org.openacs.message.GetRPCMethodsResponse;
import org.openacs.message.Inform;
import org.openacs.message.Reboot;
import org.openacs.message.SetParameterValues;
import org.openacs.message.SetParameterValuesResponse;
import org.openacs.message.TransferComplete;
import org.openacs.message.X_00000C_SetConfiguration;
import org.openacs.utils.Ejb;
import org.openacs.utils.Version;
import org.openacs.vendors.Vendor;

public class Configurator extends Thread implements ICpe {

    protected static final int MIN_CALL_TIMEOUT = 15;
    protected static final int DEFAULT_CALL_TIMEOUT = 60 * 2;
    protected static final int MAX_CALL_TIMEOUT = 60 * 10;
    protected static final String DSL_IFC_CFG = ".WANDevice.1.WANDSLInterfaceConfig.";
    protected long timeout = 180;
    private Inform lastInform;
    private HostsLocal host;
    private DeviceProfileLocal deviceProfile;
    private ArrayList<TransferComplete> transferComplete;
    private String fwpath;
    private String urlServer;
    //private CPELocal cpe;
    private String paramConfigVersion;
    private String KEY_SOFTWARE = "dlSoftware";
    private String KEY_CONFIG = "dlConfig";
    private String paramConfigName;
    private Object hostid;
    private String sessionid;
    //private static final String OUI_Thomson = "00147F";
    //private static final String OUI_Thomson2 = "0090D0";

    public Configurator(Inform lastInform, Object hostid, ArrayList<TransferComplete> transferComplete, String fwpath, String urlServer, String sessionid) {
        this.lastInform = lastInform;
        this.hostid = hostid;
        this.transferComplete = transferComplete;
        this.fwpath = fwpath;
        this.urlServer = urlServer;
        //this.cpe = Ejb.lookupCPEBean();
        String vcf = lastInform.getRoot() + ".DeviceInfo.VendorConfigFile.1.";
        this.paramConfigVersion = vcf + "Version";
        this.paramConfigName = vcf + "Name";
        this.sessionid = sessionid;
    }

    private Message Call(Message request, long timeout) {
        return Call(host, request, timeout);
    }

    public boolean testing() {
        /*
        GetRPCMethods grpcm = new GetRPCMethods();
        Message m = Call(grpcm, 30);
        if (m instanceof GetRPCMethodsResponse) {
        GetRPCMethodsResponse gpnr = (GetRPCMethodsResponse) m;
        for (String method : gpnr.methods) {
        System.out.println("Method: " + method);
        }
        }
        X_00000C_ShowStatus sm = new X_00000C_ShowStatus();
        //sm.addCommand("show cwmp map");
        //sm.addCommand("show cwmp parameter all");
        //sm.addCommand("show cwmp persistent");
        sm.addCommand("show cwmp session");
        m = Call(sm, 30);
         */
        X_00000C_SetConfiguration sc = new X_00000C_SetConfiguration();
        sc.addCommand("clock timezone CST 8");
//        sc.addCommand(" enable");
        //       sc.addCommand("!");
        Call(sc, 30);
        /*
        String b = lastInform.getRoot() + ".ManagementServer.";
        String pii = b + "PeriodicInformInterval";
        String[] n = new String[]{            pii        };
        GetParameterValues gpv = new GetParameterValues(n);
        m = Call(gpv, 30);
        System.out.println("Getting Periodic inform ");
        if (m instanceof GetParameterValuesResponse) {
        GetParameterValuesResponse gpvr = (GetParameterValuesResponse) m;
        int c = gpvr.getParamInt(pii);
        System.out.println("Periodic int" + c);
        
        SetParameterValues spv = new SetParameterValues();
        spv.AddValue(pii, c);
        Message m2 = Call(spv, 30);
        }
         */
        /*
        Upload up = new Upload();
        up.CommandKey="ck";
        up.FileType = Upload.FT_LOG;
        up.URL = "http://78.60.200.151:8080/openacs/upload";
        
        Call(up, 30);
         */
        return true;
    }
    private Logger logger = Logger.getLogger(Configurator.class.getName());

    private void log(Level level, String msg) {
        if (lastInform != null) {
            logger.log(level, lastInform.getOui() + ":" + lastInform.sn + " " + msg);
        } else {
            logger.log(level, msg);
        }
    }

    private void log(Level level, String msg, Throwable ex) {
        if (lastInform != null) {
            logger.log(level, lastInform.getOui() + ":" + lastInform.sn + " " + msg, ex);
        } else {
            logger.log(level, msg, ex);
        }
    }

    @Override
    public void run() {
        try {
            log(Level.INFO, "Configurator::run");
            try {
                host = Ejb.lookupHostsBean().findByPrimaryKey(hostid);
                if (host.getForcePasswords() == null) {
                    host.setForcePasswords(false);
                }
            } catch (FinderException ex) {
                log(Level.SEVERE, null, ex);
                return;
            }

            if (host.getReboot() != null && host.getReboot()) {
                if (!(lastInform.isEvent(Inform.EVENT_BOOT) || lastInform.isEvent(Inform.EVENT_BOOT_STRAP))) {
                    log(Level.INFO, "force reboot");
                    Message m = Call(new Reboot("forced_reboot"), DEFAULT_CALL_TIMEOUT);
                    host.setReboot(false);
                    return;
                } else {
                    host.setReboot(false);
                }
            }
            HardwareModelLocal hw = host.getModel();
            if (host.getProfileName() == null) {
                host.setProfileName("Default");
            }
            try {
                deviceProfile = Ejb.lookupDeviceProfileBean().findByPrimaryKey(host.getProfileName());
            } catch (FinderException ex) {
                log(Level.SEVERE, "Device profile '" + host.getProfileName() + "' not found.", ex);
            }
            if (SyncCwmpVariables(host.getForcePasswords()) && host.getForcePasswords()) {
                host.setForcePasswords(false);
            }

            log(Level.INFO, "Backup cwmp tree");
            BackupLocalHome bckHome = Ejb.lookupBackupBean();
            Timestamp tsBackup = null;
            try {
                tsBackup = bckHome.getTimeOfLastBackup((Integer) host.getId());
            } catch (FinderException ex) {
                log(Level.INFO, "No cfg backup found.");
            }
            onTransferComplete();


            if (!lastInform.isEvent(Inform.EVENT_CONNECTION_REQUEST)) {
                if (deviceProfile != null
                        && (lastInform.isEvent(Inform.EVENT_PERIODIC) || lastInform.isEvent(Inform.EVENT_BOOT) || lastInform.isEvent(Inform.EVENT_BOOT_STRAP))) {
                    updateSoftware();
                    Vendor vendor = Vendor.getVendor(lastInform.getOui(), hw.getHclass(), hw.getVersion());
                    switch (vendor.getConfigUpdateMethod()) {
                        case Vendor.CFG_UPDATE_METHOD_2:
                            System.out.println("Configurator: UPDATE by name/version");
                            updateConfigThomson();
                            break;
                        case Vendor.CFG_UPDATE_METHOD_1:
                        default:
                            System.out.println("Configurator: UPDATE ProvisioningCode");
                            updateConfigByProvisioningCode();
                            break;
                    }
                    if (lastInform.isEvent(Inform.EVENT_PERIODIC)) {
                        CheckParameters();
                        RemoveDSLStats();
                        RemoveATMErrorStats();
                        Integer d = deviceProfile.getDayskeepstats();
                        if (d != null && d > 0) {
                            SaveDSLStats();
                            SaveATMErrorsStats();
                        }
                        Integer i = deviceProfile.getSaveParamValuesInterval();
                        if (i == null) {
                            i = 0;
                        }
                        //System.out.println("getSaveParamValuesInterval: " + i);
                        //System.out.println("tsBackup: " + tsBackup);
                        //System.out.println ("now: "+Calendar.getInstance().getTimeInMillis()+ " tsBackup "+tsBackup.getTime());
                        if (i > 0 && (tsBackup == null || (Calendar.getInstance().getTimeInMillis() - tsBackup.getTime() > 3600 * 1000 * i))) {
                            //System.out.println("Do backup");
                            // cache parameters for browsing
                            BackupCWMPTree(bckHome, tsBackup);
                        }
                        GetSoftwareDetails();
                        UpdateVoiceCaps();
                    }
                } else if (deviceProfile != null && (lastInform.isEvent(Inform.EVENT_VALUE_CHANGE) || lastInform.isEvent(Inform.EVENT_BOOT))) {
                    CheckParameters();
                    // cache parameter values for browsing
                    if ((lastInform.isEvent(Inform.EVENT_VALUE_CHANGE) && deviceProfile.getSaveParamValuesOnChange()) || (lastInform.isEvent(Inform.EVENT_BOOT) && deviceProfile.getSaveParamValuesOnBoot())) {
                        BackupCWMPTree(bckHome, tsBackup);
                    }
                }
            } else {
                CheckParameters();
            }
        } catch (Exception e) {
            log(Level.SEVERE, e.getMessage());
        }

        try {
            String sn = deviceProfile.getScriptname();
            if (sn == null) {
                deviceProfile.setScriptname("Default");
                sn = "Default";
            }
            runScript(sn, (transferComplete.size() > 0) ? transferComplete.get(0) : null);
        } catch (Exception e) {
            log(Level.SEVERE, e.getMessage());
        }
    }

    protected void GetSoftwareDetails() {
        SoftwareDetailLocalHome sdh = Ejb.lookupSoftwareDetailBean();
        try {
            SoftwareDetailLocal sdl = sdh.findByPrimaryKey(new SoftwareDetailPK(host.getHwid(), host.getCurrentsoftware()));
            return;
        } catch (FinderException ex) {
        }

        ByteArrayOutputStream streamParamNames = null;
        GetParameterNames gpn = new GetParameterNames(lastInform.getRoot() + ".", false);
        Message m = Call(gpn, timeout);
        if (m instanceof GetParameterNamesResponse) {
            GetParameterNamesResponse gpnr = (GetParameterNamesResponse) m;
            Properties paramNames = new Properties();
            paramNames.putAll(gpnr.names);
            streamParamNames = new ByteArrayOutputStream(1024 * 1024);
            try {
//                GZIPOutputStream gz = new GZIPOutputStream(os);
                paramNames.store(streamParamNames, null);
            } catch (IOException ex) {
                log(Level.SEVERE, null, ex);
                return;
            }
        } else {
            log(Level.WARNING, "Failed get software parameter names: " + m.toString());
        }

        ByteArrayOutputStream streamMethods = null;
        GetRPCMethods grpcm = new GetRPCMethods();
        m = Call(grpcm, 30);
        if (m instanceof GetRPCMethodsResponse) {
            GetRPCMethodsResponse gpnr = (GetRPCMethodsResponse) m;
            Properties methods = new Properties();
            for (String method : gpnr.methods) {
                methods.put(method, "y");
            }
            streamMethods = new ByteArrayOutputStream(1024);
            try {
//                GZIPOutputStream gz = new GZIPOutputStream(os);
                methods.store(streamMethods, null);
            } catch (IOException ex) {
                log(Level.SEVERE, null, ex);
                return;
            }
        } else {
            log(Level.WARNING, "Failed get software RPC methods: " + m.toString());
        }

        if (streamMethods != null && streamParamNames != null) {
            try {
                SoftwareLocalHome slh = Ejb.lookupSoftwareBean();
                try {
                    SoftwareLocal sl = slh.findByPrimaryKey(new SoftwarePK(host.getHwid(), host.getCurrentsoftware()));
                } catch (FinderException ex1) {
                    try {
                        slh.create(host.getHwid(), host.getCurrentsoftware(), "", "");
                    } catch (CreateException ex2) {
                        log(Level.WARNING, "Failed to insert software", ex2);
                        return;
                    }
                }
                sdh.create(host.getHwid(), host.getCurrentsoftware(), streamParamNames.toByteArray(), streamMethods.toByteArray());
            } catch (CreateException ex) {
                log(Level.WARNING, "Failed to insert software details", ex);
            }
        }
    }

    public void BackupCWMPTree() {
        BackupLocalHome bckHome = Ejb.lookupBackupBean();
        Timestamp tsBackup = null;
        try {
            tsBackup = bckHome.getTimeOfLastBackup((Integer) host.getId());
        } catch (FinderException ex) {
        }
        BackupCWMPTree(bckHome, tsBackup);
    }

    protected void BackupCWMPTree(BackupLocalHome bckHome, Timestamp time) {
        Properties p = new Properties();
        BackupCWMPTree(bckHome, time, lastInform.getRoot() + ".", p);

        ByteArrayOutputStream os = new ByteArrayOutputStream(1024 * 1024);
        try {
//                GZIPOutputStream gz = new GZIPOutputStream(os);
            p.store(os, null);
        } catch (IOException ex) {
            log(Level.SEVERE, null, ex);
            return;
        }
        if (time == null) {
            Timestamp ts = new Timestamp(Calendar.getInstance().getTimeInMillis());
            try {
                bckHome.create((Integer) host.getId(), BackupLocal.TYPE_VENDOR_INDEPENDANT, ts, os.toByteArray());
            } catch (CreateException ex) {
                log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                BackupLocal b = bckHome.findByPrimaryKey(new BackupPK((Integer) host.getId(), time, BackupLocal.TYPE_VENDOR_INDEPENDANT));
                b.setCfg(os.toByteArray());
            } catch (FinderException ex) {
                log(Level.SEVERE, null, ex);
            }
        }
    }

    protected void BackupCWMPTree(BackupLocalHome bckHome, Timestamp time, String root, Properties p) {
        try {
            GetParameterNames gpn = new GetParameterNames(root, true);
            Message m = Call(gpn, timeout);
            if (m instanceof GetParameterNamesResponse) {
                GetParameterNamesResponse gpnr = (GetParameterNamesResponse) m;
                for (String name : gpnr.names.keySet()) {
                    log(Level.INFO, "Saving CWMP vars for +'" + name);
                    GetParameterValues gpv = new GetParameterValues(name);
                    m = Call(gpv, timeout);
                    if (m instanceof GetParameterValuesResponse) {
                        GetParameterValuesResponse gpvr = (GetParameterValuesResponse) m;
                        p.putAll(gpvr.values);
                    } else {
                        log(Level.WARNING, "Failed save CWMP vars for +'" + name + "': " + m.toString());
                        Fault f = (Fault) m;
                        if (f.getCwmpFaultCode().equals(Fault.FCODE_INTERNAL)) {
                            BackupCWMPTree(bckHome, time, name, p);
                        }
                    }
                }

            } else {
                log(Level.WARNING, "Failed save CWMP vars: " + m.toString());
            }
        } catch (Exception ex) {
            log(Level.WARNING, "Failed save CWMP vars: " + ex.getMessage());
        }
    }

    protected void updateConfigThomson() {

        ConfigurationLocalHome cs = Ejb.lookupConfigurationBean();
        try {
            String vl = "";
            GetParameterValues gpv = new GetParameterValues(new String[]{paramConfigName, paramConfigVersion});
            Message m = Call(gpv, DEFAULT_CALL_TIMEOUT);
            if (m instanceof GetParameterValuesResponse) {
                GetParameterValuesResponse gpvr = (GetParameterValuesResponse) m;
                vl = gpvr.values.get(paramConfigVersion);
                String nl = gpvr.values.get(paramConfigName);
                log(Level.INFO, "Got cfg version: " + vl + " name=" + nl);
                host.setCfgversion(vl);
            } else {
                log(Level.SEVERE, m.toString());
            }


            Version vh = new Version(vl);
            if (host.getConfigname() != null) {
                System.out.println("Find config: hwid=" + host.getHwid() + " name=" + host.getConfigname());
                ConfigurationLocal cfg = cs.findByPrimaryKey(new ConfigurationPK(host.getHwid(), host.getConfigname()));
                System.out.println("Found config version: " + cfg.getVersion());
                Version vc = new Version(cfg.getVersion());
                Boolean force = host.getCfgforce();
                if (force == null) {
                    force = false;
                }
                if (force || !vc.isUptodate(vh)) {
//                    DownloadConfig(cfg, null);
                    DownloadConfig(cfg, getProvisioningCode(host.getConfigname(), cfg.getVersion()));
                }
            }
        } catch (FinderException e) {
            host.setCfgupdres(e.getMessage());
            host.setCfgupdtime(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
            log(Level.WARNING, "Config lookup: " + e.getMessage());
        } catch (Exception e) {
            log(Level.SEVERE, e.getMessage());
        }
    }

    protected void DownloadConfig(ConfigurationLocal cfg, String key) {
        log(Level.INFO, "Update config '" + cfg.getName() + "' to " + cfg.getVersion() + " key=" + key);
        host.setCfgupdtime(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
        String urlConfig = urlServer + "/cfg";
        Download d = new Download(KEY_CONFIG + ((key != null) ? ("." + key) : ""), urlConfig, Download.FT_CONFIG);
        d.TargetFileName = cfg.getFilename();
        d.FileSize = cfg.getConfig().length;
        Call(d, timeout);
    }

    protected void updateConfigByProvisioningCode() {
        ConfigurationLocalHome cs = Ejb.lookupConfigurationBean();
        try {
            String cfgname = host.getConfigname();
            if (cfgname != null) {
                ConfigurationLocal cfg = cs.findByPrimaryKey(new ConfigurationPK(host.getHwid(), host.getConfigname()));
                String version = cfg.getVersion();

                if (version == null) {
                    version = "";
                }

                Boolean force = host.getCfgforce();
                if (force == null) {
                    force = false;
                }
                System.out.println("ProvisioningCode=" + lastInform.getProvisiongCode());
//                if (!version.equals(lastInform.getProvisiongCode())) {
                String pc = getProvisioningCode(cfgname, version);
                System.out.println("ProvisioningCode (new)=" + pc);
                if (!pc.equals(lastInform.getProvisiongCode()) || force) {
                    DownloadConfig(cfg, pc);
                }
            }
        } catch (FinderException e) {
            host.setCfgupdres(e.getMessage());
            host.setCfgupdtime(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
            log(Level.WARNING, "Config lookup: " + e.getMessage());
        }

    }

    private boolean checkImage(String name) {
        if (name == null || name.equals("")) {
            return false;
        }

        String filename = Application.getFirmwarePath();
        if (!(filename.endsWith("\\") || filename.endsWith("/"))) {
            filename += "/";
        }
        filename += name;
        File f = new File(filename);
        if (!f.exists()) {
            log(Level.WARNING, "File not present for firmware version " + filename);
            return false;
        }
        if (!f.canRead()) {
            log(Level.WARNING, "File not readable for firmware version " + filename);
            return false;
        }
        return true;
    }

    protected void updateSoftware() {
        SoftwareLocalHome sh = Ejb.lookupSoftwareBean();
        String vl = lastInform.getSoftwareVersion();

        SoftwareLocal swUpdate = null;
        DeviceProfile2SoftwareLocalHome dp2slh = Ejb.lookupDeviceProfile2SoftwareBean();
        try {
            DeviceProfile2SoftwareLocal dp2sl = dp2slh.findByProfileNameAndHwid(deviceProfile.getName(), host.getHwid());
            String versionProfile = dp2sl.getVersion();
//            System.out.println ("Profile sets firmware version to "+dp2sl.getVersion());
            if (versionProfile == null || versionProfile.equals(DeviceProfile2SoftwareLocal.NOUPDATE)) {
                log(Level.INFO, "Update action set to NOUPDATE");
                return;
            }
            if (!versionProfile.equals(DeviceProfile2SoftwareLocal.AUTOUPDATE)) {

                //swUpdate = dp2sl.getFirmware();
                swUpdate = sh.findByPrimaryKey(new SoftwarePK(dp2sl.getHwid(), dp2sl.getVersion()));
                /*
                if (swUpdate.getFilename() == null || swUpdate.getFilename().equals("")) {
                swUpdate = null;
                log(Level.WARNING, "File not present for firmware version " + swUpdate.getVersion());
                return;
                }
                 */
                if (!checkImage(swUpdate.getFilename())) {
                    return;
                }

                if (swUpdate.getVersion().equals(vl)) {
                    // we are up to date
                    return;
                }
                log(Level.INFO, "Updating firmware to " + swUpdate.getVersion());
            }

        } catch (FinderException ex) {
            //log(Level.SEVERE, null, ex);
        }
        Version CurrentVersion = new Version(vl);
        try {
            if (swUpdate == null) {
                Iterator<SoftwareLocal> itSoftware = sh.findByHardware(host.getHwid()).iterator();
                while (itSoftware.hasNext()) {
                    SoftwareLocal sw = (SoftwareLocal) itSoftware.next();
                    System.out.println("v=" + sw.getVersion());
                    /*
                    if (sw.getFilename() == null) {
                    log(Level.INFO, "Update software: version " + sw.getVersion() + "is not considered as no image assigned.");
                    continue;
                    }
                     * 
                     */
                    if (!checkImage(sw.getFilename())) {
                        continue;
                    }
                    Version v2 = new Version(sw.getVersion());
                    if (!v2.isUptodate(CurrentVersion)) {
                        Version v = null;
                        if (swUpdate != null) {
                            if (!v2.isUptodate(v = new Version(swUpdate.getVersion()))) {
                                //                        if (!(v = new Version(swUpdate.getVersion())).isUptodate(v2)) {
                                swUpdate = sw;
                                //System.out.println ("Yet newer soft found");
                            }
                        } else {
                            //System.out.println ("Update software");
                            swUpdate = sw;
                        }
                    }
                }
            }
            System.out.println("swUpdate=" + swUpdate);
            if (swUpdate != null && !swUpdate.getVersion().equalsIgnoreCase(lastInform.getSoftwareVersion())) {
                log(Level.INFO, "Update software to " + swUpdate.getVersion() + /*" hw=" + swUpdate.getHardware() +*/ " size=" + swUpdate.getSize());
                host.setSfwupdtime(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));

                String url = urlServer + "/firmware/" + swUpdate.getFilename();
                File f = new File(fwpath + swUpdate.getFilename());
                Download d = new Download(KEY_SOFTWARE, url, Download.FT_FIRMWARE);
//                    d.TargetFileName = swUpdate.getFileName ();
                d.FileSize = swUpdate.getSize();
                d.FileSize = f.length();
                log(Level.INFO, "Update software to " + swUpdate.getVersion() + /*" hw=" + swUpdate.getHardware() + */ " size=" + d.FileSize + " url=" + url);
                Call(d, timeout);
            }
        } catch (FinderException e) {
            log(Level.WARNING, "Software lookup: " + e.getMessage());
//                ex.printStackTrace();
        }
    }

    public static String getProvisioningCode(String name, String version) {
        return Integer.toString(name.hashCode(), 16) + "." + Integer.toString(version.hashCode(), 16);
//        return version;
    }

    private void runScript(String scriptName, TransferComplete tc) {

        try {
            ScriptLocal sb;
            sb = Ejb.lookupScriptBean(scriptName);
            Script script = new Script(lastInform, new String(sb.getScript()), host, tc, this, sessionid);
            script.run();
        } catch (FinderException ex) {
            log(Level.WARNING, "Configuration script '" + scriptName + "' not found in db.");
        }
    }

    private void onTransferComplete() {
        for (TransferComplete tc : transferComplete) {
            String result = (tc.FaultCode != 0) ? tc.FaultCode + ": " + tc.FaultString : "OK";
            if (tc.CommandKey.startsWith(KEY_CONFIG)) {
                host.setCfgupdres(result);

                if (tc.FaultCode == 0) {
                    host.setCfgforce(false);
                    int ix = tc.CommandKey.indexOf('.');
                    if (ix != -1) {
                        SetParameterValues spv = new SetParameterValues();
                        spv.key = "onTransferComplete";
                        String provisioningCode = tc.CommandKey.substring(ix + 1);
                        spv.AddValue(lastInform.getRoot() + ".DeviceInfo.ProvisioningCode", provisioningCode);
                        System.out.println("OnTransfercomplete: Set " + lastInform.getRoot() + ".DeviceInfo.ProvisioningCode -> " + tc.CommandKey.substring(ix + 1));
                        Call(spv, timeout);
                        lastInform.setProvisiongCode(provisioningCode);
                    }
                }

            } else if (tc.CommandKey.equals(KEY_SOFTWARE)) {
                host.setSfwupdres(result);
            } else {
                runScript("OnTransferComplete", tc);
            }
        }
    }

    public void SaveDSLStats() {
        DSLStatsLocalHome statsHome = Ejb.lookupDSLStatsBean();
        String b = lastInform.getRoot() + DSL_IFC_CFG;
        /*
        String[] n = new String[]{
        b + "DownstreamAttenuation",
        b + "DownstreamCurrRate",
        b + "DownstreamMaxRate",
        b + "DownstreamNoiseMargin",
        b + "DownstreamPower",
        b + "UpstreamAttenuation",
        b + "UpstreamCurrRate",
        b + "UpstreamMaxRate",
        b + "UpstreamNoiseMargin",
        b + "UpstreamPower"
        };
         */
        GetParameterValues gpv = new GetParameterValues(b);
        Message m = Call(gpv, DEFAULT_CALL_TIMEOUT);
        if (m instanceof GetParameterValuesResponse) {
            GetParameterValuesResponse gpvr = (GetParameterValuesResponse) m;
            try {
//                Timestamp t = new Timestamp(Calendar.getInstance().getTime().getTime());
                statsHome.create((Integer) this.host.getId(),
                        host.getLastcontact(),
                        gpvr.getParamInt(b + "DownstreamAttenuation"),
                        gpvr.getParamInt(b + "DownstreamCurrRate"),
                        gpvr.getParamInt(b + "DownstreamMaxRate"),
                        gpvr.getParamInt(b + "DownstreamNoiseMargin"),
                        gpvr.getParamInt(b + "DownstreamPower"),
                        gpvr.getParamInt(b + "UpstreamAttenuation"),
                        gpvr.getParamInt(b + "UpstreamCurrRate"),
                        gpvr.getParamInt(b + "UpstreamMaxRate"),
                        gpvr.getParamInt(b + "UpstreamNoiseMargin"),
                        gpvr.getParamInt(b + "UpstreamPower"),
                        gpvr.getParam(b + "Status"),
                        gpvr.getParam(b + "ModulationType"));
            } catch (CreateException ex) {
                log(Level.SEVERE, null, ex);
            }
        } else {
            log(Level.SEVERE, m.toString());
        }
    }

    private Long getStat(String b, String type, String name, Hashtable<String, String> v) {
        String fn = b + type + "." + name;
        String value = v.get(fn);

//        System.out.println ("getStat: "+fn+"="+value);
        if (value != null) {
            return Long.valueOf(value);
        } else if (type.equals("Showtime")) {
            return getStat(b, "ShowTime", name, v);  // fix for thomson misspell
        }
        return null;
    }

    public void SaveATMErrorsStats() {
        ATMErrorsStatsLocalHome statsHome = Ejb.lookupATMErrorsStatsBean();
        String b = lastInform.getRoot() + ".WANDevice.1.WANDSLInterfaceConfig.Stats.";
        //System.out.println("Get vars: " + b);
        String[] nms = {b, lastInform.getRoot() + DSL_IFC_CFG};
        GetParameterValues gpv = new GetParameterValues(nms);
        Message m = Call(gpv, DEFAULT_CALL_TIMEOUT);
        if (m instanceof GetParameterValuesResponse) {
//            System.out.println("Got vars: " + m);
            GetParameterValuesResponse gpvr = (GetParameterValuesResponse) m;
            String typenames[] = {
                "Showtime", "Total", "QuarterHour", "CurrentDay", "LastShowtime"
            };
            int types[] = {
                ATMErrorsStatsLocal.TYPE_SHOWTIME, ATMErrorsStatsLocal.TYPE_TOTAL, ATMErrorsStatsLocal.TYPE_QUARTERHOUR, ATMErrorsStatsLocal.TYPE_CURRENTDAY, ATMErrorsStatsLocal.TYPE_LASTSHOWTIME
            };
            for (int i = 0; i < typenames.length; i++) {
                String type = typenames[i];
//                System.out.println("Type name=" + typenames[i]);
                String is = gpvr.values.get(lastInform.getRoot() + DSL_IFC_CFG + type + "Start");
                /*
                boolean f = false;
                for (String n : ns) {
                //String name = b + typenames[i] + "." + n;
                //System.out.println("Is key? " + name);
                //if (gpvr.values.get(name) != null) {
                if (getStat(b, type, n, gpvr.values) != null) {
                f = true;
                break;
                }
                }
                System.out.println("f=" + f);
                
                if (f) {
                 */
                if (is != null) {
                    try {
                        String n = lastInform.getRoot() + DSL_IFC_CFG + type + "Start";
//                        System.out.println ("n="+n+" v="+is);
                        Timestamp intervalStart = new Timestamp(host.getLastcontact().getTime() - Long.valueOf(is) * 1000);
                        ATMErrorsStatsLocal s = statsHome.create((Integer) this.host.getId(), host.getLastcontact(), types[i],
                                intervalStart,
                                getStat(b, type, "ATUCCRCErrors", gpvr.values),
                                getStat(b, type, "ATUCFECErrors", gpvr.values),
                                getStat(b, type, "ATUCHECErrors", gpvr.values),
                                getStat(b, type, "CellDelin", gpvr.values),
                                getStat(b, type, "CRCErrors", gpvr.values),
                                getStat(b, type, "FECErrors", gpvr.values),
                                getStat(b, type, "HECErrors", gpvr.values),
                                getStat(b, type, "ErroredSecs", gpvr.values),
                                getStat(b, type, "InitErrors", gpvr.values),
                                getStat(b, type, "InitTimeouts", gpvr.values),
                                getStat(b, type, "LinkRetrain", gpvr.values),
                                getStat(b, type, "LossOfFraming", gpvr.values),
                                getStat(b, type, "ReceiveBlocks", gpvr.values),
                                getStat(b, type, "SeverelyErroredSecs", gpvr.values),
                                getStat(b, type, "TransmitBlocks", gpvr.values),
                                getStat(b, type, "X_000E50_LossOfPower", gpvr.values),
                                getStat(b, type, "X_000E50_LossOfSignal", gpvr.values));

                    } catch (CreateException ex) {
                        log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            log(Level.SEVERE, m.toString());
        }
    }

    private void RemoveDSLStats() {
        DSLStatsLocalHome statsHome = Ejb.lookupDSLStatsBean();
        try {
            long t = Calendar.getInstance().getTimeInMillis();
            if (deviceProfile.getDayskeepstats() != null && deviceProfile.getDayskeepstats() < 0) {
                deviceProfile.setDayskeepstats(0);
            }

            if (deviceProfile.getDayskeepstats() != null) {
                t -= 3600 * 24 * 1000 * deviceProfile.getDayskeepstats();
            } else {
                t -= 60 * 15 * 1000;
            }
            Timestamp ts = new Timestamp(t);
            //System.out.println ("Remove stats before: "+ts+" (keep for "+deviceProfile.getDayskeepstats()+" profile="+deviceProfile.getName());
            Collection<DSLStatsLocal> ss = statsHome.findByTimeBeforeAndHost((Integer) host.getId(), ts);
            for (DSLStatsLocal s : ss) {
                try {
                    //System.out.println ("Remove:"+s.getTime());
                    s.remove();
                } catch (RemoveException ex) {
                    log(Level.SEVERE, null, ex);
                } catch (EJBException ex) {
                    log(Level.SEVERE, null, ex);
                }
            }

        } catch (FinderException ex) {
            log(Level.SEVERE, null, ex);
        }

    }

    private void RemoveATMErrorStats() {
        ATMErrorsStatsLocalHome statsHome = Ejb.lookupATMErrorsStatsBean();
        try {
            long t = Calendar.getInstance().getTimeInMillis();
            if (deviceProfile.getDayskeepstats() != null && deviceProfile.getDayskeepstats() < 0) {
                deviceProfile.setDayskeepstats(0);
            }

            if (deviceProfile.getDayskeepstats() != null) {
                t -= 3600 * 24 * 1000 * deviceProfile.getDayskeepstats();
            } else {
                t -= 60 * 15 * 1000;
            }
            Timestamp ts = new Timestamp(t);
            //System.out.println ("Remove stats before: "+ts+" (keep for "+deviceProfile.getDayskeepstats()+" profile="+deviceProfile.getName());
            Collection<ATMErrorsStatsLocal> ss = statsHome.findByTimeBeforeAndHost((Integer) host.getId(), ts);
            for (ATMErrorsStatsLocal s : ss) {
                try {
                    //System.out.println ("Remove:"+s.getTime());
                    s.remove();
                } catch (RemoveException ex) {
                    log(Level.SEVERE, null, ex);
                } catch (EJBException ex) {
                    log(Level.SEVERE, null, ex);
                }
            }

        } catch (FinderException ex) {
            log(Level.SEVERE, null, ex);
        }

    }

    private void CheckParameters() {
        log(Level.INFO, "Configurator::CheckParameters");
        String b = lastInform.getRoot() + ".ManagementServer.";
        Properties parameters = new Properties();
        if (deviceProfile != null) {
            Integer i = deviceProfile.getInforminterval();
            if (i != null && i > 0) {
                parameters.put(b + "PeriodicInformInterval", i.toString());
                parameters.put(b + "PeriodicInformEnable", "1");
            }
        }

        SyncParameterValues(parameters);
    }

    private void _CheckParameters() {
        String b = lastInform.getRoot() + ".ManagementServer.";
        String pii = b + "PeriodicInformInterval";
        String[] n = new String[]{
            pii
        };
        GetParameterValues gpv = new GetParameterValues(n);
        Message m = Call(gpv, 30);
        System.out.println("Getting Periodic inform");

        if (m instanceof GetParameterValuesResponse) {
            GetParameterValuesResponse gpvr = (GetParameterValuesResponse) m;
            int c = gpvr.getParamInt(pii, 0);
            int t = deviceProfile.getInforminterval();
            System.out.println("Periodic inform int: " + c + " " + t);
            if (c != t) {
                System.out.println("Set Periodic inform to: " + t);

                SetParameterValues spv = new SetParameterValues();
                spv.AddValue(pii, t);
                Message m2 = Call(spv, 30);
                if (m2.isFault()) {
                    log(Level.SEVERE, m2.toString());
                } else {
                    System.out.println("Set success");
                }

            }
        } else {
            System.out.println("Getting Periodic inform FAULT " + m);
            log(Level.SEVERE, (m != null) ? m.toString() : "Getting Periodic inform timeout");
        }
    }

    private void CheckParameters(Map<String, String> parameters) {
        if (parameters.isEmpty()) {
            return;
        }

        GetParameterValues gpv = new GetParameterValues(parameters);
        Message m = Call(gpv, DEFAULT_CALL_TIMEOUT);
        if (m instanceof GetParameterValuesResponse) {
            GetParameterValuesResponse gpvr = (GetParameterValuesResponse) m;
            SetParameterValues spv = new SetParameterValues();
            for (Entry<String, String> e : parameters.entrySet()) {
                if (!gpvr.values.get(e.getKey()).equals(e.getValue())) {
                    spv.AddValue(e.getKey(), e.getValue());
                }
            }
            if (!spv.isEmpty()) {
                Message m2 = Call(spv, DEFAULT_CALL_TIMEOUT);
                if (m2.isFault()) {
                    System.out.println("Configurator::CheckParameters set values FAULT");
                    log(Level.SEVERE, m2.toString());
                } else {
                    System.out.println("Set success");
                }
            }
        } else {
            System.out.println("Configurator::CheckParameters get values FAULT");
            log(Level.SEVERE, m.toString());
        }
    }

    private void UpdateVoiceCaps() {
        SoftwareDetailLocalHome sdh = Ejb.lookupSoftwareDetailBean();
        SoftwareDetailLocal sdl = null;
        try {
            sdl = sdh.findByPrimaryKey(new SoftwareDetailPK(host.getHwid(), host.getCurrentsoftware()));
        } catch (FinderException ex) {
            return;
        }

        if (sdl == null || sdl.getVoicecaps() != null) {
            return;
        }

        ByteArrayOutputStream streamVoiceCaps = null;
        GetParameterValues gpv = new GetParameterValues(lastInform.getRoot() + ".Services.VoiceService.");
        Message m = Call(gpv, timeout);
        if (m == null) {
            return;
        }
        if (m instanceof GetParameterValuesResponse) {
            GetParameterValuesResponse gpvr = (GetParameterValuesResponse) m;
            Properties caps = new Properties();
            for (Entry<String, String> e : gpvr.values.entrySet()) {
                if (e.getKey().contains(".Capabilities.") || e.getKey().contains(".PhyInterface.")) {
                    caps.put(e.getKey(), e.getValue());
                }
            }

            streamVoiceCaps = new ByteArrayOutputStream(1024 * 1024);
            try {
                caps.store(streamVoiceCaps, null);
            } catch (IOException ex) {
                log(Level.SEVERE, null, ex);
                return;
            }
            sdl.setVoicecaps(streamVoiceCaps.toByteArray());
        } else {
            //logger.log(Level.WARNING, "Failed get voice service caps: " + m.toString());
            sdl.setVoicecaps("".getBytes());
        }
    }
    private Message request;
    private Message response;

    public synchronized Message ReceiveRequest(long w) {
        Message r = request;
        //System.out.println ("Configurator::ReceiveRequest w="+w+" req="+request+" resp="+response);
        if (w != 0) {
            try {
                wait(w);
                //System.out.println ("Configurator::ReceiveRequest2 w="+w+" req="+request+" resp="+response);
            } catch (InterruptedException ex) {
                //System.out.println ("Configurator::ReceiveRequest3 w="+w+" req="+request+" resp="+response);
            }
            r = request;
        }
        request = null;
        response = null;
        return r;
    }

    public synchronized void SendResponse(Message response) {
        this.response = response;
        //System.out.println ("Configurator::SendResponse req="+request+" resp="+response);
        notify();
        //System.out.println ("Configurator::SendResponse2 req="+request+" resp="+response);
    }

    public synchronized Message Call(HostsLocal host, Message call, long timeout) {
        Message mr = null;

        if (timeout > MAX_CALL_TIMEOUT) {
            timeout = MAX_CALL_TIMEOUT;
        } else if (timeout < MIN_CALL_TIMEOUT) {
            timeout = MIN_CALL_TIMEOUT;
        }
        switch (callType) {
            case 1:
                mr = Ejb.lookupCPEBean().Call(host, call, timeout);
                break;
            case 2:
                request = call;
                //System.out.println ("Configurator::Call req="+request+" resp="+response);
                notify();
                //System.out.println ("Configurator::Call2 req="+request+" resp="+response);

                try {
//                    System.out.println ("Configurator::Call WAIT timeout="+timeout);
                    wait(timeout * 1000);
                } catch (InterruptedException ex) {
                    //System.out.println ("Configurator::Call4 req="+request+" resp="+response);
                }
                //System.out.println ("Configurator::Call3 req="+request+" resp="+response);
                mr = response;
                break;
        }
        if (mr instanceof Fault) {
            Fault f = (Fault) mr;
            System.out.println("Fault: " + f.getFaultString() + " cwmp: " + f.getFaultStringCwmp());
        }
        return mr;
    }
    private int callType = 1;

    public void SetCallType(int callType) {
        this.callType = callType;
    }

    private boolean getProfileProps(Properties p, String root, String profile) throws FinderException {
        Hashtable<String, Boolean> profiles = new Hashtable<String, Boolean>();
        return getProfileProps(p, root, profile, profiles);
    }

    private boolean getProfileProps(Properties p, String root, String profile, Hashtable<String, Boolean> profiles) throws FinderException {
        if (profiles.containsKey(profile)) {
            return false;
        }
        profiles.put(profile, true);
        DeviceProfileLocal prf = Ejb.lookupDeviceProfileBean().findByPrimaryKey(profile);
        String baseprofile = prf.getBaseprofile();
        if (baseprofile != null && !baseprofile.equals("")) {
            boolean r = getProfileProps(p, root, baseprofile, profiles);
            if (!r) {
                return false;
            }
        }

        String profilename = prf.getName();
        Iterator<ProfilePropertyLocal> profileprops = Ejb.lookupProfilePropertyBean().findByProfile(profile).iterator();// prf.getProperties().iterator();
        while (profileprops.hasNext()) {
            ProfilePropertyLocal profileprop = profileprops.next();
            String name = profileprop.getName();
            String value = profileprop.getValue();
            log(Level.INFO, "Set property: source is profile '" + profilename + "' " + name + " -> " + value);
            putNormalizedProperty(p, root, name, value);
            //p.setProperty(name, value);
        }
        return true;
    }

    protected String getNormalizedName(String root, String name) {
        if (name.startsWith(root)) {
            return name;
        } else {
            return root + "." + Parameter.getNameWithoutRoot(name);
        }
    }

    protected String putNormalizedProperty(Properties p, String root, String name, String value) {
        p.setProperty(name = getNormalizedName(root, name), value);
        return name;
    }

    protected boolean SyncCwmpVariables(boolean fForcePasswords) {
        DataModelNode cfg = new DataModelNode(null);
        Properties p = new Properties();
        String root = lastInform.getRoot();

        try {
            if (!getProfileProps(p, root, host.getProfileName())) {
                log(Level.SEVERE, "Profile loop");
                return false;
            }
        } catch (FinderException ex) {
            log(Level.SEVERE, "Profile not found", ex);
            return false;
        }

        ArrayList<String> params = new ArrayList<String>();
        try {
            Collection<Host2ServiceLocal> svcicollection = Ejb.lookupHost2ServiceBean().findByHostId((Integer) host.getId());
            Iterator<Host2ServiceLocal> svcinstances = svcicollection.iterator();

            ServiceInstanceList svcistancelist = new ServiceInstanceList(svcicollection);

            while (svcinstances.hasNext()) {
                Host2ServiceLocal svcinstance = svcinstances.next();
                ServiceLocal svc = Ejb.lookupServiceBean().findByPrimaryKey(svcinstance.getServiceid());
                Iterator<ServicePropertyLocal> svcprops = Ejb.lookupServicePropertyBean().findByServiceId((Integer) svc.getId()).iterator();

                //String instance = svcinstance.getInstance().toString();
                String svcname = svc.getName();
                Integer[] ix = svcistancelist.getInstancesArray(svcinstance);

                while (svcprops.hasNext()) {
                    ServicePropertyLocal svcprop = svcprops.next();
                    String name = svcprop.getName();
                    //name.replace("{i}", instance);
                    name = svcistancelist.mapName(name, ix);

                    if (svcprop.getIsparam()) {
                        params.add(getNormalizedName(root, name));
                    } else {
                        String value = svcprop.getValue();
                        log(Level.INFO, "Set property: source is service '" + svcname + "' " + name + " -> " + value);
                        //p.setProperty(name, value);
                        putNormalizedProperty(p, root, name, value);
                    }
                }
            }
        } catch (FinderException ex) {
        }

        try {
            Iterator<HostPropertyLocal> hostprops = Ejb.lookupHostPropertyBean().findByHost((Integer) host.getId()).iterator();
            while (hostprops.hasNext()) {
                HostPropertyLocal hostprop = hostprops.next();
                String name = hostprop.getName();
                String value = hostprop.getValue();
                log(Level.INFO, "Set property: source is host " + name + " -> " + value);
//                p.setProperty(name, value);
                putNormalizedProperty(p, root, name, value);
            }
        } catch (FinderException ex) {
        }

        boolean fMissingParams = false;
        for (String par : params) {
            if (p.getProperty(getNormalizedName(root, par)) == null) {
                log(Level.WARNING, "Parameter " + par + " should be overriden in host properties.");
                fMissingParams = true;
            }
        }

        if (fMissingParams) {
            log(Level.WARNING, "Not all required parameters set.");
            return false;
        }

        cfg.load(p, false);
        return SyncCwmpVariables(0, cfg, fForcePasswords);
    }

    protected boolean SyncCwmpVariables(int lvl, DataModelNode cfg, boolean fForcePasswords) {
        ArrayList<DataModelNode> l = new ArrayList<DataModelNode>();
        ArrayList<DataModelNode> v = new ArrayList<DataModelNode>();
        cfg.getUpdateList(l, v);

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < lvl; i++) {
            b.append('\t');
        }

        try {
            for (DataModelNode c : l) {
                String fqdnCanonic = c.getFQNameCanonic();
                System.out.println(b + fqdnCanonic);
                if (c.isMultiInstance()) {
                    String fqdn = c.getFQName();
                    System.out.println("Parameter " + fqdn + " to be checked.");
                    GetParameterNames gpn = new GetParameterNames(fqdn + ".", true);
                    GetParameterNamesResponse gpnr = (GetParameterNamesResponse) Call(gpn, timeout);
                    // GetParameterNames
                    //int [] cpenames = {1,3,4};
                    int[] cpenames = gpnr.getMultiInstanceNames(fqdn + ".");
                    int[] chknames = c.getInstanceNames();
                    if (c.ExpandWildcardInstance(cpenames)) {
                        chknames = cpenames;
                    }

                    System.out.println("In chklist " + chknames.length + " and on cpe " + cpenames.length);
                    Parameter d = Parameter.lookup(fqdnCanonic + ".{i}.");
                    if (d == null) {
                        log(Level.WARNING, "Var " + fqdnCanonic + " is not known in dictionary");
                        continue;
                    }
                    log(Level.INFO, c.getChildren().size() + " childrens (before count check)");
                    if (d.isWritable() && !c.DoNotCreateDelete()) {
                        System.out.println(fqdn + " writable=" + d.isWritable());
                        // Check if writable and if same length
                        int countCommon = (cpenames.length > chknames.length) ? chknames.length : cpenames.length;
                        for (int i = 0; i < countCommon; i++) {
                            // Map
                            if (chknames[i] != cpenames[i]) {
                                System.out.println("Parameter " + fqdn + "." + chknames[i] + " renamed to " + cpenames[i]);
                                c.renameChild(chknames[i], cpenames[i]);
                            } else {
                                System.out.println("Parameter " + fqdn + "." + chknames[i] + " is OK");
                            }
                            log(Level.INFO, c.getChildren().size() + " childrens (while renaming) i=" + i);
                        }
                        for (int i = countCommon; i < cpenames.length; i++) {
                            // Delete
                            String n = fqdn + "." + cpenames[i] + ".";
                            System.out.println("Parameter " + n + " to be deleted.");
                            DeleteObject delo = new DeleteObject(n, fqdn);
                            Call(delo, timeout);
                        }
                        for (int i = countCommon; i < chknames.length; i++) {
                            // Add/map
                            //int ni = 0; // AddObject ()
                            System.out.println("Parameter " + fqdn + "." + chknames[i] + " to be created.");
                            AddObject ao = new AddObject(fqdn + ".", "SyncAdd");
                            AddObjectResponse aor;
                            aor = (AddObjectResponse) Call(ao, timeout);
                            System.out.println("Parameter " + fqdn + "." + aor.InstanceNumber + " was created.");
                            c.renameChild(chknames[i], aor.InstanceNumber);
                        }
                    } else {
                        if (cpenames.length != chknames.length) {
                            log(Level.WARNING, "Var " + fqdn + " is not writable and var count (" + chknames.length + ")!= count on cpe(" + cpenames.length + ")");
                        }
                    }
                    log(Level.INFO, c.getChildren().size() + " childrens (after count check)");
                    for (DataModelNode ch : c.getChildren()) {
                        log(Level.INFO, b + "name=" + ch.getName());
                        SyncCwmpVariables(lvl + 1, ch, fForcePasswords);
                    }
                }
            }

            ArrayList<String> ParameterNames = new ArrayList<String>(v.size());
            // for writable GetParameterValues/SetParameterValues
            for (DataModelNode c : v) {
                String fqdnCanonic = c.getFQNameCanonic();
                String fqdn = c.getFQName();
                System.out.println(b + fqdnCanonic);
                Parameter d = Parameter.lookup(fqdnCanonic);
                if (d == null) {
                    System.out.println("Object unknown: " + fqdn);
                    continue;
                }

                if (d.isWritable()) {
                    System.out.println("Parameter " + fqdn + "  is writable");
                    ParameterNames.add(fqdn);
                } else {
                    System.out.println("Parameter " + fqdn + "  is readonly");
                }
            }
            if (!ParameterNames.isEmpty()) {
                for (String n : ParameterNames) {
                    System.out.println("Get parameter: " + n);
                }
                GetParameterValues gpv = new GetParameterValues(ParameterNames.toArray(new String[0]));
                GetParameterValuesResponse gpvr = (GetParameterValuesResponse) Call(gpv, timeout);

                SetParameterValues spv = new SetParameterValues();
                SetParameterValues spvPasswords = new SetParameterValues();

                for (DataModelNode c : v) {
                    String fqdn = c.getFQName();
                    String fqdnCanonic = c.getFQNameCanonic();
                    System.out.println(b + fqdn);

                    String cpevalue = gpvr.values.get(fqdn);
                    boolean isParamPassword = fqdn.endsWith("Password") || fqdn.endsWith("WEPKey") || fqdn.endsWith("PreSharedKey");
                    if (cpevalue != null || isParamPassword) {
                        Parameter d = Parameter.lookup(fqdnCanonic);
                        boolean isEqual;
                        if (cpevalue == null) {
                            isEqual = false;
                        } else if (d != null && d.getType().equals(Type.BOOLEAN.toString())) {
                            boolean v1 = c.getValue().equalsIgnoreCase("true") || c.getValue().equals("1");
                            boolean v2 = cpevalue.equalsIgnoreCase("true") || cpevalue.equals("1");
                            isEqual = (v1 == v2);
                        } else {
                            isEqual = c.getValue().equals(cpevalue);
                        }
                        if (!isEqual) {
                            if (isParamPassword) {
                                System.out.println("Set password :" + fqdn + "  '" + c.getValue());
                                spvPasswords.AddValue(fqdn, c.getValue(), d.getType());
                            } else {
                                System.out.println("Value mismatch :" + fqdn + "  '" + c.getValue() + "'!='" + cpevalue + "'");
                                spv.AddValue(fqdn, c.getValue(), d.getType());
                            }
                        } else {
                            System.out.println("Value OK :" + fqdn + "  " + c.getValue() + "==" + cpevalue);
                        }
                    } else {
                        System.out.println("No value for " + fqdn);
                    }
                }

                if (!spv.isEmpty() || fForcePasswords) {
                    spv.Merge(spvPasswords);
                    System.out.println("Set parameters\n" + spv);
                    SetParameterValuesResponse spvr = (SetParameterValuesResponse) Call(spv, timeout);
                } else {
                    System.out.println("Nothing to set");
                }
            }
        } catch (ClassCastException e) {
            return false;
        }
        return true;
    }

    public void SyncParameterValues(Properties vars) {
        DataModelNode cfg = new DataModelNode(null);
        cfg.load(vars, false);
        SyncCwmpVariables(0, cfg, false);
    }
}
