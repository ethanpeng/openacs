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

public class Util {

    private static final String FWPATH = "org.openacs.fwbase";

    private static String getFirmwarePath(String fwbase) {
        if (fwbase == null) {
            fwbase = "c:/firmware/";
        }
        String osname = (String) System.getProperty("os.name");
        if (!osname.startsWith("Windows") && fwbase.charAt(1) == ':') {
            fwbase = fwbase.substring(2);
        }
        return fwbase;
    }

    /*
    public static String getFirmwarePath(ExternalContext ctx) {
    return getFirmwarePath(ctx.getInitParameter(Util.FWPATH));
    }
    public static String getFirmwarePath(HttpServlet servlet) {
    return getFirmwarePath(servlet.getInitParameter(Util.FWPATH));
    }
     */
}
