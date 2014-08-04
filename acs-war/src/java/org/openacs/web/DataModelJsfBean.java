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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.FinderException;
import javax.faces.model.SelectItem;
import org.openacs.datamodel.Parameter;
import org.openacs.DataModelLocal;
import org.openacs.DataModelLocalHome;
import org.openacs.DataModelNode;
import org.openacs.utils.Ejb;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNodeImpl;
import org.richfaces.model.TreeRowKey;

public class DataModelJsfBean implements Map {

    private String lastPath = null;

    public class DataModelItem {

        private String defaultvalue;
        private String description;
        private Integer length;
        private long max;
        private long min;
        private String name;
        private String trname;
        private String type;
        private String version;
        private Boolean writable;

        public String getDefaultvalue() {
            return defaultvalue;
        }

        public String getDescription() {
            return description;
        }

        public String getLength() {
            return (length != Integer.MAX_VALUE) ? String.valueOf(length) : "";
        }

        public String getMax() {
            return (max != Long.MAX_VALUE) ? String.valueOf(max) : "";
        }

        public String getMin() {
            return (min != Long.MIN_VALUE) ? String.valueOf(min) : "";

        }

        public String getName() {
            return name;
        }

        public String getTrname() {
            return trname;
        }

        public String getType() {
            return type;
        }

        public String getVersion() {
            return version;
        }

        public String getWritable() {
            return (writable != null) ? writable.toString() : "unknown";
        }

        protected void setValues(DataModelLocal dm) {
            if (dm != null) {
                defaultvalue = dm.getDefaultvalue();
                if (defaultvalue.equals("-")) {
                    defaultvalue = "";
                }
                description = new String(dm.getDescription());
                length = dm.getLength();
                max = dm.getMax();
                min = dm.getMin();
                name = dm.getName();
                trname = dm.getTrname();
                type = dm.getType();
                version = dm.getVersion();
                writable = dm.getWritable();
            } else {
                defaultvalue = "";
                description = "Variable is unknown";
                length = 0;
                max = 0;
                min = 0;
                name = "";
                trname = "";
                type = "";
                version = "";
                writable = null;

            }
        }
    }
    protected static final String dateTimePattern = "^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}$";
    private DataModelItem item = new DataModelItem();

    public int size() {
        System.out.println("At size ()");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isEmpty() {
        System.out.println("At isEmpty ()");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean containsKey(Object key) {
        System.out.println("At containsKey ()" + key);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean containsValue(Object value) {
        System.out.println("At containsValue ()" + value);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object get(Object key) {
        StringBuilder path = new StringBuilder(128);
        System.out.println("DataModelJsfBean GET: key=" + key);
        try {
            String k = (String) key;
            String methodname = "get" + k.substring(0, 1).toUpperCase() + k.substring(1);
            System.out.println("Getting method " + methodname);
            Method get = this.getClass().getMethod(methodname);
            System.out.println("Got it");
            return get.invoke(this);
        } catch (Exception ex) {
        }
        /*
        if (key.equals("objectNames")) {
        return getObjectNames();
        }
        if (key.equals("paramNames")) {
        return getParamNames();
        }
         */
        DataModelNode p = (DataModelNode) key;
        path.insert(0, p.getName());
        path.insert(0, ".");
        while ((p = p.getParent()) != null) {
            if (p.getParent() == null) {
                break;
            }
            if (p.isInstance()) {
                path.insert(0, ".{i}");
            } else {
                path.insert(0, p.getName());
                path.insert(0, ".");
            }
        }
        String ps = path.toString();
        //System.out.println("DataModelJsfBean GET: " + ps);
        if (!ps.equals(lastPath)) {
            lastPath = ps;
            //System.out.println("DataModelJsfBean LOOKUP: " + ps);
            DataModelLocalHome dmh = Ejb.lookupDataModelBean();
            DataModelLocal dm = null;
            try {
                dm = dmh.findByName(lastPath);
            } catch (FinderException ex) {
            }
            item.setValues(dm);
        }
        return item;
    }

    public Object put(Object key, Object value) {
        System.out.println("At put ()" + key + "->" + value);
        if (key.equals("selectedParamNames")) {
            System.out.println("value " + value.getClass().getName());
            try {
                if (value instanceof String) {
                    List<String> l = new ArrayList<String>();
                    l.add((String) value);
                } else {
                    setSelectedParamNames((List<String>) value);
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e);
            }
            return value;
        }
        try {
            String k = (String) key;
            String methodname = "set" + k.substring(0, 1).toUpperCase() + k.substring(1);
            System.out.println("Getting method " + methodname);
            Method get = this.getClass().getMethod(methodname, value.getClass());
            System.out.println("Got it");
            get.invoke(this, value);
            return null;
        } catch (Exception ex) {
            System.out.println("Exception " + ex);
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object remove(Object key) {
        System.out.println("At remove ()" + key);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void putAll(Map t) {
        System.out.println("At putAll ()");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clear() {
        System.out.println("At clear ()");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set keySet() {
        System.out.println("At keySet ()");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection values() {
        System.out.println("At values ()");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set entrySet() {
        System.out.println("At entrySet ()");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** Creates a new instance of DataModelJsfBean */
    public DataModelJsfBean() {
    }

    public static DataModelValidationResult Validate(String name, String value) {
        Parameter dm = Parameter.lookup(name);
        if (dm == null) {
            return new DataModelValidationResult(name + " is not known", true);
        }
        //System.out.println("DataModelJsfBean::Validate name=" + name + " value=" + value);
        switch (dm.Validate(value)) {
            case Parameter.VALIDATION_READONLY:
                return new DataModelValidationResult("Value is read only.");
            case Parameter.VALIDATION_TOOLONG:
                return new DataModelValidationResult("Value too long. Max length is " + dm.getMaxLength() + " and current is " + value.length());
            case Parameter.VALIDATION_BADBOOLEAN:
                return new DataModelValidationResult("Value must be '0' or '1'");
            case Parameter.VALIDATION_BADDATE:
                return new DataModelValidationResult("Value is not in valid dateTime format: yyyy-mm-ddThh:mm:ss");
            case Parameter.VALIDATION_BADINT:
                return new DataModelValidationResult("Value must be valid integer");
            case Parameter.VALIDATION_TOOSMALL:
                return new DataModelValidationResult("Value must be greater than " + dm.getMin());
            case Parameter.VALIDATION_TOOBIG:
                return new DataModelValidationResult("Value must be less than " + dm.getMax());
        }
        return new DataModelValidationResult();
    }
    /*
    public static DataModelValidationResult Validate(String name, String value) {
    DataModelLocalHome dmh = Ejb.lookupDataModelBean();
    DataModelLocal dm = null;
    try {
    dm = dmh.lookupByName(name);
    System.out.println("DataModelJsfBean::Validate name=" + name + " value=" + value);
    String t = dm.getType();
    if (!dm.getWritable()) {
    return new DataModelValidationResult("Value is read only.");
    }
    if (t.equals("string")) {
    if (dm.getLength() < value.length()) {
    return new DataModelValidationResult("Value too long. Max length is " + dm.getLength() + " and current is " + value.length());
    }
    } else if (t.equals("boolean")) {
    if (!value.equals("0") && !value.equals("1")) {
    return new DataModelValidationResult("Value must be '0' or '1'");
    }
    } else if (t.equals("dateTime")) {
    if (!value.matches(dateTimePattern)) {
    return new DataModelValidationResult("Value is not in valid dateTime format: yyyy-mm-ddThh:mm:ss");
    }
    } else if (t.equals("unsignedInt") || t.equals("int")) {
    long v;
    try {
    v = Long.parseLong(value);
    } catch (NumberFormatException e) {
    return new DataModelValidationResult("Value must be valid integer");
    }
    if (v < dm.getMin()) {
    return new DataModelValidationResult("Value must be greater than " + dm.getMin());
    }
    if (v > dm.getMax()) {
    return new DataModelValidationResult("Value must be less than " + dm.getMax());
    }
    } else if (t.equals("base64")) {
    if (dm.getLength() < value.length()) {
    return new DataModelValidationResult("Value too long. Max length is " + dm.getLength() + " and current is " + value.length());
    }
    }
    } catch (FinderException ex) {
    return new DataModelValidationResult(name + " is not known", true);
    }
    return new DataModelValidationResult();
    }
     */

    public static Collection autocompletePropName(Object v) {
        return Ejb.lookupDataModelBean().getChildNames((String) v, true);
    }

    public int buildObjectNamesSubtree(String[] names, String prefix, int ix, TreeNodeImpl<String> root) {
        String prevname = null;
        TreeNodeImpl<String> prevnode = null;
        for (; ix < names.length && names[ix].startsWith(prefix);) {
            String n = names[ix].substring(prefix.length());
            if (prevname != null && names[ix].startsWith(prevname)) {
                ix = buildObjectNamesSubtree(names, prevname, ix, prevnode);
            } else {
                //System.out.println ("Build subtree set leaf: ("+n+","+ix);
                prevnode = new TreeNodeImpl<String>();
                prevnode.setData(n);
                root.addChild(names[ix], prevnode);
                prevname = names[ix];
                ix++;
            }
        }
        return ix;
    }
    TreeNodeImpl<String> objectnames = null;

    public TreeNodeImpl<String> getObjectNames() {
        if (objectnames != null) {
            return objectnames;
        }
        TreeNodeImpl<String> root = new TreeNodeImpl<String>();
        root.setData("InternetGatewayDevice");

        String[] n = Ejb.lookupDataModelBean().getObjectNames();
        buildObjectNamesSubtree(n, ".", 0, root);
        return objectnames = root;
    }
    private String selectedKey = null;

    public Collection<String> getParamNames() {
        Collection<String> names = Ejb.lookupDataModelBean().getChildNames(selectedKey, false);
        /*
        for (String n : names) {
        System.out.println("DataModelJsfBean::getParamNames " + n);
        }
         */
        return names;
    }

    public SelectItem[] getParamNamesList() {
        Collection<String> names = getParamNames();
        ArrayList<SelectItem> items = new ArrayList<SelectItem>(names.size());
        for (String n : names) {
            items.add(new SelectItem(n));
            System.out.println("DataModelJsfBean::getParamNamesList " + n);
        }
        return items.toArray(new SelectItem[items.size()]);
    }
    private List<String> selectedParamNames;

    public List<String> getSelectedParamNames() {
        return selectedParamNames;
    }

    public void setSelectedParamNames(List<String> selectedParamNames) {
        this.selectedParamNames = selectedParamNames;
    }
    private String currentRoot;

    public String getCurrentRoot() {
        return currentRoot;
    }

    public void setCurrentRoot(String currentRoot) {
        this.currentRoot = currentRoot;
    }

    public void nodeSelectListener(NodeSelectedEvent event) {
        HtmlTree tree = (HtmlTree) event.getComponent();
        System.out.println("nodeSelectListener: rowkey class = " + tree.getRowKey().getClass().getName());
        TreeRowKey treerowkey = (TreeRowKey) tree.getRowKey();
        String rowkey = treerowkey.getPath();
        System.out.println("nodeSelectListener: rowkey  = " + rowkey);
        String keys[] = rowkey.split(":");
        selectedKey = keys[keys.length - 1];
        setSelectedParamNames(null);
        System.out.println("nodeSelectListener: key = " + selectedKey);
        System.out.println("nodeSelectListener: rowdata = " + tree.getRowData());
        /*
        selectedNodeChildren.clear();
        TreeNode currentNode = tree.getModelTreeNode(tree.getRowKey());
        if (currentNode.isLeaf()){
        selectedNodeChildren.add((String)currentNode.getData());
        }else
        {
        Iterator<Map.Entry><Object, TreeNode>> it = currentNode.getChildren();
        while (it!=null &&it.hasNext()) {
        Map.Entry<Object, TreeNode> entry = it.next();
        selectedNodeChildren.add(entry.getValue().getData().toString());
        }
        }
         */
    }
}
