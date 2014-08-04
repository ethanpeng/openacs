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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Vendor {

    protected String hwversion;
    // Update by comparing ProvisiniongCode
    public static final int CFG_UPDATE_METHOD_1 = 1;
    // Update by comparing DeviceInfo.VendorConfigFile.1.Name/Version
    public static final int CFG_UPDATE_METHOD_2 = 2;
    private static HashMap<String, Class> vendors = new HashMap<String, Class>();

    static {
        vendors.put("00147F", Thomson.class);
        vendors.put("0090D0", Thomson.class);
    }

    public String[] CheckConfig(String filename, String name, String version, String cfg) {
        if (cfg.contains("[ env.ini ]")) {
            Thomson v = new Thomson();
            v.hwversion = this.hwversion;
            return v.CheckConfig(filename, name, version, cfg);
        }
        if (Broadcomm.DetectFromConfig(cfg)) {
            // Looks like broadcom rebranded clone
        }
        return null;
    }

    public String UpdateConfig(String filename, String name, String version, String cfg) {
        if (Broadcomm.DetectFromConfig(cfg)) {
            Vendor v = new Broadcomm();
            return v.UpdateConfig(filename, name, version, cfg);
        }
        return null;
    }

    public static Vendor getVendor(String oui, String hardwareClass, String hardwareVersion) {
        try {
            Class c = vendors.get(oui);
//            System.out.println ("Class="+c);
            Vendor v;
            if (c == null) {
                v = new Vendor();
            } else {
                Constructor<?> m = c.getConstructor();
                //          System.out.println ("Constructor="+m);
                v = (Vendor) m.newInstance();
            }
            v.hwversion = hardwareVersion;
            return v;
        } catch (Exception ex) {
            Logger.getLogger(Vendor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Vendor();
    }

    public int getConfigUpdateMethod() {
        return CFG_UPDATE_METHOD_1;
    }
}
