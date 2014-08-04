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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Form {

    public class Param {

        private String varname;
        private String description;
        private boolean usrparam;

        public Param(String varname, String description, boolean usrparam) {
            this.varname = varname;
            this.description = description;
            this.usrparam = usrparam;
        }

        public String getDescription() {
            String d = varname;
            if (description != null) {
                d = description;
            } else {
                int ix = varname.lastIndexOf('.');
                if (ix != -1) {
                    d = varname.substring(ix + 1);
                    if (d.equals("Enable") && ix > 1) {
                        ix = varname.lastIndexOf('.', ix - 1);
                        if (ix != -1) {
                            d = varname.substring(ix + 1);
                            d.replace('.', ' ');
                        }
                        if (d.startsWith("{i}.")) {
                            d = d.substring(4);
                        }
                    }
                }
            }
            return d;
        }

        public boolean isUsrparam() {
            return usrparam;
        }

        public String getVarname() {
            return varname;
        }

        @Override
        public String toString() {
            return varname + " \"" + description + "\"";
        }
    }

    public class Tab {

        private String name;
        private Param[] params;

        public Tab(String name, Param[] params) {
            this.params = params;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Param[] getParams() {

            return params;
        }
    }
    private String name;
    private static Map<String, Tab[]> f = new Hashtable<String, Tab[]>();

    public Form(String name) {
        this.name = name;
    }

    public Form() {
    }

    public Map getForms() {
        //System.out.println("Form::getForms");
        return f;
    }

    private static String attrvalue(NamedNodeMap attrs, String name, String def) {
        Node a = attrs.getNamedItem(name);
        if (a != null) {
            String v = a.getNodeValue();
            if (v != null) {
                return v;
            }
        }
        return def;
    }

    private static boolean attrvalueBoolean(NamedNodeMap attrs, String name, boolean def) {
        try {
            return Boolean.parseBoolean(attrvalue(attrs, name, null));
        } catch (Exception e) {
            return def;
        }
    }

    public void Load(String name, InputStream is) {
        try {
            DocumentBuilder docbuilder;
            docbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = docbuilder.parse(is);

            Node nodeForm = doc.getChildNodes().item(0);
            if (!nodeForm.getNodeName().equals("form")) {
                return;
            }

            String nameForm = attrvalue(nodeForm.getAttributes(), "name", null);
            NodeList tabs = nodeForm.getChildNodes();

            List<Tab> tablist = new ArrayList<Tab>();
            for (int ixtab = 0; ixtab < tabs.getLength(); ixtab++) {
                Node tab = tabs.item(ixtab);
                if (tab.getNodeName().equals("tab")) {
                    String nameTab = attrvalue(tab.getAttributes(), "name", null);
                    NodeList params = tab.getChildNodes();

                    ArrayList<Param> paramarray = new ArrayList<Param>();
                    for (int ixpar = 0; ixpar < params.getLength(); ixpar++) {
                        Node par = params.item(ixpar);
                        if (par.getNodeName().equals("param")) {
                            NamedNodeMap attrs = par.getAttributes();
                            paramarray.add(new Param(attrvalue(attrs, "varname", null), attrvalue(attrs, "description", null), attrvalueBoolean(attrs, "usrparam", false)));
                        }
                    }
                    tablist.add(new Tab(nameTab, paramarray.toArray(new Param[paramarray.size()])));
                }
            }
            f.put(nameForm, tablist.toArray(new Tab[tablist.size()]));
        } catch (Exception e) {
        }
    }
    private static Hashtable<String, String> tstv = new Hashtable<String, String>();

    public Hashtable<String, String> getTstv() {
        return tstv;
    }

    public String action() {
        for (Entry<String, String> e : tstv.entrySet()) {
            System.out.println("action: " + e.getKey() + " -> " + e.getValue());
        }
        return null;
    }
}
