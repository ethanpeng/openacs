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
package org.openacs.vendors;

import org.openacs.Configurator;

public class Broadcomm extends Vendor {

    private static String PROVISIONING_CODE = "provisioningCode=\"";

    public static boolean DetectFromConfig(String cfg) {
        return (cfg.contains("<psitree>") && cfg.contains("<tr69c "));
    }

    @Override
    public String UpdateConfig(String filename, String name, String version, String cfg) {
        StringBuilder r = new StringBuilder(cfg);
        int s = r.indexOf(PROVISIONING_CODE);
        if (s != -1) {
            int e = r.indexOf("\"", s + PROVISIONING_CODE.length());
            if (e != -1) {
                r.replace(s, e, Configurator.getProvisioningCode(name, version));
                return r.toString();
            }
        }
        return null;
    }
}
