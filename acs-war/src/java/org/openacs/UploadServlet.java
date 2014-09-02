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
import java.util.Enumeration;
import javax.mail.MessagingException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.BodyPart;

public class UploadServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Uploader request: path=" + request.getPathInfo() + " querystring=" + request.getQueryString() + " sess=" + request.getSession(false));

        int t = 0;
        String ct = request.getContentType();
        if (ct == null) {
            return;
        }

        if (ct.startsWith("multipart/form-data;")) {
            MultipartDataSource src = new MultipartDataSource(request);
            MimeMultipart mp;

            try {
                mp = new MimeMultipart(src);
                System.out.println("Got body count " + mp.getCount());

                BodyPart bp = mp.getBodyPart(0);
                FileOutputStream fout = new FileOutputStream("c:\\temp\\" + bp.getFileName());
                bp.getDataHandler().writeTo(fout);
                fout.close();
                System.out.println("Upload data read complete.");

                System.out.println("FileName = " + bp.getFileName());
                System.out.println("Disposition = " + bp.getDisposition());
                System.out.println("Description = " + bp.getDescription());
                System.out.println("Size = " + bp.getSize());
            } catch (MessagingException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Uknown content type: " + ct);
            /*
            ServletInputStream in = request.getInputStream();
            int                c;
            byte[]             b = new byte[10240];
            
            while ((c = in.read(b)) != -1) {
            
            // System.out.println ("Read "+c+" bytes");
            t += c;
            }
            System.out.println("UploadServlet data read complete " + t + " bytes");
             */
        }


//      response.setStatus(response.SC_NO_CONTENT);
    }
    /*
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    System.out.println("Uploader service: sess="+req.getSession(false));
    System.out.println("Uploader service: user="+req.getUserPrincipal());
    Enumeration<String> hdrs = req.getHeaderNames();
    while (hdrs.hasMoreElements()) {
    String hn = hdrs.nextElement();
    System.out.println (hn+" -> "+req.getHeader(hn));
    }
    if (req.getUserPrincipal() == null) {
    resp.setStatus(401);
    resp.addHeader("WWW-Authenticate", "Basic realm=\"openacs upload\"");
    return;
    }
    super.service(req, resp);
    }
     */
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>PUT</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Upload files";
    }

    private class MultipartDataSource implements javax.activation.DataSource {

        private ByteArrayInputStream streamIn;
        private String contentType;

        public MultipartDataSource(HttpServletRequest request) throws IOException {
            int c, o = 0, l = request.getContentLength();
            InputStream s = request.getInputStream();
            byte b[] = new byte[l];
            while ((c = s.read(b, o, l)) > 0 && l > 0) {
                o += c;
                l -= c;
            }
            streamIn = new ByteArrayInputStream(b);
            System.out.println("Available: " + streamIn.available());
            contentType = request.getContentType();
        }

        public InputStream getInputStream() throws IOException {
            return streamIn;
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException();
        }

        public String getContentType() {
            return contentType;
        }

        public String getName() {
            return "MultipartDataSource";
        }
    }
    // </editor-fold>
}
