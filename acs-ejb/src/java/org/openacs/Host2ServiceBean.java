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

public abstract class Host2ServiceBean implements EntityBean {

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
    public abstract Integer getServiceid();

    public abstract void setServiceid(Integer serviceid);

    public Host2ServicePK ejbCreate(Integer hostid, Integer serviceid, Integer instance) throws CreateException {
        if (hostid == null) {
            throw new CreateException("The field \"hostid\" must not be null");
        }
        if (serviceid == null) {
            throw new CreateException("The field \"serviceid\" must not be null");
        }
        if (instance == null) {
            throw new CreateException("The field \"instance\" must not be null");
        }

        // TODO add additional validation code, throw CreateException if data is not valid
        setHostid(hostid);
        setInstance(instance);
        setServiceid(serviceid);

        return null;
    }

    public void ejbPostCreate(Integer hostid, Integer serviceid, Integer instance) {
        // TODO populate relationships here if appropriate
    }

    public abstract Integer getHostid();

    public abstract void setHostid(Integer hostid);

    public abstract Integer getInstance();

    public abstract void setInstance(Integer instance);

    public abstract ServiceLocal getService();

    public abstract void setService(ServiceLocal svc);

    public abstract Integer getParentServiceId();

    public abstract void setParentServiceId(Integer parentServiceId);

    public abstract Integer getParentServiceInstance();

    public abstract void setParentServiceInstance(Integer parentServiceInstance);
}
