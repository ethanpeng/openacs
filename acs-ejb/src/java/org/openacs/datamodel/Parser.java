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

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class Parser {

    public static void setDir(String dir) {
        DefaultHandler.setDirectory(dir);
    }

    public static void Parse(String name) throws SAXException, ParserConfigurationException, IOException {
        System.out.println("PARSE: " + name);
        DefaultHandler.Parse(name);
    }

    public static void setStreamProvider(StreamProvider streamProvider) {
        DefaultHandler.setStreamProvider(streamProvider);

    }
}
