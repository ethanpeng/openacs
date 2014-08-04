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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DefaultHandler extends org.xml.sax.helpers.DefaultHandler {

    protected static String lastText;
    static List<String> tags = new ArrayList<String>();
    DefaultHandler parentHandler;
    private static List<DefaultHandler> handlers = new LinkedList<DefaultHandler>();
    boolean isService = false;
    private static String dirDefault = "c:/tmp/";
    protected static Map<String, List<Parameter>> components = new HashMap<String, List<Parameter>>();
    protected static Map<String, Boolean> filesparsed = new HashMap<String, Boolean>();
    protected static Map<String, DataType> datatypes = new HashMap<String, DataType>();
    protected static String currentComponent = null;
    protected String filename;
    protected static boolean debug = false;
    private static StreamProvider streamProvider = new StreamProvider() {

        public InputStream getStream(String name) {
            try {
                return new FileInputStream(name);
            } catch (FileNotFoundException ex) {
            }
            return null;
        }
    };

    public static void setStreamProvider(StreamProvider streamProvider) {
        DefaultHandler.streamProvider = streamProvider;
    }

    public DefaultHandler() {
    }

    public DefaultHandler(DefaultHandler parentHandler) {
        this.parentHandler = parentHandler;
    }

    private void pushHandler(DefaultHandler handler) {
        handlers.add(handler);
    }

    private DefaultHandler popHandler(String tag) {
        if (!handlers.isEmpty() && handlers.get(handlers.size() - 1).getRootTag().equals(tag)) {
            return handlers.remove(handlers.size() - 1);
        }
        return null;
    }

    private DefaultHandler getHandler() {
        if (!handlers.isEmpty()) {
            return handlers.get(handlers.size() - 1);
        }
        return null;
    }

    private DefaultHandler getHandlerPrevious() {
        return handlers.get(handlers.size() - 2);
    }

    protected void setName(String name) {
        filename = name;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        tags.add(qName);
        if (debug) {
            System.out.println("Start Element: " + qName + " parent: " + getParent());
        }
        dumpAttrs(attributes);
        lastText = "";

        if (qName.equalsIgnoreCase("object") && (getParent().equals("model") || getParent().equals("component"))) {
            pushHandler(new ObjectHandler(isService));
        } else if (qName.equalsIgnoreCase(ParamHandler.TOP_TAG) && getParent().equals("object") && !isAnyChildOf("profile")) {
            ObjectHandler oh = (ObjectHandler) getHandler();
            pushHandler(new ParamHandler((oh != null ? oh.getParam().getName() : "")));
        } else if (qName.equalsIgnoreCase(DatatypeHandler.TOP_TAG) && getParent().equals("dm:document")) {
            pushHandler(new DatatypeHandler());
        } else if (qName.equalsIgnoreCase(BibiliographyHandler.TOP_TAG) && getParent().equals("dm:document")) {
            pushHandler(new BibiliographyHandler());
        } else if (qName.equalsIgnoreCase("component")) {
            if (getParent().equals("import")) {
                String n = attributes.getValue("name");
                dumpTags();
                if (n != null) {
                    // TODO really import or maybe do nothing 
                }
            } else if (getParent().equals("model")) {
                dumpTags();
                String path = attributes.getValue("path");
                String ref = attributes.getValue("ref");
                if (ref != null && path != null) {
                    List<Parameter> cs = components.get(ref);
                    if (cs != null) {
                        for (Parameter p : cs) {
                            // copy parameters
                            Parameter pc = p.clone();
                            pc.setName(path + pc.getName());
                            Parameter.Add(pc);
                        }
                    }
                }
            } else {
                dumpTags();
                currentComponent = attributes.getValue("name");
                pushHandler(new ComponentHandler());
            }
        } else if (qName.equalsIgnoreCase("model")) {
            try {
                isService = attributes.getValue("isService").equals("true");
            } catch (NullPointerException e) {
            }
            Model.setName(attributes.getValue("name"));
        } else if (qName.equalsIgnoreCase("import")) {
            String fname = attributes.getValue("file");
            InputStream is = streamProvider.getStream(dirDefault + fname);
            if (is == null) {
                String fnameBase = fname.substring(0, fname.length() - 4);
                fname = fnameBase + "-0.xml";
                is = streamProvider.getStream(dirDefault + fname);
            }

            try {
                Parse(is, fname);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("EXCEPTION: (import " + fname + ") " + e.getMessage());
            }
        } else if (qName.equalsIgnoreCase("profile")) {
            pushHandler(new NullHandler(qName));
        } else if (getParent().equals("")) {
            handlers.clear();
        } else {
        }

        DefaultHandler h = getHandler();
        if (h != null) {
            h.startElement(uri, localName, qName, attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (debug) {
            System.out.println("END: " + qName);
        }

        DefaultHandler h = null;
        if (!handlers.isEmpty()) {
            h = handlers.get(handlers.size() - 1);
            if (h.getRootTag().equals(qName)) {
                handlers.remove(handlers.size() - 1);
            }
        }

        if (h != null) {
            h.endElement(uri, localName, qName);
        }
        tags.remove(tags.size() - 1);
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (length > 0) {
            lastText = new String(ch, start, length);
        }
    }

    protected String getParent() {
        if (tags.size() > 1) {
            return tags.get(tags.size() - 2);
        }
        return "";
    }

    protected String getParentParent() {
        if (tags.size() > 2) {
            return tags.get(tags.size() - 3);
        }
        return "";
    }

    protected boolean isChildOf(String n) {
        return n.equals(getParent());
    }

    protected boolean isAnyChildOf(String n) {
        for (String tag : tags) {
            if (tag.equals(n)) {
                return true;
            }
        }
        return false;
    }

    protected String getCurrentTag() {
        if (tags.size() > 0) {
            return tags.get(tags.size() - 1);
        }
        return "";
    }

    void dumpAttrs(Attributes attributes) {
        if (!debug) {
            return;
        }
        int l = attributes.getLength();
        for (int i = 0; i < l; i++) {
            String n = attributes.getLocalName(i);
            String qn = attributes.getQName(i);
            String v = attributes.getValue(i);
            System.out.println(getCurrentTag() + ": " + n + " (" + qn + ") -> " + v);
        }
    }

    protected String getRootTag() {
        return "";
    }

    public static void Parse(String filename) throws SAXException, ParserConfigurationException, IOException {
        if (debug) {
            System.out.println("PARSE: " + filename);
        }

        if (filesparsed.get(filename) == null) {
            try {
                Parse(streamProvider.getStream(dirDefault + filename), filename);
                filesparsed.put(filename, Boolean.TRUE);
            } catch (Exception e) {
                System.out.println("EXCEPTION: " + e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            }
        }
        if (debug) {
            System.out.println("ENDPARSE: " + filename);
        }
    }

    public static void Parse(InputStream istream, String name) throws SAXException, ParserConfigurationException, IOException {
        if (debug) {
            System.out.println("PARSE: " + name);
        }

        if (filesparsed.get(name) == null) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            DefaultHandler h = new DefaultHandler();
            h.setName(name);
            saxParser.parse(istream, h);
            filesparsed.put(name, Boolean.TRUE);
        }
        if (debug) {
            System.out.println("ENDPARSE: " + name);
        }

    }

    protected void addComponentParameter(Parameter p) {
        List<Parameter> c = components.get(currentComponent);
        if (c == null) {
            c = new ArrayList<Parameter>();
            components.put(currentComponent, c);
        }
        c.add(p);

    }

    protected void addDatatype(DataType t) {
        datatypes.put(t.getName(), t);
    }

    protected DataType lookupType(String name) {
        return datatypes.get(name);
    }

    protected void dumpTags() {
        if (!debug) {
            return;
        }
        for (String t : tags) {
            System.out.print(t + "->");
        }
        System.out.println();

    }

    void unknownAttribute(String name) {
        if (debug) {
            System.out.println("Unknown attribute " + name + " on " + getCurrentTag());
        }
    }
    protected boolean handled = false;

    void ignore(String name, String parent, String tag) {
        if (!handled) {
            if (name.equals(tag) && isChildOf(parent)) {
                handled = true;
            }
        }
    }

    void ignore(boolean h) {
        if (!handled && h) {
            handled = true;
        }
    }

    void handleAttribute(String tag, String name, String attr, Object o, String setname, Object value) {
        if (!handled && name.equals(attr)) {
            try {
                Method m = o.getClass().getMethod("set" + setname, value.getClass());
                m.invoke(o, value);
                handled = true;
                return;
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
            try {
                Method m = o.getClass().getMethod("add" + setname, value.getClass());
                m.invoke(o, value);
                handled = true;
                return;
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    void handleAttribute(boolean h, Object o, String setname, Object value) {
        if (!handled && h) {
            try {
                Method m = o.getClass().getMethod("set" + setname, value.getClass());
                m.invoke(o, value);
                handled = true;
                return;
            } catch (Exception e) {
            }
            try {
                Method m = o.getClass().getMethod("add" + setname, value.getClass());
                m.invoke(o, value);
                handled = true;
                return;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    boolean isComponent() {
        return (getParent().equals("component") || getParentParent().equals("component"));
    }

    Parameter lookupComponentParameter(String name) {
        Parameter param = null;
        List<Parameter> c = components.get(currentComponent);
        if (c == null) {
            param = new Parameter();
            param.setName(name);
            List<Parameter> l = new ArrayList<Parameter>();
            l.add(param);
            components.put(currentComponent, l);
            return param;
        }
        for (Parameter p : c) {
            if (p.getName().equals(name)) {
                param = p;
                break;
            }
        }
        return param;
    }

    public static Set<String> getFilesParsed() {
        return filesparsed.keySet();
    }

    public static void setDebug(boolean d) {
        debug = d;
    }

    public static void setDirectory(String dir) {
        dirDefault = dir;
    }
}
