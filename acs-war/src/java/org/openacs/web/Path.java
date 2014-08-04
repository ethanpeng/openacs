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

public class Path {

        private String element;
        private String fromRoot;

        public String getFromRoot() {
            return fromRoot;
        }

        public Path() {
        }

        public Path(String e, String f) {
            element = e;
            fromRoot = f;
        }

        public Path[] fromString(String p) {
            if (p == null || p.equals("")) return new Path [0];
            String ps[] = p.split("\\.");
            String fr = "";
            Path pr[] = new Path[ps.length];
            for (int i = 0; i < ps.length; i++) {
                if (!fr.equals("")) {
                    fr += ".";
                }
                pr[i] = new Path(ps[i], fr += ps[i]);
            }
            return pr;
        }

        @Override
        public String toString() {
            return element;
        }
    }

