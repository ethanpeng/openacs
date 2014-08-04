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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import org.openacs.SoftwareLocal;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.faces.model.SelectItem;
import org.openacs.SoftwarePK;
import org.openacs.utils.Ejb;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.openacs.Application;
import org.openacs.SoftwareDetailLocal;
import org.openacs.SoftwareDetailLocalHome;
import org.openacs.SoftwareDetailPK;
import org.openacs.DataModelNode;
import org.openacs.HardwareModelLocal;

public class SoftwareJsfBean extends JsfBeanBase {

    private DataModelNode voicecaps;

    /** Creates a new instance of ConfigJsfBean */
    public SoftwareJsfBean() {
//        System.out.println("Software.constructor");
        clear();
    }

    public String clear() {
//        System.out.println("Software.clear");
        filename = "";
        version = "";
        methods = null;
        return "swcleared";
    }
    private String hwmodel;

    public String getHwModel() {
        return this.hwmodel;
    }

    public String load() {
//        System.out.println("Software.load hwid=" + hwid);
        try {
            SoftwareLocal s = Ejb.lookupSoftwareBean().findByPrimaryKey(new SoftwarePK(this.hwid, version));
            this.filename = s.getFilename();
            HardwareModelLocal hw = Ejb.lookupHardwareModelBean().findByPrimaryKey(this.hwid);
            hwmodel = hw.getDisplayName();
        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
        }
        return "swloaded";
    }

    public Collection getAll() {
//        System.out.println("SoftwareJsfBean.getAll hwid=" + hwid);
//        if (hwid == null || hwid == 0) hwid = 10;
        try {
            if (hwid == null || hwid == 0) {
                return Collections.EMPTY_LIST;
//              return Ejb.lookupSoftwareBean().findAll();
            }
            return Ejb.lookupSoftwareBean().findByHardware(hwid);
        } catch (FinderException ex) {
            //setErrorMessage(ex.getMessage());
        }
        return Collections.EMPTY_LIST;
    }
    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
//        System.out.println("Software.setFilename: " + filename);
        this.filename = filename;
    }
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
//        System.out.println("Software.setVersion: " + version);
        if ((version != null && this.version == null) || (this.version != null && version == null) || !this.version.equalsIgnoreCase(version)) {
            thecfg = curcfg = null;
            methods = null;
        }
        this.version = version;
    }
    private Integer hwid;

    public Integer getHwid() {
        return hwid;
    }

    public void setHwid(Integer hwid) {
        try {
//            System.out.println("Software.setHwid: " + hwid);
            if (this.hwid.equals(hwid)) {
            }
        } catch (Exception e) {
            thecfg = curcfg = null;
            methods = null;

        }

        this.hwid = hwid;
    }

    public String Save() {
        System.out.println("Software.Save: hwid=" + hwid + " version=" + version + " filename=" + filename);
        try {
            SoftwareLocal s = Ejb.lookupSoftwareBean().findByPrimaryKey(new SoftwarePK(hwid, version));
            File f = new File(getFwPath() + filename);
            s.setFilename(filename);
            s.setSize(f.length());
            setInfoMessage("Saved.");

        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
            return null;
        }
        return "swsaved";
    }

    public String Create() {
        System.out.println("Software.Create: hwid=" + hwid + " version=" + version + " filename=" + filename);
        try {
            SoftwareLocal s = Ejb.lookupSoftwareBean().create(hwid, version, null, null);
            File f = new File(getFwPath() + filename);
            s.setFilename(filename);
            s.setSize(f.length());
        } catch (CreateException ex) {
            setErrorMessage(ex.getMessage());
            return null;
        }
        return "swcreated";
    }

    public String Delete() {
        System.out.println("Software.Delete: hwid=" + hwid + " version=" + version);
        try {
            Ejb.lookupSoftwareBean().findByPrimaryKey(new SoftwarePK(hwid, version)).remove();
        } catch (Exception ex) {
            setErrorMessage(ex.getMessage());
            return null;
        }
        version = filename = null;
        return "swdeleted";
    }

    public String getFwPath() {
//        return Util.getFirmwarePath(FacesContext.getCurrentInstance().getExternalContext());
        return Application.getFirmwarePath();
    }

    public String deleteFwFile() {
        System.out.println("Software.deleteFwFile: file=" + filename + " path=" + getFwPath());
        if (!filename.equals("")) {
            File f = new File(getFwPath() + filename);
            f.delete();
        }
        return null;
    }

    public void uploadListener(UploadEvent event) {
        UploadItem item = event.getUploadItem();

        System.out.println("item : '" + item);
        System.out.println("File : '" + item.getFileName() + "' was uploaded");
        System.out.println("Content-type : '" + item.getContentType());
        System.out.println("Data : '" + item.getData());
        System.out.println("IsTemporary : '" + item.isTempFile());
        //        System.out.println("Length : '" + item.getData().length);
        System.out.println("javaFile : '" + item.getFile());

        String fname = item.getFileName();
        int i = fname.lastIndexOf('/');
        if (i == -1) {
            i = fname.lastIndexOf('\\');
        }
        if (i != -1) {
            fname = fname.substring(i);
        }
        fname = getFwPath() + fname;
        if (item.isTempFile()) {
            try {
                File tmpfile = item.getFile();
                if (!tmpfile.renameTo(new File(fname))) {
                    setErrorMessage("Failed to rename file '" + tmpfile.getCanonicalPath() + "' to '" + fname + "'");
                }
            } catch (Exception ex) {
                setErrorMessage(ex.getMessage());
            }
        } else {
            try {
                FileOutputStream fout = new FileOutputStream(fname);
                fout.write(item.getData());
                fout.close();
            } catch (Exception e) {
                setErrorMessage(e.getMessage());
            }

        }
    }

    public boolean getSupportsVoice() {
        if (hwid == null || hwid == 0) {
            return true;
        }
        return Ejb.lookupSoftwareDetailBean().exists(hwid, version);
    }

    public boolean getHaveDetails() {
        if (hwid == null || hwid == 0) {
            return true;
        }
        return Ejb.lookupSoftwareDetailBean().exists(hwid, version);
    }
    private Properties methods;

    public Properties getMethods() {
        getDetails();
        return methods;
    }

    public String[] getVendorMethods() {
//        System.out.println("Software.getVendorMethods: hwid=" + hwid + " version=" + version + " filename=" + filename);
        getDetails();
        if (methods == null) {
            return new String[0];
        }
        ArrayList<String> m = new ArrayList<String>();
        Iterator mi = methods.entrySet().iterator();
        while (mi.hasNext()) {
            Entry<String, String> me = (Entry<String, String>) mi.next();
            if (me.getKey().startsWith("X_")) {
                m.add(me.getKey());
            }
        }
        String[] ms = m.toArray(new String[0]);
        Arrays.sort(ms);
        return ms;
    }

    private void getDetails() {
        if (hwid == null || hwid == 0) {
            methods = null;
            return;
        }
        SoftwareDetailLocal sd = null;
        if (methods == null) {

            try {
                sd = Ejb.lookupSoftwareDetailBean().findByPrimaryKey(new SoftwareDetailPK(hwid, version));
            } catch (FinderException ex) {
                setErrorMessage(ex.getMessage());
                return;
            }
            methods = new Properties();
            ByteArrayInputStream bi = new ByteArrayInputStream(sd.getMethods());
            try {
                methods.load(bi);
            } catch (IOException ex) {
                setErrorMessage(ex.getMessage());
                return;
            }
        }
        /*
        if (voicecaps == null && sd != null) {
        parameterNames = null;
        parameterValues = null;
        currentPath = "";
        voicecaps = new DataModelNode(null);
        InputStream in;
        in = new ByteArrayInputStream(sd.getVoicecaps());
        voicecaps.load(in);
        setPath(currentPath = voicecaps.getName());
        }
         */
        return;
    }

    public Collection getFiles() {
        ArrayList<SelectItem> a = new ArrayList<SelectItem>();
        a.add(new SelectItem("", "none"));
        try {
            File fwdir = new File(getFwPath());
            for (File f : fwdir.listFiles()) {
                if (!f.isDirectory()) {
                    a.add(new SelectItem(f.getName()));
                }
            }
        } catch (Exception e) {
            setErrorMessage(e.getMessage());
        }
        return a;
    }
    private DataModelNode thecfg = null;
    private DataModelNode curcfg = null;
    private String[] parameterNames = null;
    private Object[][] parameterValues = null;
    private String currentPath = null;

    public String[] getHeaders() {
//        System.out.println("SoftwareJsfBean.getHeaders currentPath=" + currentPath);
        getCfg();
//        System.out.println("SoftwareJsfBean.getHeaders parameterNames=" + parameterNames);

        if (parameterNames == null) {
            String ns[] = {"Name", "Value"};
            parameterNames = ns;
        }

        if (parameterNames != null) {
//            System.out.println("SoftwareJsfBean.getHeaders retlen=" + parameterNames.length);
            if (parameterNames.length == 2 && parameterNames[1].equals("Value")) {
                parameterNames[1] = "Access level";
            }
        }
        return parameterNames;
    }

    public Object[][] getCols() {
        getCfg();

        if (parameterValues == null) {
            Object[][] oa = new Object[1000][];
            Object oaa[] = {new DataModelNode(null, "dummy", true), "dummy"};
            for (int i = 0; i < oa.length; i++) {
                oa[i] = oaa;
            }
            parameterValues = oa;
        }

        return parameterValues;
    }

    public int getRowCount() {
        return getCols().length;
    }

    public void setPath(String p) {
//        System.out.println("SoftwareJsfBean.setPath currentPath=" + currentPath + " p=" + p);
        getCfg();
        curcfg = thecfg.getValue(p);
        parameterNames = curcfg.getParamNames();
        parameterValues = curcfg.getParamValues(parameterNames);
        currentPath = p;
    }

    public void setPath2(String p) {
//        System.out.println("SoftwareJsfBean.setPath2 currentPath=" + currentPath + " p=" + p);
        curcfg = thecfg.getValue(p);
        parameterNames = curcfg.getParamNames();
        parameterValues = curcfg.getParamValues(parameterNames);
        currentPath = p;
    }

    public String getPath() {
        return currentPath;
    }

    private DataModelNode getCfg() {
//        System.out.println("SoftwareJsfBean.getCfg currentPath=" + currentPath + " thecfg=" + thecfg);
        if (thecfg == null) {
            parameterNames = null;
            parameterValues = null;
//            currentPath = "";
            if (hwid == null || hwid == 0) {
                return null;
            }

            thecfg = new DataModelNode(null);
            InputStream in;
            try {
                //in = new FileInputStream("c:/temp/tr.txt");
                SoftwareDetailLocalHome sdlh = Ejb.lookupSoftwareDetailBean();
                SoftwareDetailLocal sdl = sdlh.findByPrimaryKey(new SoftwareDetailPK(hwid, version));
                in = new ByteArrayInputStream(sdl.getParamNames());
                thecfg.load(in, true);
                if (currentPath == null || currentPath.equals("")) {
                    currentPath = thecfg.getName();
                }
                setPath2(currentPath);

            } /*catch (FileNotFoundException ex) {
            Logger.getLogger(HostsBean.class.getName()).log(Level.SEVERE, null, ex);
            }*/ catch (FinderException ex) {
                thecfg = null;
                //setErrorMessage("No saved values found. \nCheck settings in profile or wait for device contacting ACS.");
//                Logger.getLogger(HostsBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return thecfg;
    }

    public Path[] getPathArray() {
        Path p = new Path();
        if (currentPath == null || currentPath.equals("")) {
            return p.fromString("a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a");
        }
        return p.fromString(currentPath);
    }

    public boolean getMultiInstance() {
        if (curcfg != null) {
            return curcfg.isMultiInstance();
        }
        return false;
    }

    public void setTooltip(String s) {
        System.out.println("SETTOOLTIP");
    }

    public String getTooltip() {
        return "SETTOOLTIP";
    }
}
