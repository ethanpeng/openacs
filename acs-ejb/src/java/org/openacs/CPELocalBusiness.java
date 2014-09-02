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

import org.openacs.message.*;

/**
 * This is the business interface for CPE enterprise bean.
 */
public interface CPELocalBusiness {

    Message FactoryReset(HostsLocal host);

    GetRPCMethodsResponse GetRPCMethods(HostsLocal host);

    void RequestCPEConnection(HostsLocal host);

    GetParameterNamesResponse GetParameterNames(HostsLocal host, String path, boolean next);

    GetParameterValuesResponse GetParameterValues(HostsLocal host, String[] names);

    SetParameterValuesResponse SetParameterValues(HostsLocal host, SetParameterValues values);

    public Message Call(HostsLocal host, Message call, long timeout);
}
