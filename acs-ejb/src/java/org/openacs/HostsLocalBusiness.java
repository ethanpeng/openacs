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

/**
 * This is the business interface for Hosts enterprise bean.
 */
public interface HostsLocalBusiness {

    public abstract String getOui();

    public abstract String getSerialno();

    public abstract String getUrl();

    public abstract void setUrl(String url);

    void setConfigname(String configname);

    String getConfigname();

    void setCurrentsoftware(String currentsoftware);

    String getCurrentsoftware();

    void setSfwupdtime(Timestamp sfwupdtime);

    Timestamp getSfwupdtime();

    void setSfwupdres(String sfwupdres);

    String getSfwupdres();

    void setCfgupdres(String cfgupdres);

    String getCfgupdres();

    void setLastcontact(Timestamp lastcontact);

    Timestamp getLastcontact();

    void setCfgupdtime(Timestamp cfgupdtime);

    Timestamp getCfgupdtime();

    void setHardware(java.lang.String hardware);

    java.lang.String getHardware();

    void setCfgversion(java.lang.String cfgversion);

    java.lang.String getCfgversion();

    Object getId();

    public void setId(Object id);
}
