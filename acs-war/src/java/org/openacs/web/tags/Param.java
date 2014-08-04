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

import java.util.Map;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.component.UIInput;
import javax.faces.context.ResponseWriter;
import org.openacs.datamodel.Parameter;
import org.openacs.datamodel.Type;

public class Param extends UIInput {

    private static final String TD = "td";
    private static final String TR = "tr";
    private Object[] _values;
    private String varname;
    private boolean fNew = true;
    //private String type;
    private int valid = Parameter.VALIDATION_OK;
    //private DataModel dm = null;
    private Form form = null;
    private boolean simple = false;

    public Param() {
        super();
        setRendererType(null);
    }

    public boolean isSimple() {
        return simple;
    }

    public void setSimple(boolean simple) {
        this.simple = simple;
    }

    public void setVarname(String varname) {
        this.varname = varname;
    }

    private Parameter getDataModel() {
        return Parameter.lookup(getVarname());
    }

    public String getVarname() {
        if (varname != null) {
            return varname;
        }
        ValueExpression _ve = getValueExpression("varname");
        if (_ve != null) {
            return (java.lang.String) _ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }
    private String description;

    public void setDescription(String description) {
        //System.out.println("Param::setDescription");
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public Object saveState(FacesContext _context) {
        //System.out.println("Param2::saveState clientid=" + getClientId(_context) + " id=" + getId() + " varname=" + varname);
        if (_values == null) {
            _values = new Object[6];
        }
        _values[0] = super.saveState(_context);
        _values[1] = description;
        _values[2] = varname;
        _values[4] = param;
        _values[5] = simple;
        return _values;
    }

    @Override
    public void restoreState(FacesContext _context, Object _state) {
        fNew = false;
        _values = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.description = (String) _values[1];
        this.varname = (String) _values[2];
        this.param = (Boolean) _values[4];
        this.simple = (Boolean) _values[5];
        //System.out.println("Param2::restoreState varname=" + varname);
    }

    public int getSize() {
        //System.out.println("Param2::getSize");
        return 50;
    }

    @Override
    public void encodeBegin(FacesContext ctx) throws IOException {
        //System.out.println("Param2::encodeBegin id=" + getClientId(ctx) + " " + getContainerClientId(ctx));
        if (param) {
            return;
        }
        ResponseWriter w = ctx.getResponseWriter();
        String id = getClientId(ctx);
        if (!simple) {
            w.startElement(TR, this);
            w.startElement(TD, this);
            String vn = getVarname();
            String d = vn;
            if (description != null) {
                d = description;
            } else {
                int ix = vn.lastIndexOf('.');
                if (ix != -1) {
                    d = vn.substring(ix + 1);
                    if (d.equals("Enable") && ix > 1) {
                        ix = vn.lastIndexOf('.', ix - 1);
                        if (ix != -1) {
                            d = vn.substring(ix + 1);
                            d.replace('.', ' ');
                        }
                        if (d.startsWith("{i}.")) {
                            d = d.substring(4);
                        }
                    }
                }
            }
            w.writeText(d, null);
            w.endElement(TD);
            w.startElement(TD, this);
        }
        w.startElement("input", this);
        Parameter dm = getDataModel();
        String type = (dm != null) ? dm.getType() : "string";
        if (fNew) {
            String value = getVariableValue(varname);
            if (value == null) {
                if (dm != null && dm.getDefaultValue() != null) {
                    value = dm.getDefaultValue();
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    if (value.equals("-") || value.equals("<Empty>")) {
                        value = "";
                    }
                } else {
                    value = "";
                }
            }
            setValue(value);
        }

        if (type.equals("boolean")) {
            w.writeAttribute("type", "checkbox", null);
            Object value = getValue();
            if (value != null && getValue().equals("1")) {
                w.writeAttribute("checked", "checked", null);
            }
        } else {
            w.writeAttribute("type", "text", null);
            w.writeAttribute("value", getValue(), "value");
            w.writeAttribute("size", 50, "size");
        }
        w.writeAttribute("name", id, "name");
        w.endElement("input");
        if ((type.equals(Type.INT.toString()) || type.equals(Type.UNSIGNEDINT.toString())) && dm != null) {
            if (dm.getMin() != Parameter.INFINITY_LOW || dm.getMax() != Parameter.INFINITY_HIGH) {
                w.writeText("[" + (dm.getMin() == Parameter.INFINITY_LOW ? "unbound" : dm.getMin()) + ".." + (dm.getMax() == Parameter.INFINITY_HIGH ? "unbound" : dm.getMax()) + "]", null);
            }
        }

        if (!simple) {
            w.endElement(TD);

            w.startElement(TD, this);
        } else {
            w.writeText(" ", null);
        }
        if (valid != Parameter.VALIDATION_OK) {
            if (dm == null) {
                dm = Parameter.lookup(varname);
            }
            if (dm != null) {
                switch (valid) {
                    case Parameter.VALIDATION_BADDATE:
                    case Parameter.VALIDATION_BADBOOLEAN:
                    case Parameter.VALIDATION_BADINT:
                        w.writeText("Value not valid for type " + type, null);
                        break;
                    case Parameter.VALIDATION_TOOBIG:
                        w.writeText("Value exceeds maximum of " + dm.getMax(), null);
                        break;
                    case Parameter.VALIDATION_TOOSMALL:
                        w.writeText("Value less than minimum of " + dm.getMin(), null);
                        break;
                    case Parameter.VALIDATION_TOOLONG:
                        w.writeText("Value longer than " + dm.getMaxLength(), null);
                        break;
                }
            } else {
                w.writeText("Can not validate", null);
            }
        }
        if (!simple) {
            w.endElement(TD);
            w.endElement(TR);
        }
    }

    @Override
    public void decode(FacesContext ctx) {
        String id = getClientId(ctx);
        Map requestMap = ctx.getExternalContext().getRequestParameterMap();
        String value = (String) requestMap.get(getClientId(ctx));
        //System.out.println("Param2::decode value=" + value);
        Parameter dm = getDataModel();
        if (dm != null && "boolean".equals(dm.getType())) {
            if (value == null) {
                value = "0";
            } else {
                value = "1";
            }
        }

        try {
            valid = Parameter.lookup(varname).Validate(value);
        } catch (Exception e) {
            valid = Parameter.VALIDATION_UNKNOWN;
        }

        setValue(value);
    }

    String getParamValue() {
        Object v = getValue();
        if (v == null) {
            return "";
        } else {
            return v.toString();
        }
    }
    /*
    void mergeValue(Properties props) {
    Object v = getValue();
    if (v == null) {
    v = "";
    }
    props.put(varname, getValue());
    }
     */

    boolean getValid() {
        return (isParam() || (valid == Parameter.VALIDATION_OK || valid == Parameter.VALIDATION_UNKNOWN));
    }

    private Form getForm() {
        //System.out.println("Param2::getForm enter");
        UIComponent p = getParent();
        while (p != null) {
            if (p instanceof Form) {
                //System.out.println("Param2::getForm return " + p);
                return (Form) p;
            }
            p = p.getParent();
            //System.out.println("Param2::getForm up " + p);
        }
        return null;
    }
    /*
    private Properties getSource() {
    Form f = getForm();
    //System.out.println("Param2::getSource f=" + f);
    if (f != null) {
    //System.out.println("Param2::getSource 1");
    Properties r = f.findSource();
    //System.out.println("Param2::getSource 2 " + r);
    return r;
    }
    throw new RuntimeException("acs:Param must be child of acs:Form which has source set");
    }
     */

    protected String getVariableValue(String name) {
        /*
        if (form == null) {
        form = getForm();
        }
        if (form == null) {
        throw new RuntimeException("acs:Param must be child of acs:Form which has source set");
        }
        return form.getVariableValue(name);
         * */
        return (String) getValue();
    }
    protected Boolean param = Boolean.FALSE;

    public Boolean isParam() {
        return param;
    }

    public void setParam(Boolean param) {
        this.param = param;
    }
}
