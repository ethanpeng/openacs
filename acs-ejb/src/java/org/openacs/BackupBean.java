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

import java.sql.Timestamp;
import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;


public abstract class BackupBean implements EntityBean {

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
    
    public org.openacs.BackupPK ejbCreate(Integer hostid, Integer type, Timestamp time, byte [] cfg)  throws CreateException {
        setHostid(hostid);
        setType(type);
        setTime(time);
        setCfg(cfg);
        // TODO add additional validation code, throw CreateException if data is not valid
        return null;
    }

    public void ejbPostCreate(Integer hostid, Integer type, Timestamp time, byte [] cfg) {
        // TODO populate relationships here if appropriate
    }

    public abstract Integer getHostid();

    public abstract void setHostid(Integer hostid);

    public abstract Integer getType();

    public abstract void setType(Integer type);

    public abstract Timestamp getTime();

    public abstract void setTime(Timestamp time);

    public abstract byte [] getCfg();

    public abstract void setCfg(byte [] cfg);

    public abstract Timestamp ejbSelectByHostid(Integer hostid) throws FinderException;

    public Timestamp ejbHomeGetTimeOfLastBackup(Integer hostid) throws FinderException {
        return ejbSelectByHostid(hostid);
    }

}
