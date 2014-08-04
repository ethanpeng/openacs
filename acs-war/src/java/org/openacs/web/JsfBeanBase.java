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
package org.openacs.web;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class JsfBeanBase {

    private void setMessage(FacesMessage.Severity severity, String msg) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        String m = msg;
        if (m != null) {
            m.replaceAll("\n", "<br>");
        } else {
            m = "Unknown error.";
        }
        ctx.addMessage(null, new FacesMessage(severity, m, null));
    }

    protected void setErrorMessage(String msg) {
        setMessage(FacesMessage.SEVERITY_ERROR, msg);
    }

    protected void setInfoMessage(String msg) {
        setMessage(FacesMessage.SEVERITY_INFO, msg);
    }

    protected void setWarningMessage(String msg) {
        setMessage(FacesMessage.SEVERITY_WARN, msg);
    }

    protected void setErrorMessage(String[] msg) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        for (String m : msg) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, m, null));
        }
    }

    protected void setSaved() {
        setInfoMessage("Saved.");
    }

    protected void setDeleted() {
        setInfoMessage("Deleted.");
    }

    protected static Hashtable<String, String> ejb2map(Object b) {
        Hashtable<String, String> h = new Hashtable<String, String>();
        Method[] ma = b.getClass().getDeclaredMethods();
        for (Method m : ma) {
            String mn = m.getName();
            if (mn.startsWith("get")) {
                String k = mn.substring(3);
                try {
                    Object value = b.getClass().getMethod(mn).invoke(b);
                    if (value != null) {
                        h.put(k, value.toString());
                    }
//                    else
//                        h.put(k, "n/a");
                } catch (Exception ex) {
                    Logger.getLogger(HostsBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return h;
    }
}
