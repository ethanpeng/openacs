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

public class Host2ServicePK {

    public Integer hostid;
    public Integer serviceid;
    public Integer instance;

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public Host2ServicePK() {
    }

    public Host2ServicePK(Integer hostId, Integer serviceId, Integer instance) {
        this.hostid = hostId;
        this.serviceid = serviceId;
        this.instance = instance;
    }

    @Override
    public boolean equals(java.lang.Object otherOb) {

        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof org.openacs.Host2ServicePK)) {
            return false;
        }
        org.openacs.Host2ServicePK other = (org.openacs.Host2ServicePK) otherOb;
        return ((hostid == null ? other.hostid == null : hostid == other.hostid)
                && (serviceid == null ? other.serviceid == null : serviceid == other.serviceid)
                && (instance == null ? other.instance == null : instance == other.instance));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ((hostid == null ? 0 : hostid.hashCode())
                ^ (serviceid == null ? 0 : serviceid.hashCode())
                ^ (instance == null ? 0 : instance.hashCode()));
    }
}
