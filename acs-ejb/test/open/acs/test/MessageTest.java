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
package open.acs.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import junit.framework.TestCase;
import org.openacs.Message;

public class MessageTest extends TestCase {

    public MessageTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    public void testGetRPCResponse() throws SOAPException, IOException {
        String msg = "<SOAP-ENV:Envelope "
                + "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + " xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\""
                + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\">"
                + "<SOAP-ENV:Header><cwmp:ID SOAP-ENV:mustUnderstand=\"1\">intrnl.unset.id.1218901372619</cwmp:ID></SOAP-ENV:Header>"
                + "<SOAP-ENV:Body><cwmp:GetRPCMethodsResponse><MethodList SOAP-ENC:arrayType=\"xsd:string[14  ]\">"
                + "<string>GetRPCMethods</string>"
                + "<string>GetParamterNames</string>"
                + "<string>GetParameterValues</string>"
                + "<string>SetParameterValues</string>"
                + "<string>GetParameterAttributes</string>"
                + "<string>SetParameterAttributes</string>"
                + "<string>AddObject</string>"
                + "<string>DeleteObject</string>"
                + "<string>Download</string>"
                + "<string>Upload</string>"
                + "<string>Reboot</string>"
                + "<string>FactoryReset</string>"
                + "<string>GetQueuedTransfers</string>"
                + "<string>ScheduleInform</string></MethodList></cwmp:GetRPCMethodsResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        messageTest(msg);
    }

    public void testMalformedFault() throws SOAPException, IOException {
        String msg = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"><SOAP-ENV:Header>  <cwmp:ID SOAP-ENV:mustUnderstand=\"1\">ID:intrnl.unset.id.GetParameterNames1268035864598.9384779</cwmp:ID></SOAP-ENV:Header> <SOAP-ENV:Body>  <cwmp:GetParameterNamesResponse>  <FaultCode>9005</FaultCode><FaultString>Invalid Parameter Name</FaultString></cwmp:GetParameterNamesResponse> </SOAP-ENV:Body></SOAP-ENV:Envelope>";
        messageTest(msg);
    }

    private void messageTest(String m) throws SOAPException, IOException {
        MessageFactory mf;
        mf = MessageFactory.newInstance();

        ByteArrayInputStream f = new ByteArrayInputStream(m.getBytes());

        SOAPMessage soapMsg = mf.createMessage(null, f);

        Message msg = null;
        try {
            msg = Message.Parse(soapMsg);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

    }
}
