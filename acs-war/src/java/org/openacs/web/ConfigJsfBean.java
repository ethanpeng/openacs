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

import java.util.Collection;
import java.util.Collections;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import org.openacs.ConfigurationLocal;
import org.openacs.ConfigurationPK;
import org.openacs.HardwareModelLocal;
import org.openacs.utils.Ejb;
import org.openacs.vendors.Vendor;

public class ConfigJsfBean extends JsfBeanBase {

    /** Creates a new instance of ConfigJsfBean */
    public ConfigJsfBean() {
//        System.out.println ("ConfigJsfBean.constructor");
        clear();
    }

    public Collection getAll() {
//        System.out.println ("ConfigJsfBean.getAll hwid="+hwid);
        try {
            if (hwid == null || hwid == 0) {
                return Ejb.lookupConfigurationBean().findAll();
            } else {
                return Ejb.lookupConfigurationBean().findByHwid(this.hwid);
            }
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
        this.filename = filename;
    }
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    private String config;

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
//        System.out.println ("Config.setName="+name);
        this.name = name;
    }
    private Integer hwid;

    public Integer getHwid() {
        return hwid;
    }

    public void setHwid(Integer hwid) {
//        System.out.println ("ConfigJsfBean.setHwid hwid="+hwid);
        this.hwid = hwid;
    }

    public String load() {
//        System.out.println("ConfigJsfBean.load hwid=" + hwid);
        try {
            ConfigurationLocal s = Ejb.lookupConfigurationBean().findByPrimaryKey(new ConfigurationPK(this.hwid, name));
            this.filename = s.getFilename();
            this.config = "";
            byte[] cb = s.getConfig();
            if (cb != null) {
                this.config = new String(cb);
            }
            this.name = s.getName();
            this.version = s.getVersion();
        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
        }
        return "cfgloaded";
    }

    public String Save() {
//        System.out.println ("Config.Save: hwid="+hwid+" name="+name);
        try {
            ConfigurationLocal s = Ejb.lookupConfigurationBean().findByPrimaryKey(new ConfigurationPK(hwid, name));
            HardwareModelLocal hw = s.getHardware();
            Vendor v = Vendor.getVendor(hw.getOui(), hw.getHclass(), hw.getVersion());

            String[] r = v.CheckConfig(filename, name, version, config);
            if (r != null && r.length > 0) {
                setErrorMessage(r);
            }
            s.setFilename(filename);
            s.setVersion(version);
            s.setConfig(config.getBytes());
            setSaved();
        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
        }
        return "cfgsaved";
    }

    public String Create() {
//        System.out.println ("Config.Create: hwid="+hwid+" name="+name);
        try {
            ConfigurationLocal s = Ejb.lookupConfigurationBean().create(hwid, name);
            s.setFilename(filename);
            s.setVersion(version);
            s.setConfig(config.getBytes());
            setSaved();
        } catch (CreateException ex) {
            setErrorMessage(ex.getMessage());
        }
        return "cfgcreated";
    }

    public String Delete() {
//        System.out.println ("Config.Delete: hwid="+hwid+" name="+name);
        try {
            Ejb.lookupConfigurationBean().findByPrimaryKey(new ConfigurationPK(hwid, name)).remove();
        } catch (Exception ex) {
            setErrorMessage(ex.getMessage());
        }
        clear();
//        name = filename = version = null;
        return "cfgdeleted";
    }

    public String clear() {
//        System.out.println ("ConfigJsfBean.clear hwid="+hwid);
        name = filename = version = null;
        return "cfgcleared";
    }
}
