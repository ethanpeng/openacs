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
package org.openacs.datamodel;

public enum Type {

    OBJECT, UNSIGNEDINT, STRING, INT, DATETIME, DATATYPE, BOOLEAN, BASE64, UNDEFINED;

    @Override
    public String toString() {
        if (OBJECT.equals(this)) {
            return "object";
        }
        if (UNSIGNEDINT.equals(this)) {
            return "unsignedInt";
        }
        if (STRING.equals(this)) {
            return "string";
        }
        if (INT.equals(this)) {
            return "int";
        }
        if (DATETIME.equals(this)) {
            return "dateTime";
        }

        if (DATATYPE.equals(this)) {
            return "datatype";
        }
        if (BOOLEAN.equals(this)) {
            return "boolean";
        }
        if (BASE64.equals(this)) {
            return "base64";
        }

        return null;
    }
}
