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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;

public abstract class SoftwareDetailBean implements EntityBean {

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
    public org.openacs.SoftwareDetailPK ejbCreate(Integer hwid, String version, byte[] paramNames, byte[] methods) throws CreateException {
        setHwid(hwid);
        setVersion(version);
        setParamNames(paramNames);
        setMethods(methods);
        return null;
    }

    public void ejbPostCreate(Integer hwid, String version, byte[] paramNames, byte[] methods) {
        // TODO populate relationships here if appropriate
    }

    public abstract Integer getHwid();

    public abstract void setHwid(Integer hwid);

    public abstract String getVersion();

    public abstract void setVersion(String version);

    public abstract byte[] getParamNames();

    public abstract void setParamNames(byte[] paramNames);

    public abstract byte[] getMethods();

    public abstract void setMethods(byte[] methods);

    public abstract int ejbSelectByHwidAndVersion(Integer hwid, String version) throws FinderException;

    public boolean ejbHomeExists(Integer hwid, String version) {
        try {
            return ejbSelectByHwidAndVersion(hwid, version) == 1;
        } catch (FinderException ex) {
            return false;
        }
    }

    public abstract byte[] getVoicecaps();

    public abstract void setVoicecaps(byte[] voicecaps);

    public Properties getVoiceCaps() {
        byte[] p = getVoicecaps();
        Properties vc = null;
        if (p != null) {
            vc = new Properties();
            try {
                vc.load(new ByteArrayInputStream(p));
            } catch (IOException ex) {
                return null;
            }
        }
        return vc;
    }
}
