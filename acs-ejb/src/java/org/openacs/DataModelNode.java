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
package org.openacs;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataModelNode {

    private static final String instanceRemoveAll = "0";
    private static final String instanceNoCreateDelete = "-2";
    private HashMap<String, DataModelNode> children = null;
    private String value = null;
    private String name = null;
    private Boolean multiInstance = null;
    private boolean mapped = false;
    private DataModelNode parent = null;

    public DataModelNode(DataModelNode parent) {
        this.parent = parent;
    }

    public DataModelNode(DataModelNode parent, String name) {
        this.parent = parent;
        this.name = name;
        //multiInstance = isNameInstance(name);
    }

    public DataModelNode(DataModelNode parent, String name, boolean f) {
        this.parent = parent;
        this.name = name;
        children = new HashMap<String, DataModelNode>();
    }

    public DataModelNode(DataModelNode parent, String name, String value) {
        this.parent = parent;
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public DataModelNode Clone(DataModelNode n) {
        DataModelNode nn = new DataModelNode(this);
        nn.name = n.name;
        nn.value = n.value;
        return n;
    }

    public DataModelNode Clone(DataModelNode n, String name) {
        DataModelNode nn = Clone(n);
        nn.name = name;
        return n;
    }

    public Collection<DataModelNode> getChildren() {
        if (children != null) {
            return children.values();
        } else {
            return new ArrayList<DataModelNode>();
        }
    }

    private void getFQName(StringBuilder s) {
        if (parent != null) {
            parent.getFQName(s);
            s.append(".");
        }
        s.append(name);
    }

    public String getFQName() {
        StringBuilder s = new StringBuilder(128);
        getFQName(s);
        return s.toString();
    }

    private void getFQNameCanonic(StringBuilder s) {
        if (parent != null) {
            parent.getFQNameCanonic(s);
            s.append(".");
            if (parent.isMultiInstance()) {
                s.append("{i}");
                return;
            }
        }
        s.append(name);
    }

    public String getFQNameCanonic() {
        StringBuilder s = new StringBuilder(128);
        getFQNameCanonic(s);
        return s.toString();
    }

    public String getValue() {
        return value;
        /*
        if (isLeaf()) {
        return (String) value;
        }
        return null;
         */
    }

    public boolean isLeaf() {
        return (children == null);
    }

    public void renameChild(String newname) {
        if (parent != null) {
            parent.children.remove(name);
            parent.children.put(newname, this);
        }
        name = newname;
    }

    public void renameChild(String name, String newname) {
        DataModelNode c = children.get(name);
        c.renameChild(newname);
    }

    public void renameChild(int name, int newname) {
        DataModelNode c = children.get(String.valueOf(name));
        c.renameChild(String.valueOf(newname));
    }

    public DataModelNode findMultiFirst() {
        for (DataModelNode c : children.values()) {
            if (!c.mapped && c.isMultiInstance()) {
                return c;
            }
            DataModelNode cr = c.findMultiFirst();
            if (cr != null) {
                return cr;
            }
        }
        return null;
    }

    public void setMapped() {
        mapped = true;
    }

    public boolean isMultiInstance() {
        if (multiInstance != null) {
            return multiInstance;
        }
        if (children != null) {
            Iterator<Entry<String, DataModelNode>> i = children.entrySet().iterator();
            while (i.hasNext()) {
                Entry<String, DataModelNode> e = i.next();
                if (!e.getValue().isNameInstance()) {
                    return (multiInstance = false);
                }
            }
            return (multiInstance = true);
        }
        return (multiInstance = false);
    }

    public void getUpdateList(ArrayList<DataModelNode> l, ArrayList<DataModelNode> v) {
        l.clear();
        v.clear();
        getUpdateListInternal(l, v);
    }

    private void getUpdateListInternal(ArrayList<DataModelNode> l, ArrayList<DataModelNode> v) {
        if (isMultiInstance()) {
            l.add(this);
            return;
        }
        if (value != null) {
            v.add(this);
        }
        if (children == null) {
            return;
        }
        for (DataModelNode c : children.values()) {
            c.getUpdateListInternal(l, v);
        }
    }
    /*
    public ArrayList<DataModelNode> getMultiInstanceList () {
    ArrayList<DataModelNode> l = new ArrayList<DataModelNode>();
    getMultiInstanceList(l);
    return l;
    }
     */

    private DataModelNode getValue(String[] path, int i) {
        if (i == path.length) {
            return this;
        }
        DataModelNode c = children.get(path[i]);
        if (c == null) {
            return null;
        }
        return c.getValue(path, i + 1);
    }

    public DataModelNode getValue(String path) {
        String pe[] = path.split("\\.");
        if (pe.length == 0) {
            return null;
        }
        if (!pe[0].equals(name)) {
            return null;
        }
        if (pe.length == 1) {
            return this;
        }
        return getValue(pe, 1);
    }

    public Collection getValues(String path) {
        DataModelNode c = getValue(path);
        if (c != null) {
            if (c.children != null) {
                return children.values();
            }
        }
        /*
        if (c.value != null) {
        if (c.value instanceof HashMap) {
        return ((HashMap) c.value).values();
        } else {
        return null;
        }
        }
         */
        return null;
    }

    public void load(InputStream in) {
        load(in, false);
    }

    public void load(InputStream in, boolean fValuesAreBoolean) {
        try {
            Properties p = new Properties();
            p.load(in);
            load(p, fValuesAreBoolean);
        } catch (Exception ex) {
            Logger.getLogger(DataModelNode.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(DataModelNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void load(Properties p, boolean fValuesAreBoolean) {
        load(p, fValuesAreBoolean, true);
    }

    public void load(Properties p, boolean fValuesAreBoolean, boolean fAllowReplace) {
        int lineNumber = 0;
        Iterator params = p.entrySet().iterator();
        while (params.hasNext()) {
            lineNumber++;
            Entry param = (Entry) params.next();
            String k = (String) param.getKey();
            //if (k.endsWith(".")) {                    k = k.substring(0, k.length() - 1);                }
            String v = (String) param.getValue();
            if (fValuesAreBoolean) {
                if (v.equalsIgnoreCase("true")) {
                    v = "1";
                } else if (v.equalsIgnoreCase("false")) {
                    v = "0";
                }
            }
            String pe[] = k.split("\\.");
            if (name == null || name.equals("")) {
                name = pe[0];
            } else if (!name.equals(pe[0])) {
                Logger.getLogger(DataModelNode.class.getName()).log(Level.SEVERE, "More than one root found: " + name + "!=" + pe[0] + " at line " + lineNumber);
            }
            Add(pe, 1, v, fAllowReplace);
        }
    }

    private HashMap<String, DataModelNode> getSubobjects() {
        if ((value != null)) {
            //Logger.getLogger(cfg.class.getName()).log(Level.WARNING, "Bad2 at " + name);
        }
        if (children == null) {
            children = new HashMap<String, DataModelNode>();
        }
        return children;
    }
    private static final String instanceWildcard = "-1";

    public DataModelNode getRoot() {
        DataModelNode root = this;
        while (root.parent != null) {
            root = root.parent;
        }
        return root;
    }

    public boolean ExpandWildcardInstance(int[] cpenames) {
        if (children == null) {
            return false;
        }
        DataModelNode wildcard = children.get(instanceWildcard);
        if (wildcard == null) {
            return false;
        }

        String pfx = wildcard.parent.getFQName() + ".";
        children.remove(instanceWildcard);
        wildcard.parent = null;
        pfx = name + ".";

        boolean result = false;
        if (wildcard != null) {
            Properties p = new Properties();
            if (children.size() == 0) {
                result = true;
                for (int n : cpenames) {
                    wildcard.toProperties(pfx + n, p);
                }
            } else {
                for (String n : children.keySet()) {
                    //Add(Clone(wildcard, n));
                    wildcard.toProperties(pfx + n, p);
                }
            }
            load(p, false, false);
        }
        return result;
    }

    private boolean isNameInstance() {
        return isNameInstance(name);
    }

    private boolean isNameInstance(String name) {
        boolean mi = true;
        try {
            Integer.parseInt(name);
        } catch (Exception e) {
            mi = false;
        }
        return mi;
    }

    private void Add(String[] pe, int i, String v, boolean fAllowReplace) {
        if (i >= pe.length) {
            StringBuffer pes = new StringBuffer(256);
            for (String pee : pe) {
                pes.append(pee);
                pes.append(".");
            }
//            System.out.print("cfg.Add pe=" + pes + " l=" + pe.length + " i=" + i + " v=" + v);
            return;
        }

        String _name = pe[i];
        if (i == pe.length - 1) { //It is value
            DataModelNode vv = getSubobjects().get(_name);
            if (vv == null) {
                getSubobjects().put(_name, new DataModelNode(this, _name, v));
            } else {
                if (fAllowReplace) {
                    vv.value = v;
                }
            }
        } else {
            if (_name.equals("*")) {
                _name = instanceWildcard;
            }
            HashMap<String, DataModelNode> objs = getSubobjects();
            DataModelNode c = objs.get(_name);
            if (c == null) {
                c = new DataModelNode(this, _name);
                //multiInstance = isNameInstance(_name);
            }
            getSubobjects().put(_name, c);
            c.Add(pe, i + 1, v, fAllowReplace);
        }

    }

    private void Add(DataModelNode node) {
        Add(node, node.name);
    }

    private void Add(DataModelNode node, String _name) {
        node.parent = this;
        if (node.children == null) { //It is value
            DataModelNode vv = getSubobjects().get(_name);
            if (vv == null) {
                getSubobjects().put(_name, node);
            } else {
                vv.value = node.value;
            }
        } else {
            HashMap<String, DataModelNode> objs = getSubobjects();
            DataModelNode c = objs.get(_name);
            if (c == null) {
                c = new DataModelNode(this, _name);
                //multiInstance = isNameInstance(_name);
            }
            getSubobjects().put(_name, c);
            for (DataModelNode n : node.children.values()) {
                c.Add(Clone(n));
            }
        }
    }

    public void print(String pfx, PrintStream p) {
        if (value != null) {
            p.println(pfx + "=" + value);
        }

        Set<Entry<String, DataModelNode>> s = getSubobjects().entrySet();
        Iterator<Entry<String, DataModelNode>> i = s.iterator();
        while (i.hasNext()) {
            Entry<String, DataModelNode> e = i.next();
            DataModelNode c = e.getValue();
            c.print(pfx + "." + c.name, p);
        }
    }

    protected void toProperties(String pfx, Properties p) {
        if (value != null) {
            p.setProperty(pfx, value);
        }

        Set<Entry<String, DataModelNode>> s = getSubobjects().entrySet();
        Iterator<Entry<String, DataModelNode>> i = s.iterator();
        while (i.hasNext()) {
            Entry<String, DataModelNode> e = i.next();
            DataModelNode c = e.getValue();
            c.toProperties(pfx + "." + c.name, p);
        }
    }

    public Properties toProperties(String pfx) {
        Properties p = new Properties();
        toProperties(pfx, p);
        return p;
    }

    @Override
    public String toString() {
        if (isLeaf()) {
            return name + "=" + value;
        } else {
            return name + " -> Object";
        }
    }
    private String INSTANCE = "Instance";

    public String[] getParamNames() {
        if (!isMultiInstance()) {
            String rn[] = new String[2];
            rn[0] = "Name";
            rn[1] = "Value";
            return rn;
        }

        HashMap<String, String> n = new HashMap<String, String>();
        n.put(INSTANCE, "");
        Iterator<Entry<String, DataModelNode>> i = children.entrySet().iterator();
        while (i.hasNext()) {
            Entry<String, DataModelNode> e = i.next();
            Iterator<Entry<String, DataModelNode>> i2 = e.getValue().children.entrySet().iterator();
            while (i2.hasNext()) {
                Entry<String, DataModelNode> e2 = i2.next();
                n.put(e2.getValue().getName(), "");
            }
        }
        String[] ns = n.keySet().toArray(new String[0]);
        Arrays.sort(ns);
        // Bring instance into begining of array
        for (int ix = 0; ix < ns.length; ix++) {
            if (ns[ix].equals(INSTANCE)) {
                String t = ns[0];
                ns[0] = ns[ix];
                ns[ix] = t;
                break;
            }
        }
        return ns;
    }

    public Object[][] getParamValues(String[] names) {
        //String [][] v = new String [names.length][];
        Object[][] v = new Object[children.entrySet().size()][];
//        Iterator<Entry<String,DataModelNode>> i = ((HashMap<String, DataModelNode>)value).entrySet().iterator();
        int vx = 0;
        Set<Entry<String, DataModelNode>> s = children.entrySet();
        Object[] a = s.toArray();

        Arrays.sort(a, new Comparator() {

            public int compare(Object o1, Object o2) {
                Entry<String, DataModelNode> e1 = (Entry<String, DataModelNode>) o1;
                Entry<String, DataModelNode> e2 = (Entry<String, DataModelNode>) o2;
                String s1 = e1.getKey();
                String s2 = e2.getKey();
                try {
                    int i1 = Integer.parseInt(s1);
                    int i2 = Integer.parseInt(s2);
                    return i1 - i2;
                } catch (NumberFormatException e) {
                }
                return s1.compareTo(s2);
            }
        });

        if (!isMultiInstance()) {
            /*
            while (i.hasNext()) {
            cfg c = i.next().getValue();
            Object values[] = new Object [2];
            values [0] = c; //.getName();
            values [1] = c.getValue();
            v [vx++] = values;
            }
             */
            for (Object eo : a) {
                Entry<String, DataModelNode> e = (Entry<String, DataModelNode>) eo;
                DataModelNode c = e.getValue();
                Object values[] = new Object[2];
                values[0] = c; //.getName();
                values[1] = c.getValue();
//                if (values[1] != null && values[1] instanceof String) values[1] = ((String)values[1]).replaceAll("\n", "<br/>");
                //System.out.println ("getParamValues: v="+v+" vx="+vx+" values="+values);
                v[vx++] = values;
            }
            return v;
        }
        /*
        Iterator<Entry<String,DataModelNode>> i = ((HashMap<String, DataModelNode>)value).entrySet().iterator();
        while (i.hasNext()) {
        cfg c = i.next().getValue();
        int nx = 0;
        Object values[] = new Object [names.length];
        for (String n : names) {
        if (nx == 0) {
        values [nx++] = c; //.getName();
        } else {
        values [nx++] = ((HashMap<String, DataModelNode>)c.value).get(n).getValue();
        }
        }
        v [vx++] = values;
        }
         */
        for (Object eo : a) {
            Entry<String, DataModelNode> e = (Entry<String, DataModelNode>) eo;
            DataModelNode c = e.getValue();
            int nx = 0;
            Object values[] = new Object[names.length];
            for (String n : names) {
                if (nx == 0) {
                    values[nx++] = c; //.getName();
                } else {
                    DataModelNode cv = c.children.get(n);
                    if (cv == null) {
                        values[nx++] = "-";
                    } else {
                        //System.out.println ("getParamValues: getValue="+((HashMap<String, DataModelNode>) c.value).get(n).getValue());
                        values[nx++] = c.children.get(n).getValue();
                    }
                }
            }
            v[vx++] = values;
        }
        return v;
    }

    public String[][] getParamValues_(String[] names) {
        String[][] v = new String[names.length][];
        int nx = 0;
        for (String n : names) {
            String values[] = new String[children.entrySet().size()];
            int vx = 0;
            Iterator<Entry<String, DataModelNode>> i = children.entrySet().iterator();
            while (i.hasNext()) {
                DataModelNode c = i.next().getValue();
                if (nx == 0) {
                    values[vx++] = c.getName();
                } else {
                    values[vx++] = c.children.get(n).getValue();
                }
            }
            v[nx++] = values;
        }
        return v;
    }

    public DataModelNode getParent() {
        return parent;
    }

    public boolean isInstance() {
        try {
            Integer.parseInt(name);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int[] getInstanceNames() {
        if (isMultiInstance()) {
            if (!children.containsKey(instanceRemoveAll)) {
                int[] n = new int[children.size()];
                int ix = 0;
                for (String sn : children.keySet()) {
                    Integer v = Integer.parseInt(sn);
                    if (v >= 0) {
                        n[ix++] = v;
                    }
                }
                Arrays.sort(n);
                return n;
            }
        }
        return new int[0];
    }

    public boolean DoNotCreateDelete() {
        if (isMultiInstance()) {
            return children.containsKey(instanceNoCreateDelete);
        }
        return true;
    }
}
