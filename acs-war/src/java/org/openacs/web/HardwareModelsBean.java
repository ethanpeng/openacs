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
import org.openacs.HardwareModelLocal;
import org.openacs.HardwareModelLocalHome;
import org.openacs.utils.Ejb;
import org.richfaces.model.ScrollableTableDataModel.SimpleRowKey;
import org.richfaces.model.selection.Selection;

public class HardwareModelsBean {

    /** Creates a new instance of HardwareModelsBean */
    public HardwareModelsBean() {
    }
    /**
     * Getter for property allHosts.
     * @return Value of property allHosts.
     */
    private Object[] models = null;

    public Object[] getAll() throws FinderException {
        if (models != null) {
            return models;
        } else {
            return models = Ejb.lookupHardwareModelBean().findAll().toArray();
        }
    }

    public String deleteItem() {
        if (selection != null && selection.size() > 0) {
            Iterator k = selection.getKeys();
            while (k.hasNext()) {
                SimpleRowKey rk = (SimpleRowKey) k.next();
                HardwareModelLocal sw = (HardwareModelLocal) models[rk.intValue()];
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
        models = null; // force reload
        return null;
    }
    private boolean edit;

    public boolean isEdit() {
        return this.edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
        if (edit) {
            prepareEdit();
        } else {
            prepareNew();
        }
    }
    private String oui;

    public void setOui(String oui) {
        this.oui = oui;
    }

    public String getOui() {
        return oui;
    }
    private String hclass;

    public void setHclass(String hclass) {
        this.hclass = hclass;
    }

    public String getHclass() {
        return hclass;
    }
    private String dname;

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getDname() {
        return dname;
    }
    private String manufacturer;

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() {
        return manufacturer;
    }
    private Object id;

    public void setId(Object id) {
        this.id = id;
    }

    public Object getId() {
        return id;
    }
    private String version;

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String prepareNew() {
        edit = false;
        oui = "";
        hclass = "";
        dname = "";
        id = null;
        manufacturer = "";
        version = "";
        return null;
    }

    public String prepareEdit() {
        edit = true;
        if (selection != null && selection.size() == 1) {
            SimpleRowKey rk = (SimpleRowKey) selection.getKeys().next();
            HardwareModelLocal sw = (HardwareModelLocal) models[rk.intValue()];
            id = sw.getId();
            oui = sw.getOui();
            dname = sw.getDisplayName();
            hclass = sw.getHclass();
            manufacturer = sw.getManufacturer();
            version = sw.getVersion();
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

    public String editItem() {
        HardwareModelLocalHome h = Ejb.lookupHardwareModelBean();
        if (edit) {
            try {
                HardwareModelLocal sw = h.findByPrimaryKey(id);
                //sw.setMinversion(minversion);
                sw.setDisplayName(dname);
                sw.setHclass(hclass);
                sw.setOui(oui);
                sw.setManufacturer(manufacturer);
                sw.setVersion(version);
            } catch (FinderException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                HardwareModelLocal c = h.create(dname, manufacturer, oui, hclass, version);
                id = c.getId();
            } catch (CreateException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
        models = null; // force reload
        return null;
    }
}
