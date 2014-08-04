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
import java.util.Iterator;
import org.openacs.Message;
import javax.xml.soap.*;

public class GetOptionsResponse extends Message {

    public class OptionStruct {

        public String OptionName;
        public String VoucherSN;
        public int State;
        public int Mode;
        public String StartDate;
        public String ExpirationDate;
        public boolean IsTransferable;
    }

    public GetOptionsResponse() {
        name = "GetOptionsResponse";
    }

    protected void createBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
    }

    protected void parseBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
        Iterator pi = getRequestChildElement(spf, body, "OptionList").getChildElements(spf.createName("OptionStruct"));
        Name nameOptionName = spf.createName("OptionName");
        Name nameVoucherSN = spf.createName("VoucherSN");
        Name nameState = spf.createName("State");
        Name nameMode = spf.createName("Mode");
        Name nameStartDate = spf.createName("StartDate");
        Name nameExpirationDate = spf.createName("ExpirationDate");
        Name nameIsTransferable = spf.createName("IsTransferable");

        OptionList = new ArrayList<OptionStruct>();

        while (pi.hasNext()) {
            SOAPElement option = (SOAPElement) pi.next();
            OptionStruct o = new OptionStruct();
            o.OptionName = getRequestElement(option, nameOptionName);
            o.VoucherSN = getRequestElement(option, nameVoucherSN);
            o.State = Integer.parseInt(getRequestElement(option, nameState));
            o.Mode = Integer.parseInt(getRequestElement(option, nameMode));
            o.StartDate = getRequestElement(option, nameStartDate);
            o.ExpirationDate = getRequestElement(option, nameExpirationDate);
            o.IsTransferable = Boolean.parseBoolean(getRequestElement(option, nameIsTransferable));
            OptionList.add(o);
        }
    }
    public ArrayList<OptionStruct> OptionList;
}
