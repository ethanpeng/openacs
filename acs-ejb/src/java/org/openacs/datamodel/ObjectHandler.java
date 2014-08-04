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

public class ObjectHandler extends DefaultHandler {

    public static final String TOP_TAG = "object";

    ObjectHandler(boolean isService) {
        this.isService = isService;
    }

    public void setText(String text) {
        lastText = text;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase(TOP_TAG)) {
            if (getParent().equals("component")) {
                addComponentParameter(parameterCurrent);
            } else {
                Parameter.Add(parameterCurrent);
            }
        } else if (qName.equalsIgnoreCase("description")) {
            parameterCurrent.setDescription(lastText);
        }
    }
    private Parameter parameterCurrent;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String tag = qName;

        if (qName.equalsIgnoreCase(TOP_TAG)) {
            String base = attributes.getValue("base");
            parameterCurrent = null;
            if (base != null) {
                parameterCurrent = Parameter.lookup(base);
            }
            if (parameterCurrent == null) {
                parameterCurrent = new Parameter();
                parameterCurrent.setType(Type.OBJECT);
                if (base != null) {
                    parameterCurrent.setName(isService ? ".Services." + base : base);
                }
            }
            parameterCurrent.setModel(Model.getName());
        } else if (tag.equalsIgnoreCase("uniqueKey")) {
            parameterCurrent.appendKey();
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
                if (value.equals("unbounded")) {
                    ivalue = Integer.MAX_VALUE;
                }
            }

            ignore(tag.equals(TOP_TAG) && attr.equals("base"));
            ignore(tag.equals(TOP_TAG) && attr.equals("ref"));
            ignore(tag.equals(TOP_TAG) && attr.equals("requirement"));
            ignore(tag.equals(TOP_TAG) && attr.equals("dmr:previousObject"));
            ignore(tag.equals("description"));

            ignore(tag.equals("uniqueKey") && attr.equals("functional"));
            ignore(tag.equals(TOP_TAG) && attr.equals("dmr:noUniqueKeys"));

            handleAttribute(tag.equals(TOP_TAG) && attr.equals("name"), parameterCurrent, "Name", isService ? ".Services." + value : value);
            handleAttribute(tag.equals(TOP_TAG) && attr.equals("enableParameter"), parameterCurrent, "EnableParameter", value);
            handleAttribute(tag.equals(TOP_TAG) && attr.equals("access"), parameterCurrent, "ReadOnly", value.equals("readOnly"));
            handleAttribute(tag.equals(TOP_TAG) && attr.equals("minEntries"), parameterCurrent, "MinEntries", ivalue);
            handleAttribute(tag.equals(TOP_TAG) && attr.equals("maxEntries"), parameterCurrent, "MaxEntries", ivalue);
            handleAttribute(tag.equals(TOP_TAG) && attr.equals("numEntriesParameter"), parameterCurrent, "NumEntriesParameter", value);

            handleAttribute(tag.equals("parameter") && attr.equals("ref"), parameterCurrent, "KeyField", value);
            handleAttribute(tag.equals("uniqueKey") && attr.equals("name"), parameterCurrent, "UniqueKey", value);
            handleAttribute(tag.equals(TOP_TAG) && attr.equals("status"), parameterCurrent, "Status", value);

            if (!handled && !emptyattrs) {
                System.out.println("ObjectHandler: unhandled tag=" + tag + " attr=" + attr + " parent=" + getParent());
            }
        }
    }

    public Parameter getParam() {
        return parameterCurrent;
    }

    @Override
    protected String getRootTag() {
        return TOP_TAG;
    }
}
