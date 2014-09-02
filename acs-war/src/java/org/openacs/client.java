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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.openacs.message.GetParameterValuesResponse;
import org.openacs.message.GetRPCMethodsResponse;
import org.openacs.utils.Ejb;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import org.openacs.message.GetParameterNamesResponse;

/**
 *
 * @author Administrator
 * @version
 */
public class client extends HttpServlet {

    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        /*
        Class c = org.ajax4jsf.component.AjaxComponent.class;
        Method [] ms = c.getMethods();
        for (Method m : ms) {
        out.println(m.getName());
        }
        ClassLoader cl = c.getClassLoader();
        out.println (cl);
        java.net.URLClassLoader clp1 = (java.net.URLClassLoader)cl.getParent();
        for (URL u : clp1.getURLs()) {
        out.println (u);
        }
        out.println (cl.getParent());
        if (true) return;
         */
//        org.ajax4jsf.component.AjaxComponent c = new org.ajax4jsf.component.AjaxComponent ();
//        org.ajax4jsf.component.AjaxComponent c = (org.ajax4jsf.component.AjaxComponent)Class.forName("org.ajax4jsf.component.AjaxComponent");

        if (false) {
            HttpAuthentication.Authenticate("testuser", "testpass", HttpAuthentication.AUTHTYPE_MD5, request, response);
        }
        if (true) {
            System.out.println("src: addr=" + request.getRemoteAddr() + " port=" + request.getRemotePort());
            String auth = request.getHeader("Authorization");
            if (auth == null) {
                System.out.println("CLIENT: PorcessRequest " + response.SC_UNAUTHORIZED + " " + request.getHeader("Authorization"));
//                response.setHeader("WWW-Authenticate", "Basic realm=\"OpenACS\"");
                byte[] nonce = new byte[16];
                Random r = new Random();
                r.nextBytes(nonce);
                // dcd98b7102dd2f0e8b11d0f600bfb0c093
                response.setHeader("WWW-Authenticate", "Digest realm=\"OpenACS\",qop=\"auth,auth-int\",nonce=\"" + cvtHex(nonce) + "\"");
//                response.setHeader("WWW-Authenticate", "Digest realm=\"testrealm@host.com\",qop=\"auth,auth-int\",nonce=\"dcd98b7102dd2f0e8b11d0f600bfb0c093\",opaque=\"5ccc069c403ebaf9f0171e9517f40e41\"");

                response.setStatus(response.SC_UNAUTHORIZED);
            } else {
                if (auth.startsWith("Basic ")) {
                    String up = auth.substring(6);
                    String ds = null;
                    try {
                        InputStream i = javax.mail.internet.MimeUtility.decode(new ByteArrayInputStream(up.getBytes()), "base64");
                        byte[] d = new byte[i.available()];
                        i.read(d);
                        ds = new String(d);
                    } catch (MessagingException ex) {
                        Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (up.endsWith("==")) {
                        ds = ds.substring(0, ds.length() - 2);
                    } else if (up.endsWith("=")) {
                        ds = ds.substring(0, ds.length() - 1);
                    }

                    String[] upa = ds.split(":");
                    System.out.println("CLIENT: up=" + up + " d='" + ds + "' user=" + upa[0] + " pass=" + upa[1]);

                } else if (auth.startsWith("Digest ")) {
                    if (auth.indexOf("nc=00000001") != -1) {
                        byte[] nonce = new byte[16];
                        Random r = new Random();
                        r.nextBytes(nonce);
                        System.out.println("Saying it is stale: " + auth);
                        response.setHeader("WWW-Authenticate", "Digest realm=\"OpenACS\",qop=\"auth,auth-int\",stale=true,nonce=\"" + cvtHex(nonce) + "\"");

                        response.setStatus(response.SC_UNAUTHORIZED);
                        return;
                    }
                    //String [] a = auth.substring(6).split(",");
                    ByteArrayInputStream bi = new ByteArrayInputStream(auth.substring(6).replace(',', '\n').replaceAll("\"", "").getBytes());
                    Properties p = new Properties();
                    p.load(bi);
                    p.setProperty("method", request.getMethod());
                    /*
                    
                    p.put("method", "POST");
                    p.put("qop", "auth");
                    p.put("uri", "/openacs/acs");
                    p.put("algorithm", "MD5");
                    p.put("username","mael49");
                    p.put("nc","00000001");
                    p.put("realm","OpenACS");
                    p.put("nonce","ca44f62837a4df15795cc629b2dae130");
                    InputStream i;
                    try {
                    i = javax.mail.internet.MimeUtility.decode(new ByteArrayInputStream("MTIyMzM2AA==".getBytes()), "base64");
                    byte [] d = new byte [i.available()];
                    i.read(d);
                    p.put("cnonce",new String (d));
                    } catch (MessagingException ex) {
                    Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    p.put ("response", "083c04b1ea861a7ee08b89406fcf5dc3");
                     */

                    for (Entry<Object, Object> e : p.entrySet()) {
                        System.out.println("Entry " + e.getKey() + " -> " + e.getValue());

                    }
                    MessageDigest digest = null;
                    try {
                        digest = MessageDigest.getInstance("MD5");
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    postDigest(digest, p);
                    /*
                    byte d[] = digest.digest();
                    for (byte b : d) {
                    System.out.print (String.format("%02x ", b));
                    }
                    System.out.println ();
                    System.out.println ("Digest "+cvtHex(d));
                     */
                    String udigest = (String) p.getProperty("response");
                    String dd = cvtHex(digest.digest());
                    System.out.println("respone: got='" + udigest + "' expected: '" + dd + "'");
                }

            }
            out.println("Hello");
            out.close();
            return;
        }


        int i;
        String oui = "00147F";
        String sn = "CP0713JTP7W";
        CPELocal cpe = Ejb.lookupCPEBean();
        out.println("Lookup bean .....\n" + cpe);
        /*        System.out.println ("Calling method ...");
        GetParameterNamesResponse r = cpe.GetParameterNames(oui, sn, ".", false);
        Object [] names = r.names.keySet().toArray();
        Arrays.sort (names);
        String [] n = new String [names.length];
        for (i = 0; i < names.length; i++) {
        out.println (names[i]+"\t"+r.names.get (names[i]));
        n[i] = (String)names [i];
        }
         */
        /*
        SetParameterValues sv = new SetParameterValues();
        sv.key = "setvalueskey";
        sv.AddValue("InternetGatewayDevice.IPPingDiagnostics.Host","192.168.0.1");
        sv.AddValue("InternetGatewayDevice.IPPingDiagnostics.NumberOfRepetitions", "2");
        sv.AddValue("InternetGatewayDevice.IPPingDiagnostics.DiagnosticsState", "Requested");
        SetParameterValuesResponse vr = cpe.SetParameterValues(oui, sn, sv);
        out.println ("Set values status: "+vr.Status);
         */
        GetParameterNamesResponse gpnr = null; //cpe.GetParameterNames(oui, sn, "InternetGatewayDevice.WANDevice.", true);
        Enumeration pnks = gpnr.names.keys();
        while (pnks.hasMoreElements()) {
            String k = (String) pnks.nextElement();
            out.println(k + " = " + gpnr.names.get(k));
        }

        if (true) {
            String[] n = new String[6];
            n[0] = "InternetGatewayDevice.DeviceSummary";
            n[1] = "InternetGatewayDevice.DeviceInfo.Manufacturer";
            n[2] = "InternetGatewayDevice.DeviceInfo.ManufacturerOUI";
            n[3] = "InternetGatewayDevice.IPPingDiagnostics.DiagnosticsState";
            n[4] = "InternetGatewayDevice.IPPingDiagnostics.SuccessCount";
            n[5] = "InternetGatewayDevice.DeviceInfo.ProductClass";

            GetParameterValuesResponse values = null; //cpe.GetParameterValues(oui, sn, n);
            if (values == null) {
                out.println("No response .....");
            }
            Enumeration ve = values.values.keys();
            out.println("-----------------------------------------------------------------");
            while (ve.hasMoreElements()) {
                String k = (String) ve.nextElement();
                String v = (String) values.values.get(k);
                out.println(k + "=" + v);
            }

            /*
            Enumeration e = r.names.keys();
            while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            out.println (name+"\t");
            }
             */
            out.println("-----------------------------------------------------------------");
            GetRPCMethodsResponse methods = null; //cpe.GetRPCMethods(oui, sn);
            if (methods == null) {
                out.println("No response .....");
            }
            for (i = 0; i < methods.methods.length; i++) {
                out.println(methods.methods[i]);
            }

        }
        out.close();
    }
    private String username;
    private String password;
    private boolean passwordIsA1Hash;

    public void postDigest(MessageDigest digest, Properties p) {
        username = "Mufasa";
        password = "Circle Of Life";
        username = "testuser";
        password = "testpass";
        passwordIsA1Hash = false;

        String qop = (String) p.getProperty("qop");
        String realm = (String) p.getProperty("realm");
        String algorithm = (String) p.getProperty("algorithm");
        String nonce = (String) p.getProperty("nonce");
        String cnonce = (String) p.getProperty("cnonce");
        String method = (String) p.getProperty("method");
        String nc = (String) p.getProperty("nc");
        String digestURI = (String) p.getProperty("uri");
        /*
        System.out.println ("qop "+qop);
        System.out.println ("realm "+realm);
        System.out.println ("nonce "+nonce);
        System.out.println ("cnonce "+cnonce);
        System.out.println ("method "+method);
        System.out.println ("nc "+nc);
        System.out.println ("uri "+digestURI);
         */
        if (algorithm == null) {
            algorithm = digest.getAlgorithm();
        }
        digest.reset();

        String hA1 = null;
        // 3.2.2.2 A1
        if (algorithm == null || algorithm.equals("MD5")) {
            if (passwordIsA1Hash) {
                hA1 = password;
            } else {
                String A1 = username + ":" + realm + ":" + password;
                hA1 = H(A1, digest);
            }
        } else if (algorithm.equals("MD5-sess")) {
            if (passwordIsA1Hash) {
                hA1 = password + ":" + nonce + ":" + cnonce;
            } else {
                String A1 = username + ":" + realm + ":" + password;
                hA1 = H(A1, digest) + ":" + nonce + ":" + cnonce;
            }
        } else {
            throw new IllegalArgumentException("Unsupported algorigthm: " + algorithm);
        }

        // 3.2.2.3 A2. First check to see if the A2 hash has been precomputed
        String hA2 = null;//(String) info.getInfo(A2HASH);
        if (hA2 == null) {
            // No, compute it based on qop
            String A2 = null;
            if (qop == null || qop.equals("auth")) {
                A2 = method + ":" + digestURI;
            } else {
                throw new IllegalArgumentException("Unsupported qop=" + qop);
            }
            hA2 = H(A2, digest);
        }

        // 3.2.2.1 Request-Digest
        if (qop == null) {
            String extra = nonce + ":" + hA2;
            KD(hA1, extra, digest);
        } else if (qop.equals("auth")) {
            String extra = nonce
                    + ":" + nc
                    + ":" + cnonce
                    + ":" + qop
                    + ":" + hA2;
            KD(hA1, extra, digest);
        }
    }

    static private String H(String data, MessageDigest digest) {
        digest.reset();
        byte[] x = digest.digest(data.getBytes());
        return cvtHex(x);
    }
    private static char[] MD5_HEX = "0123456789abcdef".toCharArray();

    static String cvtHex(byte[] data) {
        char[] hash = new char[32];
        for (int i = 0; i < 16; i++) {
            int j = (data[i] >> 4) & 0xf;
            hash[i * 2] = MD5_HEX[j];
            j = data[i] & 0xf;
            hash[i * 2 + 1] = MD5_HEX[j];
        }
        return new String(hash);
    }

    static private void KD(String secret, String data, MessageDigest digest) {
        String x = secret + ":" + data;
        digest.reset();
        digest.update(x.getBytes());
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
