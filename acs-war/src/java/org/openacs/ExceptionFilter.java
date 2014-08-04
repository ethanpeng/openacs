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
import java.io.PrintWriter;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ExceptionFilter implements Filter {

    public void init(FilterConfig arg0) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {
            if (ex.getCause() instanceof ViewExpiredException) {
                ViewExpiredException exv = (ViewExpiredException) ex.getCause();
            }
            System.out.println("Catched exception in catcher: " + ex + " class " + ex.getClass().getName());
            System.out.println("Catched exception in catcher: " + ex + " class " + ex.getCause().getClass().getName());
            System.out.println("Catched exception in catcher: " + FacesContext.getCurrentInstance());
//            request.getRequestDispatcher("/error.jsf").forward(request,response);
            PrintWriter out = response.getWriter();
            out.println(ex.getMessage());
            out.flush();
        }
    }

    public void destroy() {
    }
}
