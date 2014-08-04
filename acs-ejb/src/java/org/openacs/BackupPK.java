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

public class BackupPK {

    public java.lang.Integer hostid;
    public java.lang.Integer type;
    public Timestamp time;

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public BackupPK() {
    }

    public BackupPK(Integer hostid, Timestamp time, Integer type) {
        this.hostid = hostid;
        this.time = time;
        this.type = type;
    }

    @Override
    public boolean equals(java.lang.Object otherOb) {

        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof org.openacs.BackupPK)) {
            return false;
        }
        org.openacs.BackupPK other = (org.openacs.BackupPK) otherOb;
        return ((hostid == null ? other.hostid == null : hostid == other.hostid)
                && (type == null ? other.type == null : type == other.type)
                && (time == null ? other.time == null : time.equals(other.time)));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ((hostid == null ? 0 : hostid.hashCode())
                ^ (time == null ? 0 : time.hashCode())
                ^ (type == null ? 0 : type.hashCode()));
    }
}
