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
import org.openacs.Message;
import java.util.Iterator;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

public class GetRPCMethodsResponse extends Message {

    /** Creates a new instance of GetRPCMethodsResponse */
    public GetRPCMethodsResponse() {
    }

    public GetRPCMethodsResponse(GetRPCMethods req) {
        this.id = req.getId();
        methods = new String[]{"Inform", "TransferComplete", "GetRPCMethods"};
        name = "GetRPCMethodsResponse";
    }

    @Override
    protected void createBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
        SOAPElement mlst = body.addChildElement(spf.createName("MethodList"));
        mlst.setAttribute(SOAP_ARRAY_TYPE, "xsd:string[" + String.valueOf(methods.length) + "]");
        for (String m : methods) {
            mlst.addChildElement("string").setValue(m);
        }

    }

    @Override
    protected void parseBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
        SOAPElement ml = getRequestChildElement(spf, body, "MethodList");
        int i = getArrayCount(spf, ml);
//        Iterator mlist = ml.getChildElements(spf.createName("string"));
        Iterator mlist = ml.getChildElements();
        //methods = new String [i];
        ArrayList<String> m = new ArrayList<String>();
        i = 0;
        while (mlist.hasNext()) {
            Object e = mlist.next();
            if (e instanceof SOAPElement) {
                SOAPElement el = (SOAPElement) e;
                if (el.getElementQName().getLocalPart().equals("string")) {
//                    methods[i++] = el.getValue();
                    m.add(el.getValue());
                }
            }
        }
        methods = m.toArray(new String[1]);
        /*
        mlist = ml.getChildElements(type.getType(body,spf));
        while (mlist.hasNext()) {
        methods[i++] = ((SOAPElement)mlist.next()).getValue();
        }
         */
    }
    public String[] methods;
}
