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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.faces.model.SelectItem;
import org.openacs.HardwareModelLocal;
import org.openacs.utils.Ejb;

public class HwModelJsfBean extends JsfBeanBase {

    /** Creates a new instance of HwModelJsfBean */
    public HwModelJsfBean() {
//        System.out.println("HwModelJsfBean.constructor");
        clear();
    }

    public String clear() {
        this.displayName = "";
        this.hclass = "";
        this.id = 0;
        this.manufacturer = "";
        this.oui = "";
        this.version = "";
        return "hwcleared";
    }
    private Object[] arrayAll = null;

    public Object[] getAll() throws FinderException {
//        System.out.println("HwModelJsfBean.getAll");
        if (arrayAll != null) {
            return arrayAll;
        } else {
            return arrayAll = Ejb.lookupHardwareModelBean().findAll().toArray();
        }
    }
    protected Integer id = null;

    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the value of id
     *
     * @param id new value of id
     */
    public void setId(Integer id) {
        this.id = id;
//        System.out.println("HwModelJsfBean.setId=" + id);
    }

    public String load() {
        try {
            HardwareModelLocal s = Ejb.lookupHardwareModelBean().findByPrimaryKey(id);
            displayName = s.getDisplayName();
            hclass = s.getHclass();
            manufacturer = s.getManufacturer();
            oui = s.getOui();
            version = s.getVersion();
        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
            return null;
        }
        return "hwloaded";
    }
    private String oui;

    public String getOui() {
        return oui;
    }

    public void setOui(String oui) {
        this.oui = oui;
    }
    private String hclass;

    public String getHclass() {
        return hclass;
    }

    public void setHclass(String hclass) {
        this.hclass = hclass;
    }
    private String manufacturer;

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String Save() {
//        System.out.println("ScriptJsfBean.Save () id=" + id);
        try {
            HardwareModelLocal s = Ejb.lookupHardwareModelBean().findByPrimaryKey(id);
            s.setDisplayName(displayName);
            s.setHclass(hclass);
            s.setManufacturer(manufacturer);
            s.setOui(oui);
            s.setVersion(version);
        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
            return null;
        }
        setSaved();
        return "hwsaved";
    }

    public String Create() {
//        System.out.println("ScriptJsfBean.Create () id=" + id);
        try {
            HardwareModelLocal s = Ejb.lookupHardwareModelBean().create(null);
            id = (Integer) s.getId();
            s.setDisplayName(displayName);
            s.setHclass(hclass);
            s.setManufacturer(manufacturer);
            s.setOui(oui);
            s.setVersion(version);
        } catch (CreateException ex) {
            setErrorMessage(ex.getMessage());
            return null;
        }
        return "hwcreated";
    }

    public String Delete() {
//        System.out.println("ScriptJsfBean.Delete () id=" + id);
        try {
            Ejb.lookupHardwareModelBean().findByPrimaryKey(id).remove();
        } catch (Exception ex) {
            setErrorMessage(ex.getMessage());
            return null;
        }
        clear();
        return "hwdeleted";
    }

    public Collection getList() {
        try {
            Iterator cfgs = Ejb.lookupHardwareModelBean().findAll().iterator();
            ArrayList<SelectItem> a = new ArrayList<SelectItem>();
            while (cfgs.hasNext()) {
                HardwareModelLocal model = (HardwareModelLocal) cfgs.next();
                a.add(new SelectItem(((Integer) model.getId()).toString(), model.getDisplayName()));
            }
            return a;
        } catch (FinderException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
