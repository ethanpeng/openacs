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

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

public abstract class PropertySet<T extends Property> {

    Hashtable<String, T> original = new Hashtable<String, T>();
    Hashtable<String, T> current = new Hashtable<String, T>();

    abstract protected void DeleteEntry(T p) throws RemoveException;

    abstract protected void SetEntry(T p) throws FinderException;

    abstract protected void InsertEntry(T p) throws CreateException;

    void Save() throws RemoveException, FinderException, CreateException {
        //System.out.println ("PropertySet::Save");
        for (Entry<String, T> e : original.entrySet()) {
            if (!current.containsKey(e.getKey())) {
                //System.out.println ("PropertySet::Save Delete "+e.getValue());
                DeleteEntry(e.getValue());
            }
        }
        for (Entry<String, T> e : current.entrySet()) {
            String key = e.getKey();
            if (original.containsKey(key)) {
                //Property o = original.get(key);
                //Property c = e.getValue();

                //System.out.println ("PropertySet::Save original "+o.getName()+"->"+o.getValue());
                //System.out.println ("PropertySet::Save current "+c.getName()+"->"+c.getValue());

                SetEntry(e.getValue());
            } else {
                //System.out.println ("PropertySet::Save insert "+e.getValue());
                InsertEntry(e.getValue());
            }
        }
        original.clear();
        original.putAll(current);
    }

    protected void Load() {
        current.putAll(original);
    }

    Map<String, T> getProperties() {
        return current;
    }

    public void Set(String name, String value) {
        Property p = current.get(name);
        if (p != null) {
            p.setValue(value);
        }
    }
    /*
    void Add (String name, String value) {
    Property p = current.get(name);
    if (p != null) {
    p.setValue(value);
    } else {
    Property v = new <T> (name, value);
    current.put(name, v);
    }
    }
     */

    public T Get(String name) {
        return current.get(name);
    }

    public void Add(T p) {
        current.put(p.getName(), p);
    }

    void Remove(String name) {
        current.remove(name);
    }

    public void clear() {
        current.clear();
        original.clear();
    }

    private void Dump(PrintStream out, Map<String, T> m, String name) {
        out.println(getClass().getName() + ": " + name);
        for (Entry<String, T> e : m.entrySet()) {
            out.println("\t" + e.getKey() + " -> " + e.getValue());
        }
    }

    public void Dump(PrintStream out) {
        Dump(out, current, "current");
        Dump(out, original, "original");
    }
}
