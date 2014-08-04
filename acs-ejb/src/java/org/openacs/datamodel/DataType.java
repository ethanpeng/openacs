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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataType implements Cloneable {

    private Type type = Type.OBJECT;
    private int maxLength = Integer.MAX_VALUE;
    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;
    private String units;
    private String name;
    private String description;
    private boolean list = false;
    List<String> patterns = new ArrayList<String>();
    Set<String> enumeration = new LinkedHashSet<String>();

    public boolean isList() {
        return list;
    }

    public void setList(Boolean list) {
        this.list = list;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public int getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public int getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPattern(String pattern) {
        patterns.add(pattern);
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public void addEnumeration(String e) {
        enumeration.add(e);
    }

    public Set<String> getEnumeration() {
        return enumeration;
    }

    @Override
    public String toString() {
        String en = "";
        for (String e : enumeration) {
            en += e + " ";
        }

        String pa = "";
        for (String p : patterns) {
            pa += p + " ";
        }
        return "DataType {" + "name=" + name + ", type=" + type + ", description=" + description + ", maxLength=" + maxLength + ", min=" + min + ", max=" + max + ", units=" + units + ", patterns=" + patterns + ", enumeration=" + enumeration + '}' + "\n" + en + "\n" + pa;
    }
    private static Map<String, DataType> types = new Hashtable<String, DataType>();

    public static void Add(DataType p) {
        types.put(p.getName(), p);
    }

    public static DataType[] getParameters() {
        return types.values().toArray(new DataType[types.size()]);

    }

    static DataType lookup(String name) {
        return types.get(name);
    }

    @Override
    public DataType clone() {
        try {
            return (DataType) super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    void merge(DataType t) {
        if (t != null) {
        }
    }
}