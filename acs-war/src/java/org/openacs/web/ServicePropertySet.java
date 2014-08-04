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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import org.openacs.ServicePropertyLocal;
import org.openacs.ServicePropertyLocalHome;
import org.openacs.ServicePropertyPK;
import org.openacs.utils.Ejb;

public class ServicePropertySet extends PropertySet<ServiceProperty> {

    private ServicePropertyLocalHome home;
    private Integer Serviceid;

    public ServicePropertySet(Integer Serviceid) {
        this.home = Ejb.lookupServicePropertyBean();
        this.Serviceid = Serviceid;
    }

    public void Load(Collection<ServicePropertyLocal> props) {
        for (ServicePropertyLocal p : props) {
            original.put(p.getName(), new ServiceProperty(p.getName(), p.getValue(), p.getIsparam()));
        }
        Load();
    }

    public void setId(Integer Serviceid) {
        this.Serviceid = Serviceid;
    }

    @Override
    public void Load() {
        Collection<ServicePropertyLocal> props;
        try {
            props = Ejb.lookupServicePropertyBean().findByServiceId(Serviceid);
            for (ServicePropertyLocal p : props) {
                original.put(p.getName(), new ServiceProperty(p.getName(), p.getValue(), p.getIsparam()));
            }
            super.Load();
        } catch (FinderException ex) {
            Logger.getLogger(ServicePropertySet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void DeleteEntry(ServiceProperty p) throws RemoveException {
        home.remove(new ServicePropertyPK(Serviceid, p.getName()));
    }

    @Override
    protected void SetEntry(ServiceProperty p) throws FinderException {
        ServicePropertyLocal pl = home.findByPrimaryKey(new ServicePropertyPK(Serviceid, p.getName()));
        pl.setValue(p.getValue());
        pl.setIsparam(p.getParam());
    }

    @Override
    protected void InsertEntry(ServiceProperty p) throws CreateException {
        ServicePropertyLocal pl = home.create(Serviceid, p.getName(), p.getValue());
        pl.setIsparam(p.getParam());
    }

    public void Set(String name, String value, boolean param) {
        Add(new ServiceProperty(name, value, param));
    }

    public String getValue(String name) {
        ServiceProperty p = Get(name);
        if (p != null) {
            return p.getValue();
        }
        return null;
    }

    public boolean getParam(String name) {
        ServiceProperty p = Get(name);
        if (p != null) {
            return p.getParam();
        }
        return false;
    }
}
