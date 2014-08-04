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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ComponentHandler extends DefaultHandler {

    ComponentHandler() {
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("object")) {
        } else if (qName.equalsIgnoreCase("parameter")) {
        } else {
            if (debug) {
                System.out.println("unhandled tag under component: " + qName + " parent " + getParent());
            }
        }
    }

    @Override
    protected String getRootTag() {
        return "component";
    }
}
