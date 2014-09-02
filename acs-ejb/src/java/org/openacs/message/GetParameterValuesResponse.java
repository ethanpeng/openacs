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
package org.openacs.message;

import org.openacs.Message;
import java.util.Hashtable;
import java.util.Map.Entry;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

public class GetParameterValuesResponse extends Message {

    /** Creates a new instance of GetParameterValuesResponse */
    public GetParameterValuesResponse() {
        name = "GetParameterValuesResponse";
    }

    protected void createBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
    }

    protected void parseBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
        values = parseParamList(body, spf);
    }
    public Hashtable<String, String> values;

    public Integer getParamInt(String name) {
        String v = values.get(name);
        if (v != null) {
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) {
            }
        }
        return null;
    }

    public Integer getParamInt(String name, int defaultValue) {
        Integer value = getParamInt(name);
        return (value != null) ? value : defaultValue;
    }

    public String getParam(String name) {
        return values.get(name);
    }

    public String getParam(String name, String defaultValue) {
        String value = getParam(name);
        return (value != null) ? value : defaultValue;
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer(1024);
        for (Entry<String, String> e : values.entrySet()) {
            b.append(e.getKey());
            b.append("=");
            b.append(e.getValue());
            b.append("\n");
        }
        return b.toString();

    }
}
