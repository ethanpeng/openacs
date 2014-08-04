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
import javax.ejb.EJBLocalObject;

public interface DSLStatsLocal extends EJBLocalObject {

    public abstract Timestamp getTime();

    public abstract Integer getHostid();

    Integer getDownstreamAttenuation();

    void setDownstreamAttenuation(Integer DownstreamAttenuation);

    Integer getDownstreamCurrRate();

    void setDownstreamCurrRate(Integer DownstreamCurrRate);

    Integer getDownstreamMaxRate();

    void setDownstreamMaxRate(Integer DownstreamMaxRate);

    Integer getDownstreamNoiseMargin();

    void setDownstreamNoiseMargin(Integer DownstreamNoiseMargin);

    Integer getDownstreamPower();

    void setDownstreamPower(Integer DownstreamPower);

    Integer getUpstreamAttenuation();

    void setUpstreamAttenuation(Integer UpstreamAttenuation);

    Integer getUpstreamCurrRate();

    void setUpstreamCurrRate(Integer UpstreamCurrRate);

    Integer getUpstreamMaxRate();

    void setUpstreamMaxRate(Integer UpstreamMaxRate);

    Integer getUpstreamNoiseMargin();

    void setUpstreamNoiseMargin(Integer UpstreamNoiseMargin);

    Integer getUpstreamPower();

    void setUpstreamPower(Integer UpstreamPower);

    String getStatus();

    void setStatus(String Status);

    String getModulationType();

    void setModulationType(String ModulationType);
}
