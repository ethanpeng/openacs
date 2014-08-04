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
import javax.ejb.EJBLocalObject;

public interface DeviceProfileLocal extends EJBLocalObject {

    java.lang.String getName();

    Integer getInforminterval();

    void setInforminterval(Integer informinterval);

    Integer getDayskeepstats();

    void setDayskeepstats(Integer dayskeepstats);

    Boolean getSavestats();

    void setSavestats(Boolean savestats);

    Boolean getSaveLog();

    void setSaveLog(Boolean saveLog);

    Boolean getSaveParamValues();

    void setSaveParamValues(Boolean saveParamValues);

    Integer getSaveParamValuesInterval();

    void setSaveParamValuesInterval(Integer saveParamValuesInterval);

    Boolean getSaveParamValuesOnChange();

    void setSaveParamValuesOnChange(Boolean saveParamValuesOnChange);

    Boolean getSaveParamValuesOnBoot();

    void setSaveParamValuesOnBoot(Boolean saveParamValuesOnBoot);

    String getScriptname();

    void setScriptname(String scriptname);

    Collection<HostsLocal> getHosts();

    Collection<ProfilePropertyLocal> getProperties();

    String getBaseprofile();

    void setBaseprofile(String baseprofile);
}
