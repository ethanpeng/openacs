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

import java.util.ArrayList;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import org.openacs.Message;

public class X_00000C_ShowStatus extends Message {

    public X_00000C_ShowStatus() {
        name = "X_00000C_ShowStatus";
    }

    @Override
    protected void createBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
        SOAPElement elm = body.addChildElement(spf.createName("ExecCommandList"));
        elm.setAttribute(SOAP_ARRAY_TYPE, "xsd:string[" + String.valueOf(ExecCommandList.size()) + "]");
        for (int i = 0; i < ExecCommandList.size(); i++) {
            SOAPElement s = elm.addChildElement("string");
            s.setValue(ExecCommandList.get(i));
//            s.setAttribute("xsi:type","xsd:string");
        }
    }

    @Override
    protected void parseBody(SOAPBodyElement body, SOAPFactory f) throws SOAPException {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public void addCommand(String cmd) {
        ExecCommandList.add(cmd);
    }
    private ArrayList<String> ExecCommandList = new ArrayList<String>();
}
