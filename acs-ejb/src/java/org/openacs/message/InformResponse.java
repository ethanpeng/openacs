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

public class InformResponse extends Message {

    /** Creates a new instance of InformResponse */
    public InformResponse() {
        name = "InformResponse";
    }

    public InformResponse(String _id, int me) {
        name = "InformResponse";
        id = _id;
        MaxEnvelopes = me;
    }

    protected void createBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
        body.addChildElement(spf.createName("MaxEnvelopes")).setValue(String.valueOf(MaxEnvelopes));
    }

    protected void parseBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
    }
    public int MaxEnvelopes;
}
