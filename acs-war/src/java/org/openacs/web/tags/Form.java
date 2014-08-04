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
package org.openacs.web.tags;

import java.io.IOException;
import java.util.ArrayList;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.component.UIOutput;
import javax.faces.context.ResponseWriter;
import org.openacs.web.HostPropertySet;
import org.openacs.web.ProfilePropertySet;
import org.openacs.web.Property;
import org.openacs.web.ServicePropertySet;

public class Form extends UIOutput {

    private static final String TABLE = "table";
    private static final String TBODY = "tbody";
    Object[] _values;
    Boolean visible = true;
    Object source = null;
    Integer instance = null;

    public Form() {
        super();
        //System.out.println("Form::Form");
        setRendererType(null);
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        //System.out.println("Form::setVisible");
        this.visible = visible;
    }

    public Object getSource() {
        //System.out.println("Form::getSource " + source);
        return source;
    }

    public void setSource(Object source) {
        //System.out.println("Form::setSource");
        this.source = source;
    }

    public Integer getInstance() {
        return instance;
    }

    public void setInstance(Integer instance) {
        this.instance = instance;
    }

    String getVariableValue(String name) {
        Object src = findSource();
        if (src instanceof ServicePropertySet) {
            ServicePropertySet s = (ServicePropertySet) src;
            return s.getValue(name);
        } else if (src instanceof HostPropertySet) {
            HostPropertySet s = (HostPropertySet) src;
            Property p = s.Get(name);
            return (p != null) ? p.getValue() : null;
        } else if (src instanceof ProfilePropertySet) {
            ProfilePropertySet s = (ProfilePropertySet) src;
            Property p = s.Get(name);
            return (p != null) ? p.getValue() : null;
        }
        return null;
    }

    private void buildPath(ArrayList<Integer> a) {
        Form p = findParent();
        if (p != null) {
            p.buildPath(a);
        }
        a.add(instance);
    }

    private Form findParent() {
        UIComponent parent = getParent();
        while (parent != null) {
            if (parent instanceof Form) {
                return (Form) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    private Object findSource() {
        if (source != null) {
            return source;
        }

        UIComponent parent = getParent();
        while (parent != null) {
            try {
                Form f = (Form) parent;
                if (f.source != null) {
                    return f.source;
                }
            } catch (ClassCastException e) {
            }
            parent = parent.getParent();
        }

        return null;
    }

    @Override
    public Object saveState(FacesContext _context) {
        if (_values == null) {
            _values = new Object[6];
        }
        _values[0] = super.saveState(_context);
        _values[1] = source;
        _values[2] = visible;
        _values[3] = path;
        _values[4] = instance;
        return _values;
    }

    @Override
    public void restoreState(FacesContext _context, Object _state) {
        _values = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        source = _values[1];
        visible = (Boolean) _values[2];
        path = (int[]) _values[3];
        instance = (Integer) _values[4];
    }

    @Override
    public void encodeBegin(FacesContext ctx) throws IOException {
        System.out.println("Form::encodeBegin id=" + getClientId(ctx) + " " + getContainerClientId(ctx));
//        DumpView(ctx);
        ResponseWriter writer = ctx.getResponseWriter();
        if (visible) {
            writer.startElement(TABLE, this);
            writer.writeAttribute("cellPadding", 0, "cellPadding");
            writer.writeAttribute("name", getId(), "name");
            writer.startElement(TBODY, this);
        } else {
            writer.startElement("div", this);
            writer.writeAttribute("name", getId(), "name");
        }
    }

    @Override
    public void encodeEnd(FacesContext ctx) throws IOException {
        ResponseWriter writer = ctx.getResponseWriter();
        if (visible) {
            writer.endElement(TBODY);
            writer.endElement(TABLE);
        } else {
            writer.endElement("div");
        }
    }

    @Override
    public boolean getRendersChildren() {
        return false;
    }

    private void mergeValues(Object props, UIComponent p) {
        System.out.println("Form::mergeValues2: enter");
        for (UIComponent c : p.getChildren()) {
            System.out.println("Form::mergeValues2: " + c+" id="+c.getId());
            if (c instanceof Param) {
                Param par = (Param) c;
                System.out.println("Form::mergeValues2: param " + par.getVarname()+" -> "+par.getParamValue()+" ("+par.isParam()+")");
                if (props instanceof ServicePropertySet) {
                    ServicePropertySet _p = (ServicePropertySet) props;
                    System.out.println("Form::mergeValues2: " + par.getVarname()+" -> "+par.getParamValue()+" ("+par.isParam()+")");
                    _p.Set(par.getVarname(), par.getParamValue(), par.isParam());
                } else if (props instanceof HostPropertySet) {
                    HostPropertySet _p = (HostPropertySet) props;
                    _p.Set(par.getVarname(), par.getParamValue());
                }
                if (props instanceof ProfilePropertySet) {
                    ProfilePropertySet _p = (ProfilePropertySet) props;
                    _p.Set(par.getVarname(), par.getParamValue());
                }
            } else {
                mergeValues(props, c);
            }
        }
        System.out.println("Form::mergeValues2: leave");
    }

    public void mergeValues(Object props) {
        System.out.println("Form::mergeValues: " + props);
        mergeValues(props, this);
        if (props instanceof ServicePropertySet) {
            ServicePropertySet _p = (ServicePropertySet) props;
            _p.Dump(System.out);
        }
    }

    private boolean getValid(UIComponent p) {
        for (UIComponent c : p.getChildren()) {
            boolean valid = true;
            if (c instanceof Param) {
                Param par = (Param) c;
                valid = par.getValid();
            } else {
                valid = getValid(c);
            }
            if (!valid) {
                return false;
            }
        }
        return true;
    }

    public boolean getValid() {
        return getValid(this);
    }
    protected int[] path;

    public int[] getPath() {
        return path;
    }

    public void setPath(int[] path) {
        this.path = path;
    }

    public Integer[] buildPath() {
        ArrayList<Integer> a = new ArrayList<Integer>();
        buildPath(a);
        return a.toArray(new Integer[a.size()]);
    }

    private void DumpView(FacesContext ctx, UIComponent p, int lvl) {
        System.out.print(tabs.substring(0, lvl + 1));
        System.out.println(p.getClass().getName() + ": " + p.getClientId(ctx) + " " + p.getId());
        for (UIComponent c : p.getChildren()) {
            DumpView(ctx, c, lvl + 1);
        }
    }
    private static final String tabs = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";

    private void DumpView(FacesContext ctx) {
        UIComponent p = this, r = this;
        while ((p = p.getParent()) != null) {
            r = p;
        }
        System.out.println("ROOT children " + r.getChildCount());
        DumpView(ctx, r, 0);
    }
}
