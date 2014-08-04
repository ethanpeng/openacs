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

import java.io.IOException;
import java.io.InputStream;
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

public class cfg {

    private Object value = null;
    private String name = null;
    private boolean multiInstance = false;

    public cfg() {
    }

    public cfg(String name) {
        this.name = name;
        multiInstance = isNameInstance(name);
    }

    public cfg(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        if (isLeaf()) {
            return (String) value;
        }
        return null;
    }

    public boolean isLeaf() {
        return (value == null || value instanceof String);
    }

    public boolean isMultiInstance() {
        if (value != null && value instanceof HashMap) {
            Iterator<Entry<String, cfg>> i = ((HashMap<String, cfg>) value).entrySet().iterator();
            while (i.hasNext()) {
                Entry<String, cfg> e = i.next();
                if (!((cfg) e.getValue()).isNameInstance()) {
                    return false;
                }
            }
            /*
            Iterator<Entry<String,Object>> i = ((HashMap<String, Object>)value).entrySet().iterator();
            while (i.hasNext()) {
            Entry<String,Object> e = i.next();
            if (!(e.getValue() instanceof cfg)|| !((cfg)e.getValue()).isNameInstance()) {
            return false;
            }
            }
             */
        }
        return true;
    }

    private cfg getValue(String[] path, int i) {
        if (i == path.length) {
            return this;
        }
        HashMap<String, cfg> m = (HashMap<String, cfg>) value;
        cfg c = m.get(path[i]);
        if (c == null) {
            return null;
        }
        return c.getValue(path, i + 1);
    }

    public cfg getValue(String path) {
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
        cfg c = getValue(path);
        if (c.value != null) {
            if (c.value instanceof HashMap) {
                return ((HashMap) c.value).values();
            } else {
                return null;
            }
        }
        return null;
    }

    public void load(InputStream in) {
        try {
            Properties p = new Properties();
            p.load(in);
            Iterator params = p.entrySet().iterator();
            while (params.hasNext()) {
                Entry param = (Entry) params.next();
                String k = (String) param.getKey();
                String v = (String) param.getValue();
                String pe[] = k.split("\\.");
                if (name == null || name.equals("")) {
                    name = pe[0];
                } else if (!name.equals(pe[0])) {
                    Logger.getLogger(cfg.class.getName()).log(Level.SEVERE, "More than one root found: " + name + "!=" + pe[0]);
                }
                Add(pe, 1, v);

                //Add (pe, 0, v);
            }
        } catch (Exception ex) {
            Logger.getLogger(cfg.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(cfg.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private HashMap<String, cfg> getSubobjects() {
        if ((value != null) && !(value instanceof HashMap)) {
            Logger.getLogger(cfg.class.getName()).log(Level.WARNING, "Bad2 at " + name);
        }

        if ((value == null) || (value instanceof String)) {
            value = new HashMap<String, cfg>();
        }
        return (HashMap<String, cfg>) value;
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

    private void Add(String[] pe, int i, String v) {
        if (i >= pe.length) {
            StringBuffer pes = new StringBuffer(256);
            for (String pee : pe) {
                pes.append(pee);
                pes.append(".");
            }
//            System.out.print ("cfg.Add pe="+pes+" l="+pe.length+" i="+i+" v="+v);
            return;
        }

        String _name = pe[i];
        if (i == pe.length - 1) { //It is value
            getSubobjects().put(_name, new cfg(_name, v));
        } else {
            HashMap<String, cfg> objs = getSubobjects();
            Object co = (cfg) objs.get(_name);
            cfg c;
            if (co == null || !(co instanceof cfg)) {
                if (co != null) {
                    Logger.getLogger(cfg.class.getName()).log(Level.WARNING, "Object&value with same name at " + name);
                }
                c = new cfg(_name);
                multiInstance = isNameInstance(_name);
            } else {
                c = (cfg) co;
            }
            getSubobjects().put(_name, c);
            c.Add(pe, i + 1, v);
        }

    }

    void print(String pfx) {
        Set<Entry<String, cfg>> s = getSubobjects().entrySet();
        Iterator<Entry<String, cfg>> i = s.iterator();
        while (i.hasNext()) {
            Entry<String, cfg> e = i.next();
            if (e.getValue() instanceof cfg) {
                cfg c = (cfg) e.getValue();
                if (c.value instanceof String) {
                    System.out.println(pfx + "." + c.name + "=" + c.value);
                } else {
                    //System.out.print (pfx+"."+c.name);
                    c.print(pfx + "." + c.name);
                }
                //System.out.println ("cfg: "+c.name+"="+c.value);
            } else {
                System.out.println(e.getKey() + "=" + e.getValue());
            }
        }

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
        Iterator<Entry<String, cfg>> i = ((HashMap<String, cfg>) value).entrySet().iterator();
        while (i.hasNext()) {
            Entry<String, cfg> e = i.next();
            Iterator<Entry<String, cfg>> i2 = ((HashMap<String, cfg>) e.getValue().value).entrySet().iterator();
            while (i2.hasNext()) {
                Entry<String, cfg> e2 = i2.next();
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
        Object[][] v = new Object[((HashMap<String, cfg>) value).entrySet().size()][];
//        Iterator<Entry<String,cfg>> i = ((HashMap<String, cfg>)value).entrySet().iterator();
        int vx = 0;
        Set<Entry<String, cfg>> s = ((HashMap<String, cfg>) value).entrySet();
        Object[] a = s.toArray();
        Arrays.sort(a, new comparator());
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
                Entry<String, cfg> e = (Entry<String, cfg>) eo;
                cfg c = e.getValue();
                Object values[] = new Object[2];
                values[0] = c; //.getName();
                values[1] = c.getValue();
                //System.out.println ("getParamValues: v="+v+" vx="+vx+" values="+values);
                v[vx++] = values;
            }
            return v;
        }
        /*
        Iterator<Entry<String,cfg>> i = ((HashMap<String, cfg>)value).entrySet().iterator();
        while (i.hasNext()) {
        cfg c = i.next().getValue();
        int nx = 0;
        Object values[] = new Object [names.length];
        for (String n : names) {
        if (nx == 0) {
        values [nx++] = c; //.getName();
        } else {
        values [nx++] = ((HashMap<String, cfg>)c.value).get(n).getValue();
        }
        }
        v [vx++] = values;
        }
         */
        for (Object eo : a) {
            Entry<String, cfg> e = (Entry<String, cfg>) eo;
            cfg c = e.getValue();
            int nx = 0;
            Object values[] = new Object[names.length];
            for (String n : names) {
                if (nx == 0) {
                    values[nx++] = c; //.getName();
                } else {
                    cfg cv = ((HashMap<String, cfg>) c.value).get(n);
                    if (cv == null) {
                        values[nx++] = "-";
                    } else {
                        //System.out.println ("getParamValues: getValue="+((HashMap<String, cfg>) c.value).get(n).getValue());
                        values[nx++] = ((HashMap<String, cfg>) c.value).get(n).getValue();
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
            String values[] = new String[((HashMap<String, cfg>) value).entrySet().size()];
            int vx = 0;
            Iterator<Entry<String, cfg>> i = ((HashMap<String, cfg>) value).entrySet().iterator();
            while (i.hasNext()) {
                cfg c = i.next().getValue();
                if (nx == 0) {
                    values[vx++] = c.getName();
                } else {
                    values[vx++] = ((HashMap<String, cfg>) c.value).get(n).getValue();
                }
            }
            v[nx++] = values;
        }
        return v;
    }

    private class comparator implements Comparator {

        public int compare(Object o1, Object o2) {
            Entry<String, cfg> e1 = (Entry<String, cfg>) o1;
            Entry<String, cfg> e2 = (Entry<String, cfg>) o2;
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
    }
}
