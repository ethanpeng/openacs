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

import java.util.Iterator;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import org.openacs.ConfigurationLocal;
import org.openacs.ConfigurationLocalHome;
import org.openacs.ConfigurationPK;
import org.richfaces.model.ScrollableTableDataModel.SimpleRowKey;
import org.richfaces.model.selection.Selection;

import org.openacs.utils.Ejb;

public class ConfigsBean {

    public ConfigsBean() {
    }
    /**
     * Getter for property all.
     * @return Value of property all.
     */
    private Object[] arrayCfgs = null;

    public Object[] getAll() throws FinderException {
        if (arrayCfgs != null) {
            //System.out.println("CONFIGS: getAll (cached)");
            return arrayCfgs;
        } else {
            //System.out.println("CONFIGS: getAll");
            return arrayCfgs = Ejb.lookupConfigurationBean().findAll().toArray();
        }
    }
    private boolean edit = false;

    public boolean getEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
        if (edit) {
            prepareEdit();
        } else {
            prepareNew();
        }
    }

    public String prepareNew() {
        edit = false;
        name = "";
        software = "";
        hwid = null;
        cfg = "";
        return null;
    }

    public String prepareEdit() {
        edit = true;
        if (selection != null && selection.size() == 1) {
            SimpleRowKey rk = (SimpleRowKey) selection.getKeys().next();
            //System.out.println("resetnewflag SELECTION element is "+rk.intValue());
            ConfigurationLocal cfg = (ConfigurationLocal) arrayCfgs[rk.intValue()];
            name = cfg.getName();
            //software = cfg.getSoftware();
            hwid = (Integer) cfg.getHardware().getId();
            filename = cfg.getFilename();
            version = cfg.getVersion();
            byte[] b = cfg.getConfig();
            this.cfg = (b != null) ? new String(b) : "";
        }

        return null;
    }
    /*
    public String newItem() {
    //System.out.println("NEWITEM");
    ConfigurationLocalHome h = Ejb.lookupConfigurationBean ();
    try {
    h.create("Config "+((arrayCfgs != null)?arrayCfgs.length : 0));
    } catch (CreateException ex) {
    ex.printStackTrace();
    throw new RuntimeException(ex);
    }
    arrayCfgs = null; // force reload
    return null;
    }
     */

    public String editItem() {
        //System.out.println("EDITITEM name = "+name);
        ConfigurationLocalHome h = Ejb.lookupConfigurationBean();
        if (edit) {
            try {
                ConfigurationLocal cfg = h.findByPrimaryKey(new ConfigurationPK(hwid, name));
                //cfg.setName (name);
                //cfg.setSoftware(software);
                //cfg.setHardware(hardware);
                cfg.setConfig(this.cfg.getBytes());
                cfg.setFilename(filename);
                cfg.setVersion(version);
            } catch (FinderException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                ConfigurationLocal c = h.create(hwid, name);
                //c.setHardware(hardware);
                //c.setSoftware(software);
                c.setConfig(this.cfg.getBytes());
                c.setFilename(filename);
                c.setVersion(version);
            } catch (CreateException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
        arrayCfgs = null; // force reload
        return null;
    }

    public String deleteItem() {
        //System.out.println("DELTEITEM");
        if (selection != null && selection.size() > 0) {
            Iterator k = selection.getKeys();
            while (k.hasNext()) {
                SimpleRowKey rk = (SimpleRowKey) k.next();
                //System.out.println("DELETEITEM SELECTION element is "+rk.intValue());
                //System.out.println("DELETEITEM SELECTION element is "+arrayCfgs[0].getClass().getName());
                ConfigurationLocal cfg = (ConfigurationLocal) arrayCfgs[rk.intValue()];
                try {
                    cfg.remove();
                } catch (EJBException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                } catch (RemoveException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                }
            }
        }
        arrayCfgs = null; // force reload
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
        //System.out.println ("GETSELECTION");
        return this.selection;
    }

    /**
     * Setter for property selection.
     * @param selection New value of property selection.
     */
    public void setSelection(Selection selection) {
        //System.out.println ("SETSELECTION: size = "+selection.size());
        Iterator k = selection.getKeys();
        while (k.hasNext()) {
            Object e = k.next();
            //System.out.println("SETSELECTION element is "+e.getClass().getName());
            //System.out.println("SETSELECTION element is "+e);
        }
        //System.out.println("SETSELECTION "+selection.getClass().getName());
        this.selection = selection;
    }
    /**
     * Holds value of property name.
     */
    private String name;

    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        //System.out.println ("CONFIG GETNAME");
        return this.name;
    }

    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        //System.out.println ("CONFIG SETNAME");
        this.name = name;
    }
    /**
     * Holds value of property hardware.
     */
    private Integer hwid;

    /**
     * Getter for property hardware.
     * @return Value of property hardware.
     */
    public Integer getHwid() {
        return this.hwid;
    }

    /**
     * Setter for property hardware.
     * @param hardware New value of property hardware.
     */
    public void setHardware(Integer hwid) {
        this.hwid = hwid;
    }
    /**
     * Holds value of property software.
     */
    private String software;

    /**
     * Getter for property software.
     * @return Value of property software.
     */
    public String getSoftware() {
        return this.software;
    }

    /**
     * Setter for property software.
     * @param software New value of property software.
     */
    public void setSoftware(String software) {
        this.software = software;
    }
    /**
     * Holds value of property cfg.
     */
    private String cfg;

    /**
     * Getter for property cfg.
     * @return Value of property cfg.
     */
    public String getCfg() {
        return this.cfg;
    }

    /**
     * Setter for property cfg.
     * @param cfg New value of property cfg.
     */
    public void setCfg(String cfg) {
        this.cfg = cfg;
    }
    /**
     * Holds value of property filename.
     */
    private String filename;

    /**
     * Getter for property filename.
     * @return Value of property filename.
     */
    public String getFilename() {
        return this.filename;
    }

    /**
     * Setter for property filename.
     * @param filename New value of property filename.
     */
    public void setFilename(String filename) {
        this.filename = filename;
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
}
