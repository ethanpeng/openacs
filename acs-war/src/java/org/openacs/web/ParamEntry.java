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

public class ParamEntry {

    private String value;

    public ParamEntry() {
        System.out.println("PARAMENTRY::PARAMENTRY");
    }

    public ParamEntry(String value) {
        System.out.println("PARAMENTRY::PARAMENTRY ->" + value);
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean isAssignableFrom(Class<?> cls) {
        System.out.println("PARAMENTRY::isAssignableFrom");
        return true;
    }
}
