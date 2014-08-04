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

public abstract class DeviceProfileBean implements EntityBean {

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
    public abstract java.lang.String getName();

    public abstract void setName(java.lang.String key);

    public java.lang.String ejbCreate(java.lang.String key) throws CreateException {
        if (key == null) {
            throw new CreateException("The field \"key\" must not be null");
        }

        // TODO add additional validation code, throw CreateException if data is not valid
        setName(key);

        return null;
    }

    public void ejbPostCreate(java.lang.String key) {
        // TODO populate relationships here if appropriate
    }

    public abstract Integer getInforminterval();

    public abstract void setInforminterval(Integer informinterval);

    public abstract Integer getDayskeepstats();

    public abstract void setDayskeepstats(Integer dayskeepstats);

    public abstract Boolean getSavestats();

    public abstract void setSavestats(Boolean savestats);

    public abstract Boolean getSaveLog();

    public abstract void setSaveLog(Boolean saveLog);

    public abstract Boolean getSaveParamValues();

    public abstract void setSaveParamValues(Boolean saveParamValues);

    public abstract Integer getSaveParamValuesInterval();

    public abstract void setSaveParamValuesInterval(Integer saveParamValuesInterval);

    public abstract Boolean getSaveParamValuesOnChange();

    public abstract void setSaveParamValuesOnChange(Boolean saveParamValuesOnChange);

    public abstract Boolean getSaveParamValuesOnBoot();

    public abstract void setSaveParamValuesOnBoot(Boolean saveParamValuesOnBoot);

    public abstract String getScriptname();

    public abstract void setScriptname(String scriptname);

    public abstract Collection<HostsLocal> getHosts();

    public abstract void setHosts(Collection<HostsLocal> hosts);

    public abstract Collection<ProfilePropertyLocal> getProperties();

    public abstract void setProperties(Collection<ProfilePropertyLocal> properties);

    public abstract String getBaseprofile();

    public abstract void setBaseprofile(String baseprofile);
}
