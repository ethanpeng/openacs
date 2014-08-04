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

public class X_00000C_SetConfiguration extends Message {

    public X_00000C_SetConfiguration() {
        name = "X_00000C_SetConfiguration";
    }

    @Override
    protected void createBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
        body.addChildElement(spf.createName("ErrorOption")).setValue(ErrorOption);
        body.addChildElement(spf.createName("Target")).setValue(Target);
        SOAPElement elm = body.addChildElement(spf.createName("ConfigCommandList"));
        elm.setAttribute(SOAP_ARRAY_TYPE, "xsd:string[" + String.valueOf(ConfigCommandList.size()) + "]");
        for (int i = 0; i < ConfigCommandList.size(); i++) {
            SOAPElement s = elm.addChildElement("string");
            s.setValue(ConfigCommandList.get(i));
        }
        body.addChildElement(spf.createName("ParameterKey")).setValue(ParameterKey);
    }

    @Override
    protected void parseBody(SOAPBodyElement body, SOAPFactory f) throws SOAPException {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public void addCommand(String cmd) {
        ConfigCommandList.add(cmd);
    }
    protected String ErrorOption = "rollback";

    public void setErrorOption(String ErrorOption) {
        this.ErrorOption = ErrorOption;
    }
    protected String Target = "running-config";

    public void setTarget(String Target) {
        this.Target = Target;
    }
    protected String ParameterKey = "unsetparamkey";

    public void setParameterKey(String ParameterKey) {
        this.ParameterKey = ParameterKey;
    }
    private ArrayList<String> ConfigCommandList = new ArrayList<String>();
}
