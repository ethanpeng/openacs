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

public interface HostsLocal extends javax.ejb.EJBLocalObject, org.openacs.HostsLocalBusiness {

    byte[] getProps();

    void setProps(byte[] props);

    Integer getHwid();

    void setHwid(Integer hwid);

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    Integer getAuthtype();

    void setAuthtype(Integer authtype);

    HardwareModelLocal getModel();

    void setModel(HardwareModelLocal m);

    String getCustomerid();

    void setCustomerid(String customerid);

    String getConrequser();

    void setConrequser(String conrequser);

    String getConreqpass();

    void setConreqpass(String conreqpass);

    void RequestConnection(int timeout) throws Exception;

    Boolean getCfgforce();

    void setCfgforce(Boolean cfgforce);

    String getProfileName();

    void setProfileName(String profileName);

    Collection<HostPropertyLocal> getProperties();

    void setProperties(Collection<HostPropertyLocal> properties);

    DeviceProfileLocal getProfile();

    Collection<Host2ServiceLocal> getServices();

    Boolean getForcePasswords();

    void setForcePasswords(Boolean forcePasswords);

    Boolean getReboot();

    void setReboot(Boolean reboot);
}
