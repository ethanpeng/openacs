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

public class UploadResponse extends Message {

    /** Creates a new instance of GetParameterValues */
    public UploadResponse() {
        name = "UploadResponse";
    }

    protected void createBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
    }

    protected void parseBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
        StartTime = getRequestElement(spf, body, "StartTime");
        CompleteTime = getRequestElement(spf, body, "CompleteTime");
        Status = Integer.parseInt(getRequestElement(spf, body, "Status"));
    }

    @Override
    public String toString() {
        String s = name + ": StartTime=" + StartTime + " CompleteTIme=" + CompleteTime + " Status=" + Status;
        return s;

    }
    public String StartTime;
    public String CompleteTime;
    public int Status;
}
