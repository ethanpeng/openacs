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

public class BibiliographyHandler extends DefaultHandler {

    private BibliographyEntry entry;
    public static final String TOP_TAG = "bibliography";

    BibiliographyHandler() {
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("reference")) {
            BibliographyEntry.add(entry);
        } else if (qName.equals("name")) {
            entry.setName(lastText);
        } else if (qName.equals("title")) {
            entry.setTitle(lastText);
        } else if (qName.equals("organization")) {
            entry.setOrganization(lastText);
        } else if (qName.equals("category")) {
            entry.setCategory(lastText);
        } else if (qName.equals("date")) {
            entry.setDate(lastText);
        } else if (qName.equals("hyperlink")) {
            entry.setHyperlink(lastText);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("reference")) {
            entry = new BibliographyEntry();
            entry.setId(attributes.getValue("id"));
        }
    }

    @Override
    protected String getRootTag() {
        return TOP_TAG;
    }
}
