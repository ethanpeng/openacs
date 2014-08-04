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

public class ParamHandler extends DefaultHandler {

    public static String TOP_TAG = "parameter";
    private Parameter currentParam;
    private String objectName;

    public ParamHandler(String objectName) {
        this.objectName = objectName;
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

        if (qName.equals("description") && isChildOf(TOP_TAG)) {
            currentParam.setDescription(lastText);
        } else if (qName.equals("description") && isChildOf("enumeration")) {
        } else if (qName.equals("description") && isChildOf("range")) {
        } else if (qName.equals(TOP_TAG)) {
            if (getParent().equals("component") || getParentParent().equals("component")) {
                addComponentParameter(currentParam);
            } else {
                Parameter.Add(currentParam);
            }
        }
    }

    public Parameter getParam() {
        return currentParam;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String tag = qName;

        if (tag.equals(TOP_TAG)) {
            String base = attributes.getValue("base");
            if (base != null) {
                if (isComponent()) {
                    currentParam = lookupComponentParameter(objectName + base);
                } else {
                    currentParam = Parameter.lookup(objectName + base);
                }
                if (currentParam == null) {
                    currentParam = new Parameter();
                    currentParam.setName(objectName + base);
                }
            } else {
                currentParam = new Parameter();
            }
            currentParam.setModel(Model.getName());
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
            } catch (NumberFormatException e) {
                if (value.equals("4294967295")) {
                    ivalue = Integer.MAX_VALUE;
                }
            } catch (Exception e) {
            }
            Boolean bvalue = (value.equalsIgnoreCase("true") || value.equals("1") || value.equalsIgnoreCase("yes")) ? true : false;

            ignore(tag.equals("description") && isChildOf(TOP_TAG));
            ignore(tag.equals("description") && isChildOf("enumeration"));
            ignore(tag.equals("description") && isChildOf("range"));
            ignore(tag.equals("description") && isChildOf("pattern"));

            ignore(tag.equals(TOP_TAG) && attr.equals("base"));
            handleAttribute(tag.equals(TOP_TAG) && attr.equals("name"), currentParam, "Name", objectName + value);
            handleAttribute(tag.equals(TOP_TAG) && attr.equals("access"), currentParam, "ReadOnly", value.equals("readOnly"));
            handleAttribute(tag.equals(TOP_TAG) && attr.equals("activeNotify"), currentParam, "ReadOnly", !value.equals("canDeny"));
            handleAttribute(tag.equals(TOP_TAG) && attr.equals("ref"), currentParam, "Ref", value);
            handleAttribute(tag.equals(TOP_TAG) && attr.equals("requirement"), currentParam, "Requirement", value);

            handleAttribute(tag.equals("units") && attr.equals("value"), currentParam, "Units", value);

            handleAttribute(getParent().equals("list") && tag.equals("size") && attr.equals("maxLength"), currentParam, "MaxLength", ivalue);
            handleAttribute(tag.equals("size") && attr.equals("maxLength"), currentParam, "MaxLength", ivalue);
            handleAttribute(tag.equals("size") && attr.equals("minLength"), currentParam, "MinLength", ivalue);

            handleAttribute(getParent().equals("string") && tag.equals("pattern") && attr.equals("value"), currentParam, "Pattern", value);
            handleAttribute(getParent().equals("string") && tag.equals("enumeration") && attr.equals("value"), currentParam, "Enumeration", value);

            ignore(tag.equals("default") && isChildOf("syntax"));
            handleAttribute(isChildOf("syntax") && tag.equals("dateTime"), currentParam, "Type", Type.DATETIME);
            handleAttribute(isChildOf("syntax") && tag.equals("dataType"), currentParam, "Type", Type.DATATYPE);
            handleAttribute(isChildOf("syntax") && tag.equals("default") && attr.equals("value"), currentParam, "DefaultValue", value);
            if (isChildOf("syntax") && tag.equals("dataType") && attr.equals("ref")) {
                handleAttribute(true, currentParam, "TypeName", value);
                currentParam.setType(DataType.lookup(value));
            }
            handleAttribute(isChildOf("syntax") && tag.equals("int"), currentParam, "Type", Type.INT);
            handleAttribute(isChildOf("syntax") && tag.equals("string"), currentParam, "Type", Type.STRING);
            handleAttribute(isChildOf("syntax") && tag.equals("base64"), currentParam, "Type", Type.BASE64);
            handleAttribute(isChildOf("syntax") && tag.equals("unsignedInt"), currentParam, "Type", Type.UNSIGNEDINT);
            handleAttribute(isChildOf("syntax") && tag.equals("boolean"), currentParam, "Type", Type.BOOLEAN);
            handleAttribute(isChildOf("syntax") && tag.equals("list"), currentParam, "List", true);

            handleAttribute((isChildOf("unsignedInt") || isChildOf("int")) && tag.equals("range") && attr.equals("minInclusive"), currentParam, "Min", ivalue);
            handleAttribute((isChildOf("unsignedInt") || isChildOf("int")) && tag.equals("range") && attr.equals("maxInclusive"), currentParam, "Max", ivalue);
            handleAttribute(tag.equals("syntax") && attr.equals("hidden"), currentParam, "Hidden", true);
            handleAttribute(tag.equals("syntax") && attr.equals("command"), currentParam, "Command", bvalue);

            handleAttribute(tag.equals(TOP_TAG) && attr.equals("status"), currentParam, "Status", value);

            ignore(tag.equals("pathRef") && attr.equals("refType"));
            ignore(tag.equals("pathRef") && attr.equals("targetParent"));
            ignore(tag.equals("pathRef") && attr.equals("targetType"));
            ignore(tag.equals(TOP_TAG) && attr.equals("dmr:previousParameter"));
            ignore(tag.equals("enumeration") && attr.equals("access"));
            ignore(tag.equals("enumeration") && attr.equals("optional"));
            ignore(tag.equals("enumeration") && attr.equals("status"));
            ignore(tag.equals("enumerationRef"));
            ignore(attr.equals("status"));
            ignore(attr.equals("forcedInform"));


            if (!handled && !emptyattrs) {
                System.out.println("ParamHandler: unhandled tag=" + tag + " attr=" + attr + " parent=" + getParent() + " value=" + value);
            }
        }
    }
}
