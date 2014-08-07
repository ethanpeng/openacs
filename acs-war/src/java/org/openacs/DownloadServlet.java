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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.*;

public class DownloadServlet extends HttpServlet {

    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String p = request.getPathInfo();
        int s = Math.max(p.lastIndexOf('\\'), p.lastIndexOf('/'));
        if (s != -1) {
            p = p.substring(s);
        }

        try {
            /*
            String fwbase = getInitParameter("org.openacs.fwbase");
            if (fwbase == null) fwbase = "c:/firmware/";
            String osname = (String)System.getProperty("os.name");
            if (!osname.startsWith("Windows") && fwbase.charAt(1)==':') {
            fwbase = fwbase.substring(2);
            }
             */
//            String fwbase = Util.getFirmwarePath(this);
            String fwbase = Application.getFirmwarePath();
            FileInputStream fin = new FileInputStream(fwbase + p);
            String ct = getInitParameter("defaultContentType");
            response.setContentType(ct != null ? ct : "application/octet-stream");

            response.setContentLength(fin.available());
            Logger.getLogger(getClass().getName()).log(Level.INFO, "uri=" + request.getRequestURI() + ", size=" + fin.available() + ", ct=" + ct);
            ServletOutputStream out = response.getOutputStream();
            /*
            ByteBuffer head = ByteBuffer.allocate(24);
            head.order(ByteOrder.BIG_ENDIAN);
            head.putLong (0x32574952455f5350L);
            head.putInt(1);
            head.putInt(0);
            head.putInt(0);
            head.putInt(fin.available());
            out.write(head.array());
             */
            byte[] buffer = new byte[8192];
            int r;
            while ((r = fin.read(buffer)) != -1) {
                out.write(buffer, 0, r);
            }
            fin.close();
            out.close();
        } catch (FileNotFoundException e) {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "404 uri=" + request.getRequestURI());
            response.setStatus(response.SC_NOT_FOUND);
        }
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
