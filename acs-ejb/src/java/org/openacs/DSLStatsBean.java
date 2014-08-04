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

public abstract class DSLStatsBean implements EntityBean {

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
    public abstract Timestamp getTime();

    public abstract void setTime(Timestamp time);

    public abstract Integer getHostid();

    public abstract void setHostid(Integer hostid);

    public org.openacs.DSLStatsPK ejbCreate(Integer hostid, Timestamp time) throws CreateException {
        if (hostid == null) {
            throw new CreateException("The field \"hostid\" must not be null");
        }
        if (time == null) {
            throw new CreateException("The field \"time\" must not be null");
        }

        setHostid(hostid);
        setTime(time);

        return null;
    }

    public org.openacs.DSLStatsPK ejbCreate(Integer hostid, Timestamp time,
            Integer DownstreamAttenuation,
            Integer DownstreamCurrRate,
            Integer DownstreamMaxRate,
            Integer DownstreamNoiseMargin,
            Integer DownstreamPower,
            Integer UpstreamAttenuation,
            Integer UpstreamCurrRate,
            Integer UpstreamMaxRate,
            Integer UpstreamNoiseMargin,
            Integer UpstreamPower,
            String Status,
            String ModulationType) throws CreateException {
        if (hostid == null) {
            throw new CreateException("The field \"hostid\" must not be null");
        }
        if (time == null) {
            throw new CreateException("The field \"time\" must not be null");
        }

        setHostid(hostid);
        setTime(time);
        setDownstreamAttenuation(DownstreamAttenuation);
        setDownstreamCurrRate(DownstreamCurrRate);
        setDownstreamMaxRate(DownstreamMaxRate);
        setDownstreamNoiseMargin(DownstreamNoiseMargin);
        setDownstreamPower(DownstreamPower);
        setUpstreamAttenuation(UpstreamAttenuation);
        setUpstreamCurrRate(UpstreamCurrRate);
        setUpstreamMaxRate(UpstreamMaxRate);
        setUpstreamNoiseMargin(UpstreamNoiseMargin);
        setUpstreamPower(UpstreamPower);
        setStatus(Status);
        setModulationType(ModulationType);

        return null;
    }

    public void ejbPostCreate(Integer hostid, Timestamp time) {
    }

    public void ejbPostCreate(Integer hostid, Timestamp time,
            Integer DownstreamAttenuation,
            Integer DownstreamCurrRate,
            Integer DownstreamMaxRate,
            Integer DownstreamNoiseMargin,
            Integer DownstreamPower,
            Integer UpstreamAttenuation,
            Integer UpstreamCurrRate,
            Integer UpstreamMaxRate,
            Integer UpstreamNoiseMargin,
            Integer UpstreamPower,
            String Status,
            String ModulationType) {
    }

    public abstract Integer getDownstreamAttenuation();

    public abstract void setDownstreamAttenuation(Integer DownstreamAttenuation);

    public abstract Integer getDownstreamCurrRate();

    public abstract void setDownstreamCurrRate(Integer DownstreamCurrRate);

    public abstract Integer getDownstreamMaxRate();

    public abstract void setDownstreamMaxRate(Integer DownstreamMaxRate);

    public abstract Integer getDownstreamNoiseMargin();

    public abstract void setDownstreamNoiseMargin(Integer DownstreamNoiseMargin);

    public abstract Integer getDownstreamPower();

    public abstract void setDownstreamPower(Integer DownstreamPower);

    public abstract Integer getUpstreamAttenuation();

    public abstract void setUpstreamAttenuation(Integer UpstreamAttenuation);

    public abstract Integer getUpstreamCurrRate();

    public abstract void setUpstreamCurrRate(Integer UpstreamCurrRate);

    public abstract Integer getUpstreamMaxRate();

    public abstract void setUpstreamMaxRate(Integer UpstreamMaxRate);

    public abstract Integer getUpstreamNoiseMargin();

    public abstract void setUpstreamNoiseMargin(Integer UpstreamNoiseMargin);

    public abstract Integer getUpstreamPower();

    public abstract void setUpstreamPower(Integer UpstreamPower);

    public abstract String getStatus();

    public abstract void setStatus(String Status);

    public abstract String getModulationType();

    public abstract void setModulationType(String ModulationType);
}
