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

import java.util.Arrays;
import org.openacs.Message;
import java.util.Hashtable;
import java.util.Map.Entry;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

public class GetParameterNamesResponse extends Message {

    /** Creates a new instance of GetParameterNamesResponse */
    public GetParameterNamesResponse() {
        name = "GetParameterNamesResponse";
    }

    @Override
    protected void createBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
    }

    @Override
    protected void parseBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
        names = parseParamList(body, spf, "ParameterInfoStruct", "Writable");
    }

    public int[] getMultiInstanceNames(String prefix) {
        int[] r = new int[names.size()];
        int ix = 0;
        int pfxlength = prefix.length();
        for (Entry<String, String> e : names.entrySet()) {
            String k = e.getKey();
            String n;
            if (k.endsWith(".")) {
                n = k.substring(pfxlength, k.length() - 1);
            } else {
                n = k.substring(pfxlength);
            }
            System.out.println("Name: " + n);
            r[ix++] = Integer.parseInt(n);
        }
        Arrays.sort(r);
        return r;
    }
    public Hashtable<String, String> names;
}
