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

public class ServicePropertyPK {

    public Integer serviceid;
    public String name;

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public ServicePropertyPK() {
    }

    public ServicePropertyPK(Integer parentId, String name) {
        this.serviceid = parentId;
        this.name = name;
    }

    @Override
    public boolean equals(java.lang.Object otherOb) {

        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof org.openacs.ServicePropertyPK)) {
            return false;
        }
        org.openacs.ServicePropertyPK other = (org.openacs.ServicePropertyPK) otherOb;
        return ((serviceid == null ? other.serviceid == null : serviceid == other.serviceid)
                && (name == null ? other.name == null : name.equals(other.name)));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ((serviceid == null ? 0 : serviceid.hashCode())
                ^ (name == null ? 0 : name.hashCode()));
    }
}
