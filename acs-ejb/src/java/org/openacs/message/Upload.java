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

public class Upload extends Message {

    /** Creates a new instance of Upload */
    public Upload() {
        name = "Upload";
        Username = "";
        Password = "";
        DelaySeconds = 0;
        FileType = FT_CONFIG;
        URL = "http://192.168.1.1:8080/acs-war/upload/tst.cfg";
        CommandKey = "default.command.key";
    }

    protected void createBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
        body.addChildElement(COMMAND_KEY).setValue(CommandKey);
        body.addChildElement("FileType").setValue(FileType);

        body.addChildElement("URL").setValue(URL);
        body.addChildElement("Username").setValue(Username);
        body.addChildElement("Password").setValue(Password);
        body.addChildElement("DelaySeconds").setValue(String.valueOf(DelaySeconds));
    }

    protected void parseBody(SOAPBodyElement body, SOAPFactory f) throws SOAPException {
    }
    public String CommandKey;
    public String FileType;
    public String URL;
    public String Username;
    public String Password;
    public int DelaySeconds;
    public static final String FT_CONFIG = "1 Vendor Configuration File";
    public static final String FT_LOG = "2 Vendor Log File";
}
