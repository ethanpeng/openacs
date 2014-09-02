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

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.openacs.Application;
import org.openacs.SoftwareLocalHome;
import org.openacs.SoftwarePK;
import org.openacs.utils.Ejb;
import org.richfaces.model.ScrollableTableDataModel.SimpleRowKey;
import org.richfaces.model.selection.Selection;
import org.openacs.SoftwareLocal;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

public class SoftwaresBean {

    /**
     * Creates a new instance of SoftwaresBean
     */
    public SoftwaresBean() {
    }
    /**
     * Getter for property allHosts.
     * @return Value of property allHosts.
     */
    private Object[] arraySw = null;

    public Object[] getAll() throws FinderException {
        if (arraySw != null) {
            return arraySw;
        } else {
            return arraySw = Ejb.lookupSoftwareBean().findAll().toArray();
        }
    }

    public String deleteItem() {
        if (selection != null && selection.size() > 0) {
            Iterator k = selection.getKeys();
            while (k.hasNext()) {
                SimpleRowKey rk = (SimpleRowKey) k.next();
                SoftwareLocal sw = (SoftwareLocal) arraySw[rk.intValue()];
                try {
                    sw.remove();
                } catch (EJBException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                } catch (RemoveException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                }
            }
        }
        arraySw = null; // force reload
        return null;
    }
    /**
     * Holds value of property version.
     */
    private String version;

    /**
     * Getter for property version.
     * @return Value of property version.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Setter for property version.
     * @param version New value of property version.
     */
    public void setVersion(String version) {
        this.version = version;
    }
    /**
     * Holds value of property hardware.
     */
    //private String hardware;
    /**
     * Getter for property hardware.
     * @return Value of property hardware.
     */
    /*
    public String getHardware() {
    return this.hardware;
    }
     */
    /**
     * Setter for property hardware.
     * @param hardware New value of property hardware.
     */
    /*
    public void setHardware(String hardware) {
    this.hardware = hardware;
    }
     */
    /**
     * Holds value of property minversion.
     */
    private String minversion;

    /**
     * Getter for property minversion.
     * @return Value of property minversion.
     */
    public String getMinversion() {
        return this.minversion;
    }

    /**
     * Setter for property minversion.
     * @param minversion New value of property minversion.
     */
    public void setMinversion(String minversion) {
        this.minversion = minversion;
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

    /**
     * Setter for property url.
     * @param url New value of property url.
     */
    public void setUrl(String url) {
        this.url = url;
    }
    /**
     * Holds value of property edit.
     */
    private boolean edit;

    /**
     * Getter for property edit.
     * @return Value of property edit.
     */
    public boolean isEdit() {
        return this.edit;
    }

    /**
     * Setter for property edit.
     * @param edit New value of property edit.
     */
    public void setEdit(boolean edit) {
        this.edit = edit;
        if (edit) {
            prepareEdit();
        } else {
            prepareNew();
        }
    }

    public String getFwError() {
        try {
            File fwdir = new File(getFwPath());
            String msg = null;
            if (!fwdir.exists()) {
                msg = "not exists";
            } else if (!fwdir.isDirectory()) {
                msg = "is not directory";
            } else if (!fwdir.canRead()) {
                msg = "is not readable";
            } else if (!fwdir.canWrite()) {
                msg = "is not writable";
            }
            return msg;
        } catch (Exception e) {
            return "unknown error " + e.getClass().getName();
        }
    }

    private void checkFwPath() {
        try {
            File fwdir = new File(getFwPath());
            String msg = null;
            if (!fwdir.exists()) {
                msg = "not exists";
            } else if (!fwdir.isDirectory()) {
                msg = "is not directory";
            } else if (!fwdir.canRead()) {
                msg = "is not readable";
            } else if (!fwdir.canWrite()) {
                msg = "is not writable";
            }
            if (msg != null) {
                FacesContext ctx = FacesContext.getCurrentInstance();
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Path " + getFwPath() + " " + msg, ""));
            }
        } catch (Exception e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "unknown error " + e.getClass().getName(), ""));
        }
    }

    public String prepareNew() {
//        checkFwPath ();
        edit = false;
//        hardware = "";
        hwid = 0;
        version = "";
        minversion = "";
        url = "";
        size = 0;
        return null;
    }

    public String prepareEdit() {
        //      checkFwPath ();
        edit = true;
        if (selection != null && selection.size() == 1) {
            SimpleRowKey rk = (SimpleRowKey) selection.getKeys().next();
            SoftwareLocal sw = (SoftwareLocal) arraySw[rk.intValue()];
//            hardware = sw.getHardware();
            hwid = sw.getHwid();
            version = sw.getVersion();
            //minversion = sw.getMinversion();
            url = sw.getUrl();
            size = sw.getSize();
            fileName = sw.getFilename();
            if (fileName == null || fileName.equals("")) {
                try {
                    URL u = new URL(url);
                    fileName = u.getFile();
                } catch (MalformedURLException ex) {
                }
            }
        }

        return null;
    }
    /**
     * Holds value of property selection.
     */
    private Selection selection;

    /**
     * Getter for property selection.
     * @return Value of property selection.
     */
    public Selection getSelection() {
        return this.selection;
    }

    /**
     * Setter for property selection.
     * @param selection New value of property selection.
     */
    public void setSelection(Selection selection) {
        this.selection = selection;
    }

    public String deleteFwFile() {
        File f = new File(getFwPath() + fileName);
        f.delete();
        return null;
    }
    private Integer hwid = 0;

    public int getHwid() {
        return this.hwid;
    }

    public void setHwid(int hwid) {
        this.hwid = hwid;
    }

    public String editItem() {
        SoftwareLocalHome h = Ejb.lookupSoftwareBean();
        File f = new File(getFwPath() + fileName);
        size = f.length();
        if (edit) {
            try {
                SoftwareLocal sw = h.findByPrimaryKey(new SoftwarePK(hwid, version));
                //sw.setMinversion(minversion);
                sw.setUrl(url);
                sw.setSize(size);
                sw.setFilename(fileName);
            } catch (FinderException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                SoftwareLocal c = h.create(hwid, version, minversion, url);
                c.setUrl(url);
                c.setSize(size);
                c.setFilename(fileName);
            } catch (CreateException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
        arraySw = null; // force reload
        return null;
    }
    /**
     * Holds value of property size.
     */
    private long size;

    /**
     * Getter for property size.
     * @return Value of property size.
     */
    public long getSize() {
        return this.size;
    }

    /**
     * Setter for property size.
     * @param size New value of property size.
     */
    public void setSize(long size) {
        this.size = size;
    }

    public Collection getFiles() {
        ArrayList<SelectItem> a = new ArrayList<SelectItem>();
        try {
            File fwdir = new File(getFwPath());
            for (File f : fwdir.listFiles()) {
                if (!f.isDirectory()) {
                    a.add(new SelectItem(f.getName()));
                }
            }
        } catch (Exception e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }
        return a;
    }
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void uploadListener(UploadEvent event) {
        UploadItem item = event.getUploadItem();
        if (item.isTempFile()) {
        } else {
            try {
                String fname = item.getFileName();
                int i = fname.lastIndexOf('/');
                if (i == -1) {
                    i = fname.lastIndexOf('\\');
                }
                if (i != -1) {
                    fname = fname.substring(i);
                }

                FileOutputStream fout = new FileOutputStream(getFwPath() + fname);
                fout.write(item.getData());
                fout.close();
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }

    public String getFwPath() {
        return Application.getFirmwarePath();
    }
}
