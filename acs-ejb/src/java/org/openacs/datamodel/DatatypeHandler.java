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

public class DatatypeHandler extends DefaultHandler {

    public static String TOP_TAG = "dataType";
    private DataType currentDatatype;

    public DatatypeHandler() {
    }

    @Override
    protected String getRootTag() {
        return TOP_TAG;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
//        System.out.println ("ENDTAG: "+localName);
        if (qName.equals("description") && isChildOf(TOP_TAG)) {
            currentDatatype.setDescription(lastText);
        } else if (qName.equals("description") && isChildOf("enumeration")) {
            System.out.println("Enum description: " + lastText);
        } else if (qName.equals("description") && isChildOf("range")) {
            System.out.println("Range description: " + lastText);
        } else if (qName.equals(TOP_TAG)) {
            if (getParent().equals("component") || getParentParent().equals("component")) {
                addDatatype(currentDatatype);
            } else {
                DataType.Add(currentDatatype);
            }
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String tag = qName;

        if (tag.equals(TOP_TAG)) {
            String base = attributes.getValue("base");
            if (base != null) {
                DataType t = DataType.lookup(base);
                currentDatatype = t.clone();
            } else {
                currentDatatype = new DataType();
            }
        }

        int l = attributes.getLength();
        boolean emptyattrs = false;
        if (l == 0) {
            emptyattrs = true;
            l = 1;
        }
        for (int i = 0; i < l; i++) {
            handled = false;
            String attr = (emptyattrs) ? "" : attributes.getQName(i); //attributes.getLocalName(i);
            String value = (emptyattrs) ? "" : attributes.getValue(i);
            Integer ivalue = null;
            try {
                ivalue = Integer.parseInt(value);
            } catch (Exception e) {
            }

            ignore(tag.equals("description") && isChildOf(TOP_TAG));
            ignore(tag.equals("description") && isChildOf("enumeration"));
            ignore(tag.equals("description") && isChildOf("range"));
            ignore(tag.equals("description") && isChildOf("pattern"));

            ignore(tag.equals(TOP_TAG) && attr.equals("base"));
            handleAttribute(tag.equals(TOP_TAG) && attr.equals("name"), currentDatatype, "Name", value);
            handleAttribute(tag.equals("units") && attr.equals("value"), currentDatatype, "Units", value);

            handleAttribute(getParent().equals("list") && tag.equals("size") && attr.equals("maxLength"), currentDatatype, "MaxLength", ivalue);
            handleAttribute(tag.equals("size") && attr.equals("maxLength"), currentDatatype, "MaxLength", ivalue);
            handleAttribute(getParent().equals("dataType") && tag.equals("pattern") && attr.equals("value"), currentDatatype, "Pattern", value);
            handleAttribute(getParent().equals("dataType") && tag.equals("enumeration") && attr.equals("value"), currentDatatype, "Enumeration", value);

            handleAttribute(getParent().equals("string") && tag.equals("pattern") && attr.equals("value"), currentDatatype, "Pattern", value);
            handleAttribute(getParent().equals("string") && tag.equals("enumeration") && attr.equals("value"), currentDatatype, "Enumeration", value);

//            ignore (tag.equals("syntax") && isChildOf(TOP_TAG));
            ignore(tag.equals("default") && isChildOf("syntax"));
            handleAttribute(isChildOf(TOP_TAG) && tag.equals("dateTime"), currentDatatype, "Type", Type.DATETIME);
            handleAttribute(isChildOf(TOP_TAG) && tag.equals("dataType"), currentDatatype, "Type", Type.DATATYPE);
            handleAttribute(isChildOf(TOP_TAG) && tag.equals("int"), currentDatatype, "Type", Type.INT);
            handleAttribute(isChildOf(TOP_TAG) && tag.equals("string"), currentDatatype, "Type", Type.STRING);
            handleAttribute(isChildOf(TOP_TAG) && tag.equals("base64"), currentDatatype, "Type", Type.BASE64);
            handleAttribute(isChildOf(TOP_TAG) && tag.equals("unsignedInt"), currentDatatype, "Type", Type.UNSIGNEDINT);
            handleAttribute(isChildOf(TOP_TAG) && tag.equals("boolean"), currentDatatype, "Type", Type.BOOLEAN);
            handleAttribute(isChildOf(TOP_TAG) && tag.equals("list"), currentDatatype, "List", true);

            handleAttribute((isChildOf("unsignedInt") || isChildOf("int")) && tag.equals("range") && attr.equals("minInclusive"), currentDatatype, "Min", ivalue);
            handleAttribute((isChildOf("unsignedInt") || isChildOf("int")) && tag.equals("range") && attr.equals("maxInclusive"), currentDatatype, "Max", ivalue);
//            handleAttribute((isChildOf("unsignedInt") || isChildOf("int")) && tag.equals("syntax") && attr.equals("hidden"), currentParam, "Hidden", true);

            if (!handled && !emptyattrs && debug) {
                System.out.println("DataTypeHandler: unhandled tag=" + tag + " attr=" + attr + " parent=" + getParent());
            }
        }
    }
}
