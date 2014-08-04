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

public class HostPropertyPK {

    public Integer parentId;
    public String name;

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public HostPropertyPK() {
    }

    public HostPropertyPK(Integer parentId, String name) {
        this.parentId = parentId;
        this.name = name;
    }

    @Override
    public boolean equals(java.lang.Object otherOb) {

        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof org.openacs.HostPropertyPK)) {
            return false;
        }
        org.openacs.HostPropertyPK other = (org.openacs.HostPropertyPK) otherOb;
        return ((parentId == null ? other.parentId == null : parentId == other.parentId)
                && (name == null ? other.name == null : name.equals(other.name)));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ((parentId == null ? 0 : parentId.hashCode())
                ^ (name == null ? 0 : name.hashCode()));
    }
}
