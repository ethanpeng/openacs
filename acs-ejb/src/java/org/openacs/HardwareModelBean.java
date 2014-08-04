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

import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

public abstract class HardwareModelBean implements EntityBean {

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
    public abstract java.lang.String getOui();

    public abstract void setOui(java.lang.String oui);

    public abstract java.lang.Object getId();

    public abstract void setId(java.lang.Object id);

    public java.lang.Object ejbCreate(java.lang.Object id) throws CreateException {
        /*
        if (key == null) {
        throw new CreateException("The field \"key\" must not be null");
        }
        
        // TODO add additional validation code, throw CreateException if data is not valid
        setOui(oui);
         */

        setProfileToAssign("Default");
        return null;
    }

    public void ejbPostCreate(java.lang.Object key) {
        // TODO populate relationships here if appropriate
    }

    public abstract String getHclass();

    public abstract void setHclass(String hclass);

    public abstract String getDisplayName();

    public abstract void setDisplayName(String DisplayName);

    public Object ejbCreate(String dname, String manufacturer, String oui, String hclass, String version) throws CreateException {
        setDisplayName(dname);
        setHclass(hclass);
        setOui(oui);
        setManufacturer(manufacturer);
        setVersion(version);
        setProfileToAssign("Default");
        return null;
    }

    public void ejbPostCreate(String dname, String manufacturer, String oui, String hclass, String version) throws CreateException {
    }

    public abstract String getManufacturer();

    public abstract void setManufacturer(String manufacturer);

    public abstract String getVersion();

    public abstract void setVersion(String version);

    public abstract Collection<SoftwareLocal> getFirmware();

    public abstract void setFirmware(Collection m);

    public abstract String getProfileToAssign();

    public abstract void setProfileToAssign(String profileToAssign);
}
