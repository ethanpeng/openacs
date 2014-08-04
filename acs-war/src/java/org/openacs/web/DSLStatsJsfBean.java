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

import java.sql.Timestamp;
import java.util.Date;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.FinderException;
import org.openacs.DSLStatsLocalHome;
import org.openacs.HostsLocal;
import org.openacs.utils.Ejb;

public class DSLStatsJsfBean {

    /** Creates a new instance of DSLStatsJsfBean */
    public DSLStatsJsfBean() {
        timeFrom = new Date();
        timeFrom.setTime(timeFrom.getTime() - 7 * 24 * 3600 * 1000);
        timeTo = new Date();

    }
    private Date timeFrom;

    /**
     * Get the value of timeFrom
     *
     * @return the value of timeFrom
     */
    public Date getTimeFrom() {
        return timeFrom;
    }

    /**
     * Set the value of timeFrom
     *
     * @param timeFrom new value of timeFrom
     */
    public void setTimeFrom(Date timeFrom) {
        this.timeFrom = timeFrom;
        interval = null;
    }
    protected Date timeTo;

    /**
     * Get the value of timeTo
     *
     * @return the value of timeTo
     */
    public Date getTimeTo() {
        return timeTo;
    }

    /**
     * Set the value of timeTo
     *
     * @param timeTo new value of timeTo
     */
    public void setTimeTo(Date timeTo) {
        this.timeTo = timeTo;
        interval = null;
    }
    private Collection interval = null;

    /**
     * Get the value of interval
     *
     * @return the value of interval
     */
    public Collection getInterval() throws FinderException {
        return interval;
    }
    protected Integer cpeid;

    /**
     * Get the value of cpeid
     *
     * @return the value of cpeid
     */
    public Integer getCpeid() {
        return cpeid;
    }

    /**
     * Set the value of cpeid
     *
     * @param cpeid new value of cpeid
     */
    public void setCpeid(Integer cpeid) {
        this.cpeid = cpeid;
        interval = null;
    }
    protected Integer hwid;

    /**
     * Get the value of hwid
     *
     * @return the value of hwid
     */
    public Integer getHwid() {
        return hwid;
    }

    /**
     * Set the value of hwid
     *
     * @param hwid new value of hwid
     */
    public void setHwid(Integer hwid) {
        this.hwid = hwid;
    }
    protected String sn;

    /**
     * Get the value of sn
     *
     * @return the value of sn
     */
    public String getSn() {
        return sn;
    }

    /**
     * Set the value of sn
     *
     * @param sn new value of sn
     */
    public void setSn(String sn) {
        this.sn = sn;
    }

    public String Load() {
        Timestamp tf = new Timestamp(timeFrom.getTime());
        Timestamp tt = new Timestamp(timeTo.getTime());
        try {
//            System.out.println ("DSLstats: hwid="+hwid+" sn="+sn+" tf="+tf+" tt="+tt);
            HostsLocal h = Ejb.lookupHostsBean().findByHwidAndSn(hwid, sn);
            DSLStatsLocalHome s = Ejb.lookupDSLStatsBean();
            interval = s.findByCpeAndTime2((Integer) h.getId(), tf, tt);
        } catch (FinderException ex) {
            Logger.getLogger(DSLStatsJsfBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
