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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.logging.*;
import java.util.NoSuchElementException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.soap.*;


import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import org.openacs.message.*;
import org.openacs.utils.*;

/**
 *
 * @author Administrator
 * @version
 */
public class ACSServlet extends HttpServlet {

    protected static final String ATTR_LASTINFORM = "lastInform";
    private static final String ATTR_CONFIGURATOR = "cfgrun";

    private class MyAuthenticator extends Authenticator {

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            String ui = this.getRequestingURL().getUserInfo();
            System.out.println("MyAuthenticator: ui=" + ui);
            if (ui == null || ui.equals("")) {
                return super.getPasswordAuthentication();
            }
            String up[] = ui.split(":");
            char[] pc = new char[up[1].length()];
            up[1].getChars(0, up[1].length(), pc, 0);
            PasswordAuthentication pa = new PasswordAuthentication(up[0], pc);
            return pa;
        }
    }

    private class xmlFilterNS extends InputStream {
        // Dumb class to filter out declaration of default xmlns

        private String pat = "xmlns=\"urn:dslforum-org:cwmp-1-0\"";
        private String pat2 = "xmlns=\"urn:dslforum-org:cwmp-1-1\"";
        private int length = 0;
        private int pos = 0;
        private boolean f = false;
        private byte buff[] = new byte[1024];
        private InputStream is;

        @Override
        public int read() throws IOException {
            if (!f) {
                length = is.read(buff);
                if (length < buff.length) {
                    byte[] b2 = new byte[length];
                    System.arraycopy(buff, 0, b2, 0, length);
                    buff = b2;
                }

                String b = new String(buff);
                b = b.replace(pat, "");
                b = b.replace(pat2, "");
                buff = b.getBytes();
                length = buff.length;
                f = true;
            }

            if (pos < length) {
                return buff[pos++];
            }
            return is.read();
        }

        public xmlFilterNS(InputStream is) {
            this.is = is;
        }
    }

    private class charsetConverterInputStream extends InputStream {

        private InputStream in;
        private PipedInputStream pipein;
        private OutputStream pipeout;
        private Reader r;
        private Writer w;

        public charsetConverterInputStream(String csFrom, String csTo, InputStream in) throws UnsupportedEncodingException, IOException {
            this.in = in;
            r = new InputStreamReader(in, csFrom);
            pipein = new PipedInputStream();
            pipeout = new PipedOutputStream(pipein);
            w = new OutputStreamWriter(pipeout, csTo);
        }

        @Override
        public int read() throws IOException {
            if (pipein.available() > 0) {
                return pipein.read();
            }
            int c = r.read();
            if (c == -1) {
                return -1;
            }
            w.write(c);
            w.flush();
            return pipein.read();
        }
    }

    private class xmlFilterInputStream extends InputStream {

        /** Creates a new instance of xmlFilterInputStream */
        public xmlFilterInputStream(InputStream is, int l) {
            //      System.out.println("Stream length is "+l);
            len = l;
            istream = is;
        }

        public int read() throws IOException {
            if (lastchar == '>' && lvl == 0) {
                //        System.err.println ("return EOF");
                return -1;
            }
            int l = lastchar;
            if (nextchar != -1) {
                lastchar = nextchar;
                nextchar = -1;
            } else {
                if (buff.length() > 0) {
                    //                  System.out.println("buff len="+buff.length());
                    lastchar = buff.charAt(0);
                    buff.deleteCharAt(0);
                    return lastchar;
                } else {
                    lastchar = istream.read();
                }
            }
            if (lastchar == '<') {
                intag = true;
            } else if (lastchar == '>') {
                intag = false;
            }

            if (!intag && lastchar == '&') {
                int amppos = buff.length();
                // fix up broken xml not encoding &
                buff.append((char) lastchar);
//                System.out.println("Appended buff len="+buff.length());
                for (int c = 0; c < 10; c++) {
                    int ch = istream.read();
                    if (ch == -1) {
                        break;
                    }
                    if (ch == '&') {
                        nextchar = ch;
                        break;
                    }
                    buff.append((char) ch);
//                System.out.println("Appended buff len="+buff.length());
                }
//                System.out.println ("xmlFilterInputStream: buff="+buff.substring(0, buff.length()));
                String s = buff.substring(amppos);
                if (!s.startsWith("&amp;") && !s.startsWith("&lt;") && !s.startsWith("&gt;") && !s.startsWith("&apos;") && !s.startsWith("&quot;") && !s.startsWith("&#")) {
                    buff.replace(amppos, amppos + 1, "&amp;");
                }
                return read();
            }

            if (l == '<') {
                intag = true;
                if (lastchar == '/') {
                    lvl--;
                } else {
                    lvl++;
                }
            }
            //           System.err.println ("return char="+(char)lastchar+" lvl="+lvl);
            //System.err.print ((char)lastchar);
            len--;
            return lastchar;
        }
        private InputStream istream;
        private int lvl;
        private int lastchar;
        private int len;
        private int nextchar;
        private boolean intag = false;
        private StringBuffer buff = new StringBuffer(16);

        public boolean next() throws IOException {
            while ((nextchar = istream.read()) != -1) {
                if (!Character.isWhitespace(nextchar)) {
                    break;
                }
            }
            //        System.out.println ("Next char is "+nextchar);
            lvl = 0;
            lastchar = 0;
            return (nextchar != -1);
        }
    }

    private boolean authenticate(HostsLocal host, HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*
        System.out.println ("host="+host +" request="+request+" response="+response);
        System.out.println ("user="+host.getUsername());
        System.out.println ("pass="+host.getPassword());
        System.out.println ("authtype="+host.getAuthtype());
         */
        return HttpAuthentication.Authenticate(host.getUsername(), host.getPassword(), host.getAuthtype(), request, response);
    }
    private Logger l = Logger.getLogger(getClass().getName());

    private void log(Inform inf, Level level, String msg) {
        StringBuilder s = new StringBuilder(128);
        if (inf != null) {
            s.append("oui=" + inf.getOui() + " sn=" + inf.sn + " ");
        }
        s.append(msg);
        l.log(level, s.toString());
    }

    private void log(Inform inf, Level level, String msg, Throwable ex) {
        StringBuilder s = new StringBuilder(128);
        if (inf != null) {
            s.append("oui=" + inf.getOui() + " sn=" + inf.sn + " ");
        }
        s.append(msg);
        l.log(level, s.toString(), ex);
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * 
     * 
     * 
     * @param request ACSServlet request
     * @param response ACSServlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext ctx = getServletContext();
//        boolean autoCreateCPE = Boolean.parseBoolean(ctx.getInitParameter("org.openacs.AutoCreateCPE"));
        boolean autoCreateCPE = Application.getAutoCreateCpe();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SOAPMessage soapMsg = null;
        xmlFilterInputStream f = new xmlFilterInputStream(request.getInputStream(), request.getContentLength());
        MessageFactory mf;
        //SOAPFactory spf;
        try {
            mf = MessageFactory.newInstance();
        //spf = SOAPFactory.newInstance();
        } catch (SOAPException e) {
            throw new ServletException();
        }
        String ct = request.getContentType();
        int csix = (ct != null) ? ct.indexOf("charset=") : -1;
        String csFrom = (csix == -1) ? "ISO-8859-1" : ct.substring(csix + 8).replaceAll("\"", "");

        //response.setContentType("text/xml;charset=UTF-8");
        //response.setContentType("text/xml");
        //System.out.println ("ContentType="+ct+" cs="+csFrom);

        response.setContentType(ct != null ? ct : "text/xml;charset=UTF-8");
        int countEnvelopes = 0;
        HttpSession session = request.getSession();
        String oui, sn;
        Inform lastInform = (Inform) session.getAttribute(ATTR_LASTINFORM);
        @SuppressWarnings("uncheked")
        ArrayList<TransferComplete> transfersComplete = (ArrayList<TransferComplete>) session.getAttribute("tcs");
        if (transfersComplete == null) {
            session.setAttribute("tcs", transfersComplete = new ArrayList<TransferComplete>());
        }
        int maxEnvelopes = (lastInform != null) ? lastInform.MaxEnvelopes : MIN_MAX_ENVELOPES;
        if (maxEnvelopes < MIN_MAX_ENVELOPES) {
            System.out.println("MaxEnvelopes are less then " + MIN_MAX_ENVELOPES + " (" + maxEnvelopes + "). Setting to " + MIN_MAX_ENVELOPES);
            maxEnvelopes = 1;
        }
        // <editor-fold defaultstate="collapsed" desc="Get/create JMS producer&consumer">
//        QueueSession queuesession = null;
//        javax.jms.Queue queue = null;
        // </editor-fold>
        // <editor-fold defaultstate="expanded" desc="Process CPE requests/responses">
        HardwareModelLocal hw = (HardwareModelLocal) session.getAttribute("hardware");
        HostsLocal lasthost = (HostsLocal) session.getAttribute("host");
        boolean cpeSentEmptyReq = true;
        while (f.next()) {
            cpeSentEmptyReq = false;
            try {
                //System.out.println("createMessage");
                /*
                MimeHeaders hdrs = null;
                if (ct != null) {
                hdrs = new MimeHeaders();
                hdrs.setHeader("Content-Type", ct);
                }
                 */
                MimeHeaders hdrs = new MimeHeaders();
                hdrs.setHeader("Content-Type", "text/xml; charset=UTF-8");
                InputStream in = (csFrom.equalsIgnoreCase("UTF-8")) ? new xmlFilterNS(f) : new charsetConverterInputStream(csFrom, "UTF-8", new xmlFilterNS(f));
                soapMsg = mf.createMessage(hdrs, in);


                Message msg = null;
                try {
                    msg = Message.Parse(soapMsg);
                } catch (Exception e) {
                    soapMsg.writeTo(out);
                    log(lastInform, Level.SEVERE, "Parsing failed:\n" + out.toString(), e);
                    response.setStatus(response.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
                String reqname = msg.getName();
                log(lastInform, Level.INFO, "Request is " + reqname);
                log(lastInform, Level.FINE, msg.toString());

                //if (msg.isFault()) { continue; }

                try {
                    // <editor-fold defaultstate="expanded" desc="Process Inform request">
                    if (reqname.equals("Inform")) {
                        lastInform = (Inform) msg;
                        session.setAttribute(ATTR_LASTINFORM, lastInform);

                        oui = lastInform.getOui();
                        try {
                            OuiMapLocal om = Ejb.lookupOuiMapBean().findByPrimaryKey(oui);
                            if (om.getMappedoui() == null) {
                                log(lastInform, Level.WARNING, "Oui "+oui+" is mapped to null");
                            } else {
                                oui = om.getMappedoui();
                                lastInform.setOui(oui);
                            }
                        } catch (FinderException e) {

                        }

                        if (lastInform.getHardwareVersion() == null) {
                            // create fault
                            log(lastInform, Level.SEVERE, "No hardware version in Inform");
                            return;
                        }
                        InformResponse resp = new InformResponse(lastInform.getId(), MY_MAX_ENVELOPES);
                        resp.writeTo(out);
                        countEnvelopes++;

                        maxEnvelopes = lastInform.MaxEnvelopes;
                        sn = lastInform.sn;
                        String url = lastInform.getURL();
                        //System.out.println("oui="+oui+", sn="+sn+", URL="+url);
                        log(lastInform, Level.INFO, "oui=" + oui + ", sn=" + sn + ", URL=" + url + ", hw=" + lastInform.getHardwareVersion() + ", sw=" + lastInform.getSoftwareVersion() + ", cfg=" + lastInform.getConfigVersion() + ", ProvisioningCode=" + lastInform.getProvisiongCode());

                        HardwareModelLocalHome hwhome = Ejb.lookupHardwareModelBean();
                        try {
                            hw = hwhome.findByOuiAndClassAndVersion(oui, lastInform.ProductClass, lastInform.getHardwareVersion());
                        } catch (FinderException e) {
                            try {
                                hw = hwhome.create(lastInform.ProductClass.equals("") ? lastInform.Manufacturer + "." + lastInform.getHardwareVersion() : lastInform.ProductClass, lastInform.Manufacturer, oui, lastInform.ProductClass, lastInform.getHardwareVersion());
                            } catch (CreateException ex) {
                                ex.printStackTrace();
                                throw new RuntimeException();
                            }
                        }

                        // <editor-fold defaultstate="expanded" desc="Updating hwid to contain version">
                        HardwareModelLocal hwold = null;
                        try {
                            hwold = hwhome.findByOuiAndClass(oui, lastInform.ProductClass);
                        } catch (FinderException ex) {
                        }
                        // </editor-fold>

                        session.setAttribute("hardware", hw);

                        HostsLocalHome hsthome = Ejb.lookupHostsBean();
                        //System.out.println("SERVLET: looked up HostsLocalHome");
                        HostsLocal host = null;
                        // <editor-fold defaultstate="expanded" desc="Updating hwid to contain version">
                        if (hwold != null) {
                            try {
                                host = hsthome.findByHwidAndSn((Integer) hwold.getId(), sn);
                                host.setHwid((Integer) hw.getId());
                            } catch (FinderException ex) {
                            }
                        }
                        // </editor-fold>

                        try {
//                            host = hsthome.findByPrimaryKey(new HostsPK(oui, sn));
                            host = hsthome.findByHwidAndSn((Integer) hw.getId(), sn);
                        //System.out.println("SERVLET: after findByPrimaryKey");
                        //System.out.println("Bean url="+host.getUrl());
//                            if (!authenticate(host, request, response)) return;
//                            host.setUrl(url);
                        } catch (FinderException e) {
                            if (autoCreateCPE) {
                                try {
                                    host = hsthome.create((Integer) hw.getId(), sn, url);
                                    host.setConfigname("Default");
                                } catch (CreateException ex) {
                                    ex.printStackTrace();
                                    throw new RuntimeException();
                                }
                            } else {
                                log(lastInform, Level.WARNING, "Autocreate is off and device is not known: oui=" + oui + ", sn=" + sn);
                                return;
                            }
                        }
                        if (!authenticate(host, request, response)) {
                            return;
                        }
                        host.setUrl(url);
                        String t = lastInform.getConreqUser();
                        if (t != null) {
                            host.setConrequser(t);
                        }
                        t = lastInform.getConreqPass();
                        if (t != null) {
                            host.setConreqpass(t);
                        }

                        host.setLastcontact(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
                        host.setCurrentsoftware(lastInform.getSoftwareVersion());
                        host.setHardware(lastInform.getHardwareVersion());
                        host.setCfgversion(lastInform.getConfigVersion());

                        Collection<SoftwareLocal> fws = hw.getFirmware();

                        /*
                        HardwareModelLocal hhh = host.getModel();
                        System.out.println ("hhh="+hhh + "model="+hhh.getDisplayName());
                         */
                        lasthost = host;
                        session.setAttribute("host", host);

                    //RunInformScript(host, lastInform);
                    //RunConfigurator(request, host, lastInform, null);
                    // </editor-fold>
                    } else if (reqname.equals("TransferComplete")) {
                        log(lastInform, Level.INFO, ((TransferComplete) msg).toString());
                        TransferComplete tc = (TransferComplete) msg;
                        TransferCompleteResponse tr = new TransferCompleteResponse(tc.getId());
                        tr.writeTo(out);
                        countEnvelopes++;

//                        countEnvelopes = OnTransferComplete((TransferComplete) msg, request, lastInform, countEnvelopes, out);
                        //RunConfigurator(request, lasthost, lastInform, (TransferComplete)msg);
                        transfersComplete.add(tc);

                    } else if (reqname.equals("GetRPCMethods")) {
                        GetRPCMethodsResponse responseGetRPCMethods = new GetRPCMethodsResponse((GetRPCMethods) msg);
                        responseGetRPCMethods.writeTo(out);
                        countEnvelopes++;
                    } else {
                        // <editor-fold defaultstate="expanded" desc="Process replies & send them to JMS listeners">
                        //System.out.println ("Process replies");
                        SendResponse(session, msg);
                    }
                // </editor-fold>
                } catch (NoSuchElementException e) {
                    //System.out.println("NoSuchElementException");
                    log(lastInform, Level.SEVERE, "While parsing", e);
                    return;
                } catch (IllegalArgumentException e) {
                    log(lastInform, Level.WARNING, "IllegalArgumentException", e);
                }
            } catch (SOAPException e) {
                log(lastInform, Level.SEVERE, "While parsing", e);
                break;
            } catch (IllegalArgumentException e) {
                log(lastInform, Level.WARNING, "IllegalArgumentException", e);
            }
        }
        if (cpeSentEmptyReq) {
            if (session.getAttribute(ATTR_CONFIGURATOR) == null) {
                session.setAttribute(ATTR_CONFIGURATOR, RunConfigurator(request, lasthost, lastInform, transfersComplete));
            }

        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Process requests from queue">
        {
            //jms.sendResponseMessage(new Inform(), null);

            if (lastInform == null) {
                System.out.println("No Inform received on this session.");
                return;
            }
            oui = lastInform.getOui();
            sn = lastInform.sn;
            log(lastInform, Level.FINEST, "Envelopes: " + countEnvelopes + " of " + maxEnvelopes);
            int idle = 0, keepalive = 0;

            //countEnvelopes = 0;

            while (countEnvelopes < maxEnvelopes) {
                try {
//                    ObjectMessage jm = (ObjectMessage)consumer.receive((countEnvelopes>0) ? 1: 5000); // wait if only we haven't got something to send'
                    int wait = (countEnvelopes > 0) ? 0 : 5000; // wait if only we haven't got something to send'
                    Message m = ReceiveRequest(session, hw.getId(), sn, wait);

                    System.out.println("Received: " + ((m == null) ? "null" : m.getName()));
                    if (m == null) {
                        if (idle++ >= keepalive) {
                            break;
                        } else {
                            // maybe send some easy call e.g. getrpcmethods
                            GetRPCMethods g = new GetRPCMethods("ignorereply");
                            g.writeTo(out);
                            countEnvelopes++;
                            continue;
                        }
                    }
                    idle = 0;
                    //System.out.println ("Msgid is "+m.id);
                    m.writeTo(out);
                    countEnvelopes++;
                } catch (JMSException e) {
                    log(lastInform, Level.SEVERE, "JMSException in receive loop" + e.getMessage());
                    throw new ServletException();
                }
            }
        }

        if (out.size() == 0) {
            response.setStatus(response.SC_NO_CONTENT);
        }
        response.setContentLength(out.size());
        String sout = out.toString();
        sout = sout.replace('\'', '"');
        //sout = sout.replace(' ', '\n');
        response.getOutputStream().print(sout);
        //out.writeTo(response.getOutputStream());
        System.out.println("End of processing");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * 
     * 
     * 
     * @param request ACSServlet request
     * @param response ACSServlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * 
     * 
     * 
     * @param request ACSServlet request
     * @param response ACSServlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the ACSServlet.
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>

    private Configurator RunConfigurator(HttpServletRequest request, HostsLocal host, Inform lastInform, ArrayList<TransferComplete> transferComplete) {
//        String fwpath = Util.getFirmwarePath(this);
        String fwpath = Application.getFirmwarePath();
        String localAddr = Application.getOverrideServerName();
        try {
            if (Application.IsNoNATNetwork(request.getRemoteAddr()) || localAddr == null || localAddr.equals("")) {
                localAddr = request.getLocalAddr();
            }
        } catch (UnknownHostException ex) {
        }
        String urlServer = request.getScheme() + "://" + localAddr + ":" + request.getLocalPort() + request.getContextPath();
        Configurator cfgr = new Configurator(lastInform, host.getId(), transferComplete, fwpath, urlServer, request.getSession().getId());
        cfgr.SetCallType(queueingType);
        cfgr.start();
        return cfgr;
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("PUT FILE");
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    public ACSServlet() {
    }

    @Override
    public void init() throws ServletException {
        //setupJMS();
        Authenticator.setDefault(new MyAuthenticator());
        InitQueueing();
    }

    @Override
    public void destroy() {
        FinishQueueing();
    }
    private static final int QUEUEING_TYPE_JMS = 1;
    private static final int QUEUEING_TYPE_OBJ = 2;
//    private int queueingType = QUEUEING_TYPE_JMS;
    private int queueingType = QUEUEING_TYPE_OBJ;

    private void InitQueueing() throws ServletException {
        switch (queueingType) {
            case QUEUEING_TYPE_JMS:
                jmsInitQueueing();
                break;
            case QUEUEING_TYPE_OBJ:
                break;
            default:
                throw new ServletException("Not implemented");
        }
    }

    private void jmsInitQueueing() throws ServletException {
        try {
            _jms = new Jms();
        } catch (Exception e) {
            log(null, Level.SEVERE, e.getMessage());
            throw new ServletException(e);
        }
    }

    private void FinishQueueing() {
        switch (queueingType) {
            case QUEUEING_TYPE_JMS:
                jmsFinishQueueing();
                break;
        }
    }

    private void jmsFinishQueueing() {
        _jms.closeJMS();
    }

    private void SendResponse(HttpSession session, Message msg) {
        switch (queueingType) {
            case QUEUEING_TYPE_JMS:
                jmsSendResponse(session, msg);
                break;
            case QUEUEING_TYPE_OBJ:
                objSendResponse(session, msg);
                break;
        }
    }
    private void objSendResponse(HttpSession session, Message msg) {
         Configurator c = (Configurator)session.getAttribute(ATTR_CONFIGURATOR);
        if (c == null) {
            System.out.println ("No configurator for this session in objSendResponse");
            return;
        }
         c.SendResponse(msg);
    }

    private void jmsSendResponse(HttpSession session, Message msg) {
        Inform lastInform = (Inform) session.getAttribute(ATTR_LASTINFORM);
        try {
            if (!msg.getId().startsWith("ignorereply")) {
                log(lastInform, Level.FINEST, "Send JMS reply for " + msg.getId());
                _jms.sendResponseMessage(msg, msg.getId());
            }
        } catch (JMSException e) {
            log(lastInform, Level.WARNING, "Exception while sending", e);
        }
    }

    private Message ReceiveRequest(HttpSession session, Object hwid, String sn, long w) throws JMSException {
        switch (queueingType) {
            case QUEUEING_TYPE_JMS:
                return jmsReceiveRequest(session, hwid, sn, w);
            case QUEUEING_TYPE_OBJ:
                return objReceiveRequest(session, hwid, sn, w);
        }
        return null;
    }

    private Message objReceiveRequest(HttpSession session, Object hwid, String sn, long w) throws JMSException {
        Configurator c = (Configurator)session.getAttribute(ATTR_CONFIGURATOR);
        if (c == null) {
            System.out.println ("No configurator for this session");
            return null;
        }
        return c.ReceiveRequest(w);
    }
    private Message jmsReceiveRequest(HttpSession session, Object hwid, String sn, long w) throws JMSException {
        MessageConsumer consumer = (MessageConsumer) session.getAttribute("consumer");
        if (consumer == null) {
//                consumer = queuesession.createConsumer(queue, "OUI='" + oui + "' AND SN='" + sn + "'");
            //String filter = "OUI='" + oui + "' AND SN='" + sn + "'";
            String filter = "HWID='" + /*hw.getId()*/ hwid + "' AND SN='" + sn + "'";

            consumer = _jms.createConsumer(filter);
            session.setAttribute("consumer", consumer);
        //log(lastInform, Level.FINEST, "Created consumer: " + filter);
        }
        ObjectMessage jm = (w == 0) ? (ObjectMessage) consumer.receiveNoWait() : (ObjectMessage) consumer.receive(w);
        if (jm != null) {
            return (Message) jm.getObject();
        }
        return null;
    }
    private Jms _jms;
    private final int MIN_MAX_ENVELOPES = 1; // Minimal value for MaxEnevelopes
    private final int MY_MAX_ENVELOPES = 1;
}
