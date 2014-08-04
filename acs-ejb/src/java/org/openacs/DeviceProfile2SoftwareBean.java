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

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

public abstract class DeviceProfile2SoftwareBean implements EntityBean {

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
    public DeviceProfile2SoftwarePK ejbCreate(String profileName, Integer hwid, String version) throws CreateException {
        if (profileName == null) {
            throw new CreateException("The field \"profileName\" must not be null");
        }
        if (hwid == null) {
            throw new CreateException("The field \"hwid\" must not be null");
        }
        if (version == null) {
            throw new CreateException("The field \"version\" must not be null");
        }

        // TODO add additional validation code, throw CreateException if data is not valid
        setProfileName(profileName);
        setHwid(hwid);
        setVersion(version);

        return null;
    }

    public void ejbPostCreate(String profileName, Integer hwid, String version) {
        // TODO populate relationships here if appropriate
    }

    public abstract String getProfileName();

    public abstract void setProfileName(String profileName);

    public abstract Integer getHwid();

    public abstract void setHwid(Integer hwid);

    public abstract String getVersion();

    public abstract void setVersion(String version);
//    public abstract SoftwareLocal getFirmware ();
//    public abstract void setFirmware(SoftwareLocal s);
}
