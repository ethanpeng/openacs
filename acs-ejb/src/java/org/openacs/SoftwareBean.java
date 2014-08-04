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

import javax.ejb.*;

public abstract class SoftwareBean implements EntityBean, SoftwareLocalBusiness {

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

    /*
    public abstract String getHardware();
    public abstract void setHardware(String hardware);
     */
    public abstract Integer getHwid();

    public abstract void setHwid(Integer hardware);

    public abstract String getVersion();

    public abstract void setVersion(String version);

    public abstract String getMinversion();

    public abstract void setMinversion(String minversion);

    public abstract String getUrl();

    public abstract void setUrl(String url);

    public abstract long getSize();

    public abstract void setSize(long size);

    public abstract byte[] getImg();

    public abstract void setImg(byte[] img);

    public SoftwarePK ejbCreate(Integer hwid, String version, String minversion, String url) throws CreateException {
        if (hwid == null) {
            throw new CreateException("The field \"hardware\" must not be null");
        }
        if (version == null) {
            throw new CreateException("The field \"version\" must not be null");
        }

        // TODO add additional validation code, throw CreateException if data is not valid
        setHwid(hwid);
        setVersion(version);
        setMinversion(minversion);
        setUrl(url);

        return null;
    }

    public void ejbPostCreate(Integer hwid, String version, String minversion, String url) {
        // TODO populate relationships here if appropriate
    }

    public abstract String getFilename();

    public abstract void setFilename(String filename);
}
