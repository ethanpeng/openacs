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
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Admin
 */
public class Parameter implements Cloneable {

    private String name;
    private Boolean readOnly;
    private Boolean hidden;
    private Type type = Type.UNDEFINED;
    private Integer minEntries;
    private Integer maxEntries;
    private String numEntriesParameter;
    private String ref;
    private String requirement;
    private String description;
    private String uniqueKey;
    private String defaultValue;
    private Integer maxLength;
    private Integer minLength = 0;
    private Integer min;
    private Integer max;
    private String units;
    private Boolean activeNotify;
    private String status = "current";
    private Boolean list = false;
    private Boolean command = false;
    private List<List<String>> keys = new ArrayList<List<String>>();
    private String model;
    private String typeName;
    private String enableParameter;
    List<String> patterns = new ArrayList<String>();
    Set<String> enumeration = new LinkedHashSet<String>();
    private static Map<String, Parameter> params = new Hashtable<String, Parameter>();
    static public final String ROOT_IGD = "InternetGatewayDevice.";
    static public final String ROOT_DEVICE = "Device.";
    protected static final String dateTimePattern = "^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}$";
    static public final long INFINITY_LOW = Long.MIN_VALUE;
    static public final long INFINITY_HIGH = Long.MAX_VALUE;

    public Boolean isCommand() {
        return command;
    }

    public void setCommand(Boolean command) {
        this.command = command;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isList() {
        return list;
    }

    public void setList(Boolean list) {
        this.list = list;
    }

    public String getEnableParameter() {
        return enableParameter;
    }

    public void setEnableParameter(String enableParameter) {
        this.enableParameter = enableParameter;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isActiveNotify() {
        return activeNotify;
    }

    public void setActiveNotify(boolean activeNotify) {
        this.activeNotify = activeNotify;
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

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxEntries() {
        return maxEntries;
    }

    public void setMaxEntries(Integer maxEntries) {
        this.maxEntries = maxEntries;
    }

    public int getMinEntries() {
        return minEntries;
    }

    public void setMinEntries(Integer minEntries) {
        this.minEntries = minEntries;
    }

    public String getNumEntriesParameter() {
        return numEntriesParameter;
    }

    public void setNumEntriesParameter(String numEntriesParameter) {
        this.numEntriesParameter = numEntriesParameter;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public boolean isHidden() {
        return hidden;
    }

    public String getType() {
        return type.toString();
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isWritable() {
        return !readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
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
        return "Parameter{" + "name=" + name + ", readOnly=" + readOnly + ", hidden=" + hidden + ", type=" + type + ", minEntries=" + minEntries + ", maxEntries=" + maxEntries + ", numEntriesParameter=" + numEntriesParameter + ", ref=" + ref + ", requirement=" + requirement + ", description=" + description + ", uniqueKey=" + uniqueKey + ", maxLength=" + maxLength + ", min=" + min + ", max=" + max + ", units=" + units + ", activeNotify=" + activeNotify + ", patterns=" + patterns + ", enumeration=" + enumeration + '}' + "\n" + en + "\n" + pa;
    }

    public static void Add(Parameter p) {
        Parameter param = params.get(p.getName());
        if (p.equals(param)) {
            return;
        }
        if (param != null) {
            param.merge(p);
        } else {
            params.put(p.getName(), p);
        }
    }

    public static Parameter[] getParameters() {
        return params.values().toArray(new Parameter[params.size()]);

    }

    public void appendKey() {
        keys.add(new ArrayList<String>());
    }

    public void addKeyField(String name) {
        keys.get(keys.size() - 1).add(name);
    }

    public static Parameter lookup(String name) {
        if (name == null) {
            return null;
        }
        name = name.replaceAll("\\.[0-9]+\\.", ".{i}.");
        return params.get(name);
    }

    @Override
    public Parameter clone() {
        try {
            Parameter param = (Parameter) super.clone();
            param.keys = new ArrayList<List<String>>();
            for (List<String> l : keys) {
                param.keys.add(l);
            }

            param.patterns = new ArrayList<String>();
            for (String p : patterns) {
                param.patterns.add(p);
            }

            param.enumeration = new LinkedHashSet<String>();
            for (String e : enumeration) {
                param.enumeration.add(e);
            }
            return param;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    void setType(DataType t) {
        this.type = t.getType();
        this.max = t.getMax();
        this.min = t.getMin();
        this.patterns = t.getPatterns();
        this.list = t.isList();
        this.enumeration = t.getEnumeration();
        this.units = t.getUnits();
    }

    private void merge(Parameter p) {
        if (p.readOnly != null) {
            this.readOnly = p.readOnly;
        }
        if (p.hidden != null) {
            this.hidden = p.hidden;
        }
        if (p.type != Type.UNDEFINED) {
            this.type = p.type;
        }
        if (p.minEntries != null) {
            this.minEntries = p.minEntries;
        }
        if (p.maxEntries != null) {
            this.maxEntries = p.maxEntries;
        }
        if (p.numEntriesParameter != null) {
            this.numEntriesParameter = p.numEntriesParameter;
        }
        if (p.ref != null) {
            this.ref = p.ref;
        }
        if (p.requirement != null) {
            this.requirement = p.requirement;
        }
        if (p.description != null) {
            this.description = p.description;
        }
        if (p.uniqueKey != null) {
            this.uniqueKey = p.uniqueKey;
        }
        if (p.maxLength != null) {
            this.maxLength = p.maxLength;
        }
        if (p.min != null) {
            this.min = p.min;
        }
        if (p.max != null) {
            this.max = p.max;
        }
        if (p.units != null) {
            this.units = p.units;
        }
        if (p.activeNotify != null) {
            this.activeNotify = p.activeNotify;
        }
        if (p.list != null) {
            this.list = p.list;
        }
        if (!p.keys.isEmpty()) {
            for (List<String> l : p.keys) {
                keys.add(l);
            }
        }
        if (p.model != null) {
            this.model = p.model;
        }
        if (p.typeName != null) {
            this.typeName = p.typeName;
        }
        for (String pat : p.patterns) {
            patterns.add(pat);
        }
        for (String en : p.enumeration) {
            enumeration.add(en);
        }
    }

    public static String getNormalizedName(String name) {
        String root = Parameter.getRootName(name);
        String r = name.substring(root.length()).replaceAll("\\.[0-9]+\\.", ".{i}.");
        //System.out.println("DataModel::getNormalizedName: " + name + "->" + r);
        return r;
    }

    public static String getNormalizedName(String root, String name) {
        String r = root + "." + getNormalizedName(name);
        //System.out.println("DataModel::getNormalizedName: (root=" + root + ") " + name + "->" + r);
        return r;
    }

    public static String getNameWithoutRoot(String name) {
        String root = Parameter.getRootName(name);
        String r = name.substring(root.length());
        return r;
    }

    public static String getRootName(String name) {
        if (name == null) {
            return "";
        }
        if (name.startsWith(ROOT_IGD)) {
            return ROOT_IGD;
        } else if (name.startsWith(ROOT_DEVICE)) {
            return ROOT_DEVICE;
        } else if (name.startsWith(".")) {
            return ".";
        }
        return "";
    }
    public static final int VALIDATION_UNKNOWN = -1;
    public static final int VALIDATION_OK = 0;
    public static final int VALIDATION_READONLY = 1;
    public static final int VALIDATION_TOOLONG = 2;
    public static final int VALIDATION_BADBOOLEAN = 3;
    public static final int VALIDATION_BADDATE = 4;
    public static final int VALIDATION_BADINT = 5;
    public static final int VALIDATION_TOOBIG = 6;
    public static final int VALIDATION_TOOSMALL = 7;

    public int Validate(String value) {
        //System.out.println("DataModel::Validate name=" + name + " value=" + value);
        if (readOnly) {
            return VALIDATION_READONLY;
        }
        if (type == Type.STRING) {
            if (maxLength < value.length()) {
                return VALIDATION_TOOLONG;
            }
        } else if (type == Type.BOOLEAN) {
            if (!value.equals("0") && !value.equals("1")) {
                return VALIDATION_BADBOOLEAN;
            }
        } else if (type == Type.DATETIME) {
            if (!value.matches(dateTimePattern)) {
                return VALIDATION_BADDATE;
            }
        } else if (type == Type.UNSIGNEDINT || type == Type.INT) {
            long v;
            try {
                v = Long.parseLong(value);
            } catch (NumberFormatException e) {
                return VALIDATION_BADINT;
            }
            if (v < min) {
                return VALIDATION_TOOSMALL;
            }
            if (v > max) {
                return VALIDATION_TOOBIG;
            }
        } else if (type == Type.BASE64) {
            if (maxLength < value.length()) {
                return VALIDATION_TOOLONG;
            }
        }
        return VALIDATION_OK;
    }

    public static void dump() {
        List<String> l = new ArrayList<String>();
        l.addAll(params.keySet());
        Collections.sort(l);
        for (String n : l) {
            System.out.println(n + " " + params.get(n).getDescription());
        }
    }
}
