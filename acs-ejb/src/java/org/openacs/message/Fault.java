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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import org.openacs.Message;

public class Fault extends Message {

    public class SetParameterValueFault implements Serializable {

        public SetParameterValueFault(String ParameterName, String FaultCode, String FaultString) {
            this.FaultCode = FaultCode;
            this.FaultString = FaultString;
            this.ParameterName = ParameterName;
        }

        @Override
        public String toString() {
            return "SetParameterValueFault: ParameterName=" + ParameterName + " FaultCode=" + FaultCode + " FaultString=" + FaultString;
        }
        public String ParameterName;
        public String FaultCode;
        public String FaultString;
    }

    public class CiscoStatusFault implements Serializable {

        public CiscoStatusFault(String Command, String FaultCode, String FaultString) {
            this.FaultCode = FaultCode;
            this.FaultString = FaultString;
            this.Command = Command;
        }

        @Override
        public String toString() {
            return "CiscoStatusFault: Command=" + Command + " FaultCode=" + FaultCode + " FaultString=" + FaultString;
        }
        public String Command;
        public String FaultCode;
        public String FaultString;
    }

    public Fault() {
        name = "Fault";
    }

    public Fault(String FaultCode, String FaultString, String id) {
        name = "Fault";
        this.faultCodeCwmp = this.faultCode = FaultCode;
        this.faultStringCwmp = this.faultString = FaultString;
        this.id = id;
    }

    protected void createBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
    }

    protected void parseBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
        try {
            faultCode = getRequestElement(spf, body, "faultcode");
        } catch (Exception e) {
        }
        try {
            faultString = getRequestElement(spf, body, "faultstring");
        } catch (Exception e) {
        }
        SOAPElement detail = null;
        try {
            detail = getRequestChildElement(spf, body, "detail");
        } catch (Exception e) {
        }
        if (detail == null) {
            detail = body; // for one broken cpe
        }
        SOAPElement cwmpfault = getRequestChildElement2(spf, detail, "Fault");
        faultCodeCwmp = getRequestElement(spf, cwmpfault, "FaultCode");
        faultStringCwmp = getRequestElement(spf, cwmpfault, "FaultString");

        Iterator i = cwmpfault.getChildElements(spf.createName("SetParameterValuesFault"));
        if (i.hasNext()) {
            SetParameterValuesFaults = new ArrayList<SetParameterValueFault>();
        }
        while (i.hasNext()) {
            SOAPElement f = (SOAPElement) i.next();
            SetParameterValueFault vf = new SetParameterValueFault(
                    getRequestElement(spf, f, "ParameterName"),
                    getRequestElement(spf, f, "FaultCode"),
                    getRequestElement(spf, f, "FaultString"));
            SetParameterValuesFaults.add(vf);
        }
        if (SetParameterValuesFaults != null) {
            for (SetParameterValueFault f : SetParameterValuesFaults) {
                System.out.println("n=" + f.ParameterName + " c=" + f.FaultCode + " s=" + f.FaultString);
            }
        }

        i = cwmpfault.getChildElements(spf.createName("X_00000C_ShowStatusFault", "cwmp_x", "http://wwwin-eng.cisco.com/Eng/IOS/EmbMgmt/CWMP/Mktg/cwmp_vendor_methods"));
        if (i.hasNext()) {
            CiscoStatusFaults = new ArrayList<CiscoStatusFault>();
            System.out.println("Found CISCO status fault");
        }
        while (i.hasNext()) {
            SOAPElement f = (SOAPElement) i.next();
            CiscoStatusFault vf = new CiscoStatusFault(
                    getRequestElement(spf, f, "Command"),
                    getRequestElement(spf, f, "FaultCode"),
                    getRequestElement(spf, f, "FaultString"));
            System.out.println(vf);
            CiscoStatusFaults.add(vf);


        }
    }

    @Override
    public String toString() {
        return "FAULT: code=" + faultCode + " msg=" + faultString + " ccode=" + faultCodeCwmp + " cmsg=" + faultStringCwmp;
    }
    private String faultCode;
    private String faultString;
    private String faultCodeCwmp;
    private String faultStringCwmp;
    public ArrayList<SetParameterValueFault> SetParameterValuesFaults;
    public ArrayList<CiscoStatusFault> CiscoStatusFaults;

    public String getFaultString() {
        return faultString;
    }

    public String getFaultStringCwmp() {
        return faultStringCwmp;
    }

    public String getFaultCode() {
        return faultCode;
    }

    public String getCwmpFaultCode() {
        return faultCodeCwmp;
    }
    public static final String FCODE_REQUEST_DENIED = "9001";
    public static final String FCODE_INTERNAL = "9002";
    public static final String FCODE_INVALID_ARGS = "9003";
    public static final String FCODE_RESOURCE_EXCEEDED = "9004";
    public static final String FCODE_INVALID_PARAMETER_NAME = "9005";
    public static final String FCODE_INVALID_PARAMETER_TYPE = "9006";
    public static final String FCODE_INVALID_PARAMETER_VALUE = "9007";
    public static final String FCODE_PARAMETER_READONLY = "9008";
    public static final String FCODE_NOTIFICATION_REJECTED = "9009";
    public static final String FCODE_DOWNLOAD_FAILURE = "9010";
    public static final String FCODE_UPLOAD_FAILURE = "9011";
    public static final String FCODE_FILE_TRANSFER_AUTHENTICATION_FAILURE = "9012";
    public static final String FCODE_PROTOCOL_NOT_SUPPORTED = "9013";
    public static final String FCODE_DLF_MULTICAST = "9014";
    public static final String FCODE_DLF_NO_CONTACT = "9015";
    public static final String FCODE_DLF_FILE_ACCESS = "9016";
    public static final String FCODE_DLF_UNABLE_TO_COMPLETE = "9017";
    public static final String FCODE_DLF_FILE_CORRUPTED = "9018";
    public static final String FCODE_DLF_FILE_AUTHENTICATION = "9019";
    public static final String FCODE_ACS_METHOD_NOT_SUPPORTED = "8000";
    public static final String FCODE_ACS_REQUEST_DENIED = "8001";
    public static final String FCODE_ACS_INTERNAL_ERROR = "8002";
    public static final String FCODE_ACS_INVALID_ARGS = "8003";
    public static final String FCODE_ACS_RESOURCE_EXCEEDED = "8004";
    public static final String FCODE_ACS_RETRY = "8005";
}
