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

public class DeviceProfile2SoftwarePK {

    public java.lang.Integer hwid;
    public java.lang.String profileName;

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public DeviceProfile2SoftwarePK() {
    }

    public DeviceProfile2SoftwarePK(Integer hwid, String profilename) {
        this.hwid = hwid;
        this.profileName = profilename;
    }

    @Override
    public boolean equals(java.lang.Object otherOb) {

        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof org.openacs.DeviceProfile2SoftwarePK)) {
            return false;
        }
        org.openacs.DeviceProfile2SoftwarePK other = (org.openacs.DeviceProfile2SoftwarePK) otherOb;
        return ((hwid == null ? other.hwid == null : hwid == other.hwid)
                && (profileName == null ? other.profileName == null : profileName.equals(other.profileName)));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ((hwid == null ? 0 : hwid.hashCode())
                ^ (profileName == null ? 0 : profileName.hashCode()));
    }

    @Override
    public String toString() {
        return "hwid=" + hwid + " name=" + profileName;
    }
}
