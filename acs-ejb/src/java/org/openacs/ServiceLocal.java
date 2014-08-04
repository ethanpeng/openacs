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

public interface ServiceLocal extends EJBLocalObject {

    public static final String TYPE_VOICEPROFILE = "voiceprofile";
    public static final String TYPE_PHONELINE = "phoneline";

    java.lang.Object getId();

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    public abstract Collection<ServicePropertyLocal> getProperties();

    String getType();

    void setType(String type);

    String getDefaultparentservice();

    void setDefaultparentservice(String defaultparentservice);
}
