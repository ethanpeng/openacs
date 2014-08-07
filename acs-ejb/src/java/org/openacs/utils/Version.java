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
package org.openacs.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Version {

    /** Creates a new instance of Version */
    public Version(String sv) {
        Set(sv);
    }

    void Set(String sv) {
        if (sv == null || sv.equals("")) {
            v = new int[1];
            v[0] = 0;
            return;
        }
        String sva[] = sv.split("\\.");
        v = new int[sva.length];
        boolean fEntireValid = true;
        for (int i = 0; i < sva.length; i++) {
            try {
                v[i] = Integer.parseInt(sva[i]);
            } catch (NumberFormatException ex) {
                fEntireValid = false;
            }
        }
        if (!fEntireValid) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Invalid version string: " + sv + " Non numeric elements assumed to be 0");
        }

    }

    public boolean isUptodate(Version ver2) {
        int v2[] = ver2.v;
        int c = (v2.length < v.length) ? v2.length : v.length;
        for (int i = 0; i < c; i++) {
            if (v2[i] < v[i]) {
                return false;
            }
            if (v2[i] > v[i]) {
                return true;
            }
        }
        if (v2.length < v.length) {
            return false;
        }
        return true;
    }
    /*
    static public void validate (FacesContext context, UIComponent toValidate, Object value) {
    if (!Pattern.matches("^[0-9]+(\\.[0-9]+)*", (String)value)) {
    ((UIInput)toValidate).setValid(false);
    context.addMessage(toValidate.getClientId(context), new FacesMessage("Version format must be digits[.digits]"));
    }
    }
     */
    private int[] v;

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < v.length; i++) {
            if (!s.equals("")) {
                s += ".";
            }
            s += v[i];
        }

        return s;
    }
}
