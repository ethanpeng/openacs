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

import java.util.ArrayList;
import java.util.Enumeration;
import org.openacs.CPELocal;
import org.openacs.message.GetParameterNamesResponse;
import org.openacs.message.GetParameterValuesResponse;
import org.openacs.utils.Ejb;

public class CpeBean {

    public class Params {

        public Params(String name, String value, boolean writable) {
            this.name = name;
            this.value = value;
            this.writable = writable;
        }
        private String name;
        private boolean writable;
        private String value;

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        public boolean getWritable() {
            return this.writable;
        }

        @Override
        public String toString() {
            return name + (writable ? "(W)" : "(R)") + "=" + value;
        }
    }

    /** Creates a new instance of CpeBean */
    public CpeBean() {
    }
    private String pathNames = ".";

    public void setPathNames(String pathNames) {
        this.pathNames = pathNames;
        names = null;
    }

    public String getPathNames() {
        return this.pathNames;
    }
    private ArrayList names = null;

    public ArrayList getNames() {
        if (names == null) {
            CPELocal cpe = Ejb.lookupCPEBean();
            GetParameterNamesResponse r = null; //cpe.GetParameterNames(oui, sn, pathNames, true);
            String ns[] = new String[r.names.size()];

            int count = 0;
            Enumeration k = r.names.keys();
            while (k.hasMoreElements()) {
                String n = (String) k.nextElement();
                if (!n.endsWith(".")) {
                    ns[count++] = n;
                    //System.out.println ("Add "+n);
                }
            }
            //System.out.println ("Total "+count);
            String ns2[] = new String[count];
            System.arraycopy(ns, 0, ns2, 0, count);
            //System.out.println ("Total "+ns.length);
            GetParameterValuesResponse v = null; //cpe.GetParameterValues(oui, sn, ns2);
            k = r.names.keys();
            ArrayList<Params> names = new ArrayList<Params>();
            //names = new ArrayList ();

            while (k.hasMoreElements()) {
                String n = (String) k.nextElement();
                names.add(new Params(n, n.endsWith(".") ? "" : (String) v.values.get(n), r.names.get(n).equals("1") ? true : false));
            }
            this.names = names;
        }
        return this.names;
    }
}
