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
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

public class GetParameterNames extends Message {

    /** Creates a new instance of GetParameterNames */
    public GetParameterNames() {
        name = "GetParameterNames";
        parameterPath = ".";
        nextLevel = false;
    }

    public GetParameterNames(String parameterPath, boolean nextLevel) {
        this();
        this.parameterPath = parameterPath;
        this.nextLevel = nextLevel;
    }

    protected void createBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
        body.addChildElement(spf.createName("ParameterPath")).setValue(parameterPath);
        body.addChildElement(spf.createName("NextLevel")).setValue(nextLevel ? "1" : "0");
    }

    protected void parseBody(SOAPBodyElement body, SOAPFactory f) throws SOAPException {
    }
    public String parameterPath;
    public boolean nextLevel;
}
