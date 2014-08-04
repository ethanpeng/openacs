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

import java.util.Hashtable;
import java.util.Iterator;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import org.openacs.Message;

public class X_00000C_ShowStatusResponse extends Message {

    public X_00000C_ShowStatusResponse() {
        name = "X_00000C_ShowStatusResponse";
    }

    @Override
    protected void createBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    protected void parseBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
        Iterator pi = getRequestChildElement(spf, body, "ExecResponseList").getChildElements(spf.createName("ExecResponseStruct"));
        Name nameKey = spf.createName("Command");
        Name nameValue = spf.createName("Response");
        while (pi.hasNext()) {
            SOAPElement param = (SOAPElement) pi.next();
            String key = getRequestElement(param, nameKey);
            String value = getRequestElement(param, nameValue);
            if (value == null) {
                value = "";
            }
            System.out.append(key + "->" + value);
            response.put(key, value);
        }
    }
    public Hashtable<String, String> response = new Hashtable<String, String>();
}
