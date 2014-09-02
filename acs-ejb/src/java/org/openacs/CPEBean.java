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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;
import javax.ejb.*;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.openacs.message.*;
import org.openacs.utils.Ejb;
import org.openacs.utils.Jms;

public class CPEBean implements SessionBean, CPELocalBusiness/*, ExceptionListener*/ {

    private SessionContext context;
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Add code to acquire and use other enterprise resources (DataSource, JMS, enterprise bean, Web services)
    // TODO Add business methods or web service operations

    /**
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext aContext) {
        context = aContext;
    }

    /**
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    public void ejbActivate() {
    }

    /**
     * @see javax.ejb.SessionBean#ejbPassivate()
     */
    public void ejbPassivate() {
    }

    /**
     * @see javax.ejb.SessionBean#ejbRemove()
     */
    public void ejbRemove() {
    }
    // </editor-fold>
    private long timeoutReceive = 30;
    private Jms jms;

    public void ejbCreate() throws NamingException, JMSException {
        jms = new Jms();
    }

    public void RequestCPEConnection(HostsLocal host) {
        requestCpeConnection(host.getUrl());
    }

    private void requestCpeConnection(String cpeurl) {

        try {
            URL url = new URL(cpeurl);
            URLConnection httpconn = url.openConnection();
            httpconn.setReadTimeout(5000);
            httpconn.getContent();

        } catch (MalformedURLException ex) {
            //ex.printStackTrace();
            throw new RuntimeException(cpeurl + " is malformed.");
        } catch (UnknownServiceException e) {
            // ignore exceptions caused by missing content-type header.
        } catch (IOException ex) {
            //ex.printStackTrace();
            throw new RuntimeException(cpeurl + " problem." + ex.getMessage() + " " + ex.getClass().getName());
        }
    }

    private HostsLocal findDevice(String oui, String hclass, String sn) {
        try {
            HardwareModelLocal hw = Ejb.lookupHardwareModelBean().findByOuiAndClass(oui, hclass);
            HostsLocalHome lhHosts = Ejb.lookupHostsBean();
//            return lhHosts.findByPrimaryKey(new HostsPK(oui, sn));
            return lhHosts.findByHwidAndSn((Integer) hw.getId(), sn);
        } catch (FinderException ex) {
            throw new RuntimeException("CPE not found in DB.");
        }
    }

    public Message WaitJmsReply(String filter, long timeoutReceive) throws JMSException {
        Message msg = (Message) jms.Receive(filter, timeoutReceive);
        /*        if (msg != null && msg.isFault())
        throw new RuntimeException("FAULT: " + ((Fault) msg).getFaultStringCwmp ());*/
        System.out.println("RCV1:  req=" + ((msg != null) ? msg.name : null));
        return msg;
    }

    private Message Call_(HostsLocal host, Message call) {
        return Call(host, call, true, 20);
    }

    public Message Call(HostsLocal host, Message call, long timeout) {
        return Call(host, call, false, timeout);
    }

    private Message Call(HostsLocal host, Message call, boolean requestConnection, long timeout) {
        try {
            //HardwareModelLocal hw = lookupHardwareModelBean().findByPrimaryKey(host.getHwid());
//            jms.sendCallMessage(call, call.id, hw.getOui(), host.getSerialno());
            jms.sendCallMessage(call, call.getId(), host);

            if (requestConnection) {
                RequestCPEConnection(host);
            }
            if (timeout <= 0 || timeout > 300) {
                timeout = timeoutReceive;
            }
            String flt = "JMSCorrelationID='" + call.getId() + "'";
            if (call.getId().startsWith("ID:")) {
                flt += " OR JMSCorrelationID='" + call.getId().substring(3) + "'";
            }
            return WaitJmsReply(flt, timeout * 1000);
        } catch (JMSException e) {
            throw new RuntimeException("JMSException: " + e.getMessage());
        }
    }

    public Message FactoryReset(HostsLocal host) {
        return Call_(host, new FactoryReset());
    }

    public GetRPCMethodsResponse GetRPCMethods(HostsLocal host) {
        return (GetRPCMethodsResponse) Call_(host, new GetRPCMethods());
    }

    public GetParameterNamesResponse GetParameterNames(HostsLocal host, String path, boolean next) {
        return (GetParameterNamesResponse) Call_(host, new GetParameterNames(path, next));
    }

    public GetParameterValuesResponse GetParameterValues(HostsLocal host, String[] names) {
        return (GetParameterValuesResponse) Call_(host, new GetParameterValues(names));
    }

    public SetParameterValuesResponse SetParameterValues(HostsLocal host, SetParameterValues values) {
        return (SetParameterValuesResponse) Call_(host, values);
    }

    private HardwareModelLocalHome lookupHardwareModelBean() {
        try {
            Context c = new InitialContext();
            HardwareModelLocalHome rv = (HardwareModelLocalHome) c.lookup("java:comp/env/HardwareModelBean");
            return rv;
        } catch (NamingException ne) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private HostsLocalHome lookupHostsBean() {
        try {
            Context c = new InitialContext();
            HostsLocalHome rv = (HostsLocalHome) c.lookup("java:comp/env/HostsBean");
            return rv;
        } catch (NamingException ne) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
