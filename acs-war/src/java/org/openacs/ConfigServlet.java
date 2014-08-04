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
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Logger;
import javax.ejb.FinderException;

import javax.servlet.*;
import javax.servlet.http.*;
import org.openacs.utils.Ejb;

public class ConfigServlet extends HttpServlet {

    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");

        ConfigurationLocalHome cs = Ejb.lookupConfigurationBean();
        HostsLocalHome hs = Ejb.lookupHostsBean();
        Logger.getLogger(ConfigServlet.class.getName()).info("CfgServlet: from ip=" + request.getRemoteAddr());
//        System.out.println ("CfgServlet: from ip="+request.getRemoteAddr());
        try {
            Collection _h = hs.findByIpM(request.getRemoteAddr());
            if (_h.isEmpty()) {
                response.setStatus(response.SC_NOT_FOUND);
                return;
            }
            HostsLocal host = (HostsLocal) _h.iterator().next();
            System.out.println("CfgServlet: Found host hwid=" + host.getHwid() + " config=" + host.getConfigname());
            if (host.getConfigname() != null) {
                ConfigurationLocal cfg = cs.findByPrimaryKey(new ConfigurationPK(host.getHwid(), host.getConfigname()));
                String cfgString = new String(cfg.getConfig());
//                System.out.println ("ConfigServlet: sl="+cfgString.length()+" cl="+cfg.getConfig().length);
                cfgString = processVars(cfgString, host);
                byte[] cfgBytes = cfgString.getBytes();

                response.setContentLength(cfgBytes.length);
                ServletOutputStream out = response.getOutputStream();
                out.write(cfgBytes);
                out.flush();
                out.close();
            } else {
                response.setStatus(response.SC_NOT_FOUND);
            }
        } catch (FinderException ex) {
            System.out.println("CfgServlet: finderexception" + ex);
            response.setStatus(response.SC_NOT_FOUND);
        }
    }

    private String processVars(String cfg, HostsLocal host) throws IOException {
        StringBuilder c = new StringBuilder(cfg.length() * 2);
        int fromIndex = 0, curIndex;
        Properties props = new Properties();
        props.load(new ByteArrayInputStream(host.getProps()));
        while ((curIndex = cfg.indexOf("#{", fromIndex)) != -1) {
            fromIndex = curIndex;
            int toIndex = cfg.indexOf('}', fromIndex);
            if (toIndex == -1) {
                Logger.getLogger(ConfigServlet.class.getName()).severe("Unclosed #{ at char " + curIndex + " of config " + host.getConfigname());
                throw new RuntimeException();
            } else {
                String name = cfg.substring(curIndex + 2, toIndex);
                c.append(cfg.substring(fromIndex, curIndex));
                String value = props.getProperty(name);
                if (value != null) {
                    c.append(value);
                } else {
                    Logger.getLogger(ConfigServlet.class.getName()).warning("Property '" + name + "' not found at char " + curIndex + " of config " + host.getConfigname());
                }
            }
            fromIndex = toIndex + 1;
        }
        c.append(cfg.substring(fromIndex));
        return c.toString();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Serves configurations";
    }
    // </editor-fold>
}
