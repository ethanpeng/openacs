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

/**
 * This is the business interface for Configuration enterprise bean.
 */
public interface ConfigurationLocalBusiness {
    /*
    void setHardware(Integer hardware);
    
    Integer getHardware();
     */

    HardwareModelLocal getHardware();

    void setHardware(HardwareModelLocal hwmodel);

    void setConfig(byte[] config);

    byte[] getConfig();

    void setName(String name);

    String getName();

    void setFilename(java.lang.String filename);

    java.lang.String getFilename();

    void setVersion(java.lang.String version);

    java.lang.String getVersion();
}
