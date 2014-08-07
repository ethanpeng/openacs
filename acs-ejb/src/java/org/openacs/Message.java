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
package org.openacs;

import java.io.*;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.soap.*;
import org.openacs.message.Fault;

abstract public class Message implements Serializable {

    /** Creates a new instance of Message */
    public Message() {
        //id = "intrnl.unset.id."+((name!=null) ? name : "") +(Calendar.getInstance().getTimeInMillis()+3600*1000);
    }

    abstract protected void createBody(SOAPBodyElement body, SOAPFactory spf) throws SOAPException;

    abstract protected void parseBody(SOAPBodyElement body, SOAPFactory f) throws SOAPException;

    protected class ArrayType {

        public ArrayType() {
        }
        private String type;

        public String getType() {
            return type;
        }

        public Name getType(SOAPBodyElement body, SOAPFactory spf) throws SOAPException {
            int i = type.indexOf(':');
            if (i == -1) {
                return spf.createName(type);
            } else {
                String prefix = type.substring(0, i);
                SOAPBody b = (SOAPBody) body.getParentElement();
                SOAPEnvelope e = (SOAPEnvelope) b.getParentElement();
                SOAPHeader h = e.getHeader();
                String uri = null;
                if (uri == null) {
                    try {
                        uri = h.lookupNamespaceURI(prefix);
                    } catch (Exception ee) {
                    }
                }
                if (uri == null) {
                    try {
                        uri = e.lookupNamespaceURI(prefix);
                    } catch (Exception ee) {
                    }
                }
                if (uri == null) {
                    try {
                        uri = b.lookupNamespaceURI(prefix);
                    } catch (Exception ee) {
                    }
                }
                return spf.createName(type.substring(i + 1), prefix, uri);
            }
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    static public SOAPBodyElement getRequest(SOAPMessage msg) throws SOAPException {
        SOAPBodyElement request = null;
        Iterator i1 = msg.getSOAPBody().getChildElements();
        while (i1.hasNext()) {
            Node n = (Node) i1.next();
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                request = (SOAPBodyElement) n;
            }
        }
        return request;
    }

    static private String getRequestName(SOAPMessage msg) throws SOAPException {
        if (msg.getSOAPBody().hasFault()) {
            return "Fault";
        }
        String name = getRequest(msg).getNodeName();
        if (name.startsWith("cwmp:")) {
            name = name.substring(5);
        } else if (name.startsWith("cwmp_x:")) {
            name = name.substring(7);
        }
        return name;
    }

    static public Message Parse(SOAPMessage soapMsg) throws SOAPException, ClassNotFoundException, InstantiationException, IllegalAccessException, Exception {
        String reqname = Message.getRequestName(soapMsg);

        Message msg = null;
        msg = (Message) Class.forName("org.openacs.message." + reqname).newInstance();
        msg = msg.parse(soapMsg);
        return msg;
    }

    private Message parse(SOAPMessage soapMsg) throws SOAPException, Exception {
        SOAPEnvelope env = soapMsg.getSOAPPart().getEnvelope();
        System.out.println("URI " + env.getNamespaceURI(""));

        Iterator<String> pfxs = (Iterator<String>) env.getNamespacePrefixes();
        while (pfxs.hasNext()) {
            String pfx = pfxs.next();
            String uri = env.getNamespaceURI(pfx);
            if (uri.startsWith("urn:dslforum-org:cwmp-")) {
                URN_CWMP = uri;
                System.out.println("cwmp NS =" + uri);
            }
        }
        SOAPFactory spf = SOAPFactory.newInstance();
        SOAPBodyElement soaprequest = getRequest(soapMsg);
        SOAPHeader hdr = soapMsg.getSOAPHeader();
        id = "device_did_not_send_id"; // or make it null?...
        noMore = false;
        if (hdr != null) {
            try {
                id = getHeaderElement(spf, hdr, "ID");
            } catch (NoSuchElementException e) {
            }
            try {
                noMore = getHeaderElement(spf, hdr, "NoMoreRequests").equals("1");
            } catch (NoSuchElementException e) {
            }
        }
        name = getRequestName(soapMsg);
        if (soaprequest != null) {
            try {
                parseBody(soaprequest, spf);
            } catch (Exception e) {
                SOAPElement se = getRequestChildElement(spf, soaprequest, "FaultCode");
                String FaultCode = (se != null) ? se.getValue() : "0";
                SOAPElement se2 = getRequestChildElement(spf, soaprequest, "FaultString");
                String FaultString = (se2 != null) ? se2.getValue() : "0";

                if (se != null || se2 != null) {
                    return new Fault(FaultCode, FaultString, id);
                }
                throw e;
            }
        }
        return this;
    }

    public void writeTo(OutputStream out) {
        try {
            MessageFactory mf = MessageFactory.newInstance();
            SOAPFactory spf = SOAPFactory.newInstance();

            String s = "<SOAP-ENV:Envelope";
            s += " xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"";
            s += " xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"";
            s += " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"";
            s += " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"";
            s += " xmlns:cwmp=\"" + URN_CWMP + "\"><SOAP-ENV:Header></SOAP-ENV:Header><SOAP-ENV:Body></SOAP-ENV:Body></SOAP-ENV:Envelope>";
            ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes());
            SOAPMessage msg = mf.createMessage(null, in);
            /*
            SOAPMessage msg = mf.createMessage();
            SOAPEnvelope env = msg.getSOAPPart().getEnvelope ();
            
            env.addNamespaceDeclaration("SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/");
            env.addNamespaceDeclaration("SOAP-ENC","http://schemas.xmlsoap.org/soap/encoding/");
            env.addNamespaceDeclaration("xsd","http://www.w3.org/2001/XMLSchema");
            env.addNamespaceDeclaration("xsi","http://www.w3.org/2001/XMLSchema-instance");
            env.addNamespaceDeclaration(CWMP,URN_CWMP);
             */

            //env.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

            SOAPHeaderElement elmntId = msg.getSOAPHeader().addHeaderElement(spf.createName("ID", CWMP, URN_CWMP));
            elmntId.setValue(getId());
            elmntId.setAttribute("SOAP-ENV:mustUnderstand", "1");
            msg.getSOAPHeader().addHeaderElement(spf.createName("NoMoreRequests", CWMP, URN_CWMP)).setValue((noMore) ? "1" : "0");

            SOAPBodyElement bd = msg.getSOAPBody().addBodyElement(spf.createName(name, CWMP, URN_CWMP));
            if (name == null || name.equals("")) {
                name = this.getClass().getSimpleName();
            }
            createBody(bd, spf);
            //msg.setProperty(SOAPMessage.WRITE_XML_DECLARATION, true);

            msg.writeTo(out);
        } catch (SOAPException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /*  protected SOAPElement getRequestChildElement(SOAPFactory f, SOAPElement req, String name) throws SOAPException {
    Name n = f.createName(name);
    
    Iterator ii = req.getChildElements();
    while (ii.hasNext()) {
    Object o = ii.next();
    try {
    Node nn = (Node)o;
    if (nn.getNodeName().equals("DeviceId")) {
    try { System.out.print("Name: '"+nn.getNodeName()+"'");} catch (Exception e) { System.out.println ("Exception: "+e.getMessage()+" "+e.getClass().getName());            }
    try { System.out.print("pfx: '"+nn.getPrefix()+"'");} catch (Exception e) { System.out.println ("Exception: "+e.getMessage()+" "+e.getClass().getName());            }
    try { System.out.print("baseURI: '"+nn.getBaseURI()+"'");} catch (Exception e) { System.out.println ("Exception: "+e.getMessage()+" "+e.getClass().getName());            }
    try { System.out.print("localName: '"+nn.getLocalName()+"'");} catch (Exception e) { System.out.println ("Exception: "+e.getMessage()+" "+e.getClass().getName());            }
    try { System.out.print("nameSpaceURI: '"+nn.getNamespaceURI()+"'");} catch (Exception e) { System.out.println ("Exception: "+e.getMessage()+" "+e.getClass().getName());            }
    }
    } catch (Exception e) {
    System.out.println ("Exception: "+e.getMessage()+" "+e.getClass().getName());
    }
    //            System.out.println ("getRequestChildElement: "+ o.getClass().getName());
    }
    
    Iterator i = req.getChildElements (n);
    
    while (i.hasNext()) {
    Object o = i.next();
    try {
    Node nn = (Node)o;
    System.out.print("Name: '"+nn.getNodeName()+"' value='"+nn.getNodeValue()+"'");
    } catch (Exception e) {
    System.out.println ("Exception: "+e.getMessage());
    }
    System.out.println ("getRequestChildElement: "+ o.getClass().getName());
    }
    i = req.getChildElements (n);
    
    boolean b = i.hasNext();
    Object o = i.next ();
    String c = o.getClass().getName();
    return (SOAPElement)o;
    //        return (SOAPElement)req.getChildElements(f.createName(name)).next();
    }
     */

    protected SOAPElement getRequestChildElement(SOAPFactory f, SOAPElement req, String name) throws SOAPException {
        Iterator i = req.getChildElements();
        while (i.hasNext()) {
            Object o = i.next();
            try {
                Node nn = (Node) o;
//                if (nn.getNodeName().equals(name)) {
                String n = nn.getLocalName();
                if (n != null && n.equals(name)) {
                    return (SOAPElement) o;
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage() + " " + e.getClass().getName());
            }
        }
        return null;
    }

    protected SOAPElement getRequestChildElement2(SOAPFactory f, SOAPElement req, String name) throws SOAPException {
        return (SOAPElement) req.getChildElements(f.createName(name, CWMP, URN_CWMP)).next();
    }

    protected String getRequestElement(SOAPFactory f, SOAPElement req, String name) throws SOAPException {
        return getRequestChildElement(f, req, name).getValue();
    }

    protected String getRequestElement(SOAPFactory f, SOAPElement req, String name, String def) throws SOAPException {
        String v = getRequestChildElement(f, req, name).getValue();
        return (v != null) ? v : def;
    }

    protected SOAPElement getRequestChildElement(SOAPElement req, Name name) throws SOAPException {
        return (SOAPElement) req.getChildElements(name).next();
    }

    protected String getRequestElement(SOAPElement req, Name name) throws SOAPException {
        return getRequestChildElement(req, name).getValue();
    }

    protected String getHeaderElement(SOAPFactory f, SOAPHeader hdr, String name) throws SOAPException {
        return ((SOAPHeaderElement) hdr.getChildElements(f.createName(name, CWMP, URN_CWMP)).next()).getValue();
    }

    protected Hashtable<String, String> parseParamList(SOAPElement body, SOAPFactory spf) throws SOAPException {
        return parseParamList(body, spf, "ParameterValueStruct", "Value");
    }

    protected Hashtable<String, String> parseParamList(SOAPElement body, SOAPFactory spf, String sn, String vn) throws SOAPException {
        //Hashtable pl = new Hashtable();
        Iterator pi = getRequestChildElement(spf, body, "ParameterList").getChildElements(spf.createName(sn));
        Name nameKey = spf.createName("Name");
        Name nameValue = spf.createName(vn);
        Hashtable<String, String> pl = new Hashtable<String, String>();
        while (pi.hasNext()) {
            SOAPElement param = (SOAPElement) pi.next();
            String key = getRequestElement(param, nameKey);
            String value = getRequestElement(param, nameValue);
            if (value == null) {
                value = "";
            }
            pl.put(key, value);
        }
        return pl;
    }

    protected int getArrayCount(SOAPFactory spf, SOAPElement e) throws SOAPException {
        return getArrayCount(spf, e, null);
    }

    protected int getArrayCount(SOAPFactory spf, SOAPElement e, ArrayType type) throws SOAPException {
        Name nameArray = spf.createName("arrayType", "soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
        String attr = e.getAttributeValue(nameArray);
        if (attr == null) {
            return 0;
        }
        attr = attr.replaceAll(" ", "");
        int i = attr.indexOf('[');
        String c = attr.substring(i + 1, attr.length() - 1);
        if (type != null) {
            type.setType(attr.substring(0, i));
        }
        return Integer.parseInt(c);
    }

    public boolean isFault() {
        return name.equals("Fault");
    }

    protected String b2s(boolean b) {
        return (b) ? "1" : "0";
    }
    protected String name;

    public String getName() {
        return name;
    }
    protected String id;

    public String getId() {
        if (id == null) {
            id = "ID:intrnl.unset.id." + ((name != null) ? name : "") + (Calendar.getInstance().getTimeInMillis() + 3600 * 1000) + "." + hashCode();
        } /*else {
        if (!id.startsWith("ID:")) {
        id = "ID:"+id;
        }
        }*/
        return id;
    }

    protected void println(StringBuilder b, String n, String v) {
        b.append(n);
        b.append(": ");
        b.append(v);
        b.append("\n");
    }

    protected void println(StringBuilder b, String n, String n2, String v) {
        b.append(n);
        println(b, n2, v);
    }
    public boolean noMore;
    protected String URN_CWMP = "urn:dslforum-org:cwmp-1-0";
    protected static final String CWMP = "cwmp";
    protected static final String PARAMETER_KEY = "ParameterKey";
    protected static final String COMMAND_KEY = "CommandKey";
    protected static final String XSI_TYPE = "xsi:type";
    protected static final String XSD_STRING = "xsd:string";
    protected static final String XSD_UNSIGNEDINT = "xsd:unsignedInt";
    protected static final String XSD_INT = "xsd:int";
    protected static final String XSD_BOOLEAN = "xsd:boolean";
    protected static final String XSD_DATETIME = "xsd:dateTime";
    protected static final String XSD_BASE64 = "xsd:base64";
    protected static final String SOAP_ARRAY_TYPE = "SOAP-ENC:arrayType";
    public static final String FAULT_CODE = "FaultCode";
    public static final String FAULT_STRING = "FaultString";
    static public final String TYPE_OBJECT = "object";
    static public final String TYPE_STRING = "string";
    static public final String TYPE_BOOLEAN = "boolean";
    static public final String TYPE_DATETIME = "dateTime";
    static public final String TYPE_UNSIGNEDINT = "unsignedInt";
    static public final String TYPE_INT = "int";
    static public final String TYPE_BASE64 = "base64";

    String getXmlType(String type) {
        if (type.equals(TYPE_BASE64)) {
            return Message.XSD_BASE64;
        } else if (type.equals(TYPE_BOOLEAN)) {
            return Message.XSD_BOOLEAN;
        } else if (type.equals(TYPE_DATETIME)) {
            return Message.XSD_DATETIME;
        } else if (type.equals(TYPE_INT)) {
            return Message.XSD_INT;
        } else if (type.equals(TYPE_OBJECT)) {
            return "";
        } else if (type.equals(TYPE_STRING)) {
            return Message.XSD_STRING;
        } else if (type.equals(TYPE_UNSIGNEDINT)) {
            return Message.XSD_UNSIGNEDINT;
        }
        return type;
    }
}
