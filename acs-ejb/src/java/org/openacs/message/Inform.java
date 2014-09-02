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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

public class Inform extends Message {

    public class Event implements Entry<String, String> {

        private String event;
        private String cmdKey;

        public Event(String event, String cmdKey) {
            this.event = event;
            this.cmdKey = cmdKey;
        }

        public String getKey() {
            return event;
        }

        public String getValue() {
            return cmdKey;
        }

        public String setValue(String value) {
            return cmdKey = value;
        }
    }

    /** Creates a new instance of Inform */
    public Inform() {
    }

    protected void createBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
    }

    protected void parseBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
        SOAPElement deviceid = getRequestChildElement(spf, body, "DeviceId");
        defns = deviceid.getNamespaceURI();

        oui = getRequestElement(spf, deviceid, "OUI");
        sn = getRequestElement(spf, deviceid, "SerialNumber");
        Manufacturer = getRequestElement(spf, deviceid, "Manufacturer");
        ProductClass = getRequestElement(spf, deviceid, "ProductClass");
        if (ProductClass == null) {
            ProductClass = "";
        }
        MaxEnvelopes = Integer.parseInt(getRequestElement(spf, body, "MaxEnvelopes"));
        RetryCount = Integer.parseInt(getRequestElement(spf, body, "RetryCount"));
        CurrentTime = getRequestElement(spf, body, "CurrentTime");

        Iterator pi = getRequestChildElement(spf, body, "ParameterList").getChildElements(spf.createName("ParameterValueStruct"));
        /*
        //System.out.println ("pi.hasNext: "+pi.hasNext());
        Iterator pii = getRequestChildElement (spf, body, "ParameterList").getChildElements(new QName("ParameterValueStruct"));
        //System.out.println ("pii.hasNext: "+pii.hasNext());
        pii = getRequestChildElement (spf, body, "ParameterList").getChildElements(spf.createName ("ParameterValueStruct","cwmp",defns));
        System.out.println ("pii.hasNext: "+pii.hasNext());
        pii = getRequestChildElement (spf, body, "ParameterList").getChildElements(spf.createName ("ParameterValueStruct","cwmp",URN_CWMP));
        System.out.println ("pii.hasNext: "+pii.hasNext());
         */
        Name nameKey = spf.createName("Name");
        Name nameValue = spf.createName("Value");
        params = new Hashtable<String, String>();
        while (pi.hasNext()) {
            SOAPElement param = (SOAPElement) pi.next();
            String key = getRequestElement(param, nameKey);
            if (root == null && !key.startsWith(".")) {
                if (key.startsWith("Device.")) {
                    root = "Device";
                } else if (key.startsWith("InternetGatewayDevice.")) {
                    root = "InternetGatewayDevice";
                } else {
                    throw new RuntimeException("Invalid root. Must be InternetGatewayDevice or Device: " + key);
                }
            }
            String value = "";
            try {
                value = getRequestElement(param, nameValue);
            } catch (Exception e) {
            }
            if (value == null) {
                value = "";
            }
            params.put(key, value);
        }

        if (root == null) {
            throw new RuntimeException("Invalid root. Must be InternetGatewayDevice or Device");
        }

        pi = getRequestChildElement(spf, body, "Event").getChildElements(spf.createName("EventStruct"));
        Name eventCode = spf.createName("EventCode");
        Name commandKey = spf.createName(COMMAND_KEY);
        events = new LinkedHashSet<Entry<String, String>>();
        while (pi.hasNext()) {
            SOAPElement param = (SOAPElement) pi.next();
            String event = getRequestElement(param, eventCode);
            String cmdKey = getRequestElement(param, commandKey);
            System.out.println("EVENT: " + event + "[" + cmdKey + "]");
            if (cmdKey == null) {
                cmdKey = "";
            }
            events.add(new Event(event, cmdKey));
        }

    }

    public String getSoftwareVersion() {
        String v = params.get(root + ".DeviceInfo.SoftwareVersion");
        if (v != null) {
            v = v.replace('-', '.');
            v = v.replace(',', ' ');
        }
        return v;
    }

    public String getHardwareVersion() {
        return params.get(root + ".DeviceInfo.HardwareVersion");
    }

    public String getConfigVersion() {
        return params.get(root + ".DeviceInfo.VendorConfigFile.1.Version");
    }

    public String getURL() {
        String url = params.get(root + ".ManagementServer.ConnectionRequestURL");
        if (url != null) {
            return url;
        }
        url = params.get(root + ".ManagementServer.UDPConnectionRequestAddress");
        if (url != null) {
            url = (url.indexOf(':') == -1) ? "udp://" + url + ":80" : "udp://" + url;
        }
        return url;
    }

    public String getConreqUser() {
        return params.get(root + ".ManagementServer.ConnectionRequestUsername");
    }

    public String getConreqPass() {
        return params.get(root + ".ManagementServer.ConnectionRequestPassword");
    }

    public String getProvisiongCode() {
        return params.get(root + ".DeviceInfo.ProvisioningCode");
    }

    public void setProvisiongCode(String code) {
        params.put(root + ".DeviceInfo.ProvisioningCode", code);
    }

    public String getRoot() {
        return root;
    }

    public boolean isEvent(String event) {
        for (Entry<String, String> e : events) {
            if (e.getKey().equals(event)) {
                return true;
            }
        }
        return false;
    }

    public Set<Entry<String, String>> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(1024);
        s.append("Inform:\n");
        println(s, "\toui: ", oui);
        println(s, "\tsn: ", sn);
        println(s, "\tManufacturer: ", Manufacturer);

        s.append("\tEvents:\n");
        for (Entry<String, String> ev : events) {
            println(s, "\t\t", ev.getKey(), ev.getValue());
        }

        s.append("\tParams:\n");
        for (String k : params.keySet()) {
            println(s, "\t\t", k, params.get(k));
        }
        return s.toString();
    }

    public String getOui() {
        return oui;
    }

    public void setOui(String oui) {
        this.oui = oui;
    }
    private String oui;
    public String sn;
    public String ProductClass;
    public String Manufacturer;
    public int RetryCount;
    public String CurrentTime;
    public Hashtable<String, String> params;
    private Set<Entry<String, String>> events;
    public int MaxEnvelopes;
    public String defns;
    private String root = null;
    public static final String EVENT_BOOT_STRAP = "0 BOOTSTRAP";
    public static final String EVENT_BOOT = "1 BOOT";
    public static final String EVENT_PERIODIC = "2 PERIODIC";
    public static final String EVENT_SCHEDULED = "3 SCHEDULED";
    public static final String EVENT_VALUE_CHANGE = "4 VALUE CHANGE";
    public static final String EVENT_KICKED = "5 KICKED";
    public static final String EVENT_CONNECTION_REQUEST = "6 CONNECTION REQUEST";
    public static final String EVENT_TRANSFER_COMPLETE = "7 TRANSFER COMPLETE";
}
