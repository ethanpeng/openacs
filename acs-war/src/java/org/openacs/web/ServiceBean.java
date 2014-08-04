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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.faces.model.SelectItem;
import org.openacs.datamodel.Parameter;
import org.openacs.ServiceLocal;
import org.openacs.utils.Ejb;
import org.openacs.web.tags.Form;

public class ServiceBean extends JsfBeanBase {

    protected static final String TYPE_GENERIC = "generic";
    private String defaultParent;

    public String getDefaultParent() {
        return defaultParent;
    }

    public void setDefaultParent(String defaultParent) {
        this.defaultParent = defaultParent;
    }

    public ServiceBean() {
    }
    private Form form;

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
        if (form != null && form.getSource() == null) {
            ServicePropertySet src = new ServicePropertySet(id);
            form.setSource(src);
            /*
            Properties ps = new Properties();
            if (props != null && props.current != null) {
            for (Entry<String, ServiceProperty> p : props.current.entrySet()) {
            ps.put(p.getKey(), p.getValue().getValue());
            }
            }
            form.setSource(ps);
             */
        }
    }
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    protected String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        System.out.println("ServiceBean::setType " + type);
        this.type = type;
    }
    protected String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    protected Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String clear() {
        name = null;
        description = null;
        propname = propvalue = "";
        propparam = false;
        id = null;
        props = new ServicePropertySet(id);
        type = TYPE_GENERIC;
        return "create_service";
    }

    private void mergeValues() {
        for (Entry e : _props.entrySet()) {
            props.Set((String) e.getKey(), (String) e.getValue(), false);
        }
    }

    private void save(ServiceLocal svc) {
        svc.setName(name);
        svc.setDescription(description);
        svc.setType(type);
        svc.setDefaultparentservice(defaultParent);
        try {
            if (form != null) {
                form.mergeValues(props);
            }
            if (_props != null) {
                mergeValues();
            }
            if (props != null) {
                props.Save();
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            setErrorMessage(ex.getMessage());
        }

    }

    public String create() {
        System.out.println("ServiceBean::create");
        try {
            ServiceLocal svc = Ejb.lookupServiceBean().create();
            id = (Integer) svc.getId();

            props.setId(id);
            if (ServiceLocal.TYPE_VOICEPROFILE.equals(type)) {
                props.Set(".Services.VoiceService.1.VoiceProfile.{i}.Name", name);
            }
            save(svc);
            return "created_service";
            /*
            if (type == null || type.equals("") || type.equals("generic")) {
            return "created_service";
            }
            return "created_" + type;
             */
        } catch (CreateException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            setErrorMessage(ex.getMessage());
        }

        return null;
    }

    public String save() {
        try {
            save(Ejb.lookupServiceBean().findByPrimaryKey(id));
            setSaved();

            return null;
        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
        }

        return null;
    }

    public String load() {
        try {
            ServiceLocal svc = Ejb.lookupServiceBean().findByPrimaryKey(id);
            name = svc.getName();
            description = svc.getDescription();
            type = svc.getType();
            props = new ServicePropertySet(id);
            props.Load();
            if (type == null || type.equals("") || type.equals(TYPE_GENERIC)) {
                System.out.println("ServiceBean::load return \"loaded_service\"");
                return "loaded_service";
            }

            System.out.println("ServiceBean::load return \"loaded_" + type + "\"");
            return "loaded_" + type;
        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
        }

        return null;
    }

    public String delete() {
        try {
            Ejb.lookupServiceBean().findByPrimaryKey(id).remove();
            setDeleted();
            return "deleted_service";
        } catch (RemoveException ex) {
            setErrorMessage(ex.getMessage());
        } catch (EJBException ex) {
            setErrorMessage(ex.getMessage());
        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
        }
        return null;
    }

    public Collection getAll() {
        try {
            return Ejb.lookupServiceBean().findAll();
        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
            return null;
        }
    }

    public Collection getList() {
        try {
            Iterator<ServiceLocal> lst = Ejb.lookupServiceBean().findAll().iterator();
            ArrayList<SelectItem> a = new ArrayList<SelectItem>();
            while (lst.hasNext()) {
                ServiceLocal it = lst.next();
                a.add(new SelectItem(it.getId(), it.getName()));
            }
            return a;
        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
        }
        return null;
    }
    private ServicePropertySet props;

    public ServiceProperty[] getProperties() {

        ArrayList<Property> a = new ArrayList<Property>(props.getProperties().size());
        for (Map.Entry<String, ServiceProperty> p : props.getProperties().entrySet()) {
            if (!p.getValue().isHidden()) {
                a.add(p.getValue());
            }
        }

        return a.toArray(new ServiceProperty[0]);
    }
    private Properties _props = null;

    public Properties getValues() {
        if (_props == null) {
            for (ServiceProperty p : getProperties()) {
                _props.put(p.getName(), p.getValue());
            }
        }
        return _props;
    }
    String propertyToRemove = "";

    public void setPropertyToRemove(String propertyToRemove) {
        this.propertyToRemove = propertyToRemove;
    }

    public String RemoveProperty() {
        System.out.println("ServiceBean::RemoveProperty " + propertyToRemove);
        props.Remove(propertyToRemove);
        return null;
    }
    String propname = "";

    public void setPropname(String propname) {
        this.propname = propname;
    }

    public String getPropname() {
        return this.propname;
    }
    String propvalue = "";

    public void setPropvalue(String propvalue) {
        this.propvalue = propvalue;
    }

    public String getPropvalue() {
        return this.propvalue;
    }
    boolean propparam = false;

    public void setPropparam(boolean propparam) {
        this.propparam = propparam;
    }

    public boolean getPropparam() {
        return this.propparam;
    }

    public String AddProperty() {
        //props.Add(propname, propvalue);
//        propname = DataModel.getNormalizedName("", propname);
        DataModelValidationResult r = DataModelJsfBean.Validate(Parameter.getNormalizedName("", propname), propvalue);
        if (r.isOk()) {
            props.Add(new ServiceProperty(Parameter.getNameWithoutRoot(propname), propvalue, propparam));
            if (r.getMessage() != null) {
                setWarningMessage(r.getMessage());
            }
        } else {
            if (r.getMessage() != null) {
                setErrorMessage(r.getMessage());
            } else {
                setErrorMessage("Uknown validation error");
            }
        }
        propname = propvalue = "";
        propparam = false;
        return null;
    }

    private Collection getServices(String type) {
        try {
            Iterator<ServiceLocal> svcs = Ejb.lookupServiceBean().findByType(type).iterator();
            ArrayList<SelectItem> a = new ArrayList<SelectItem>();
            while (svcs.hasNext()) {
                ServiceLocal svc = svcs.next();
                a.add(new SelectItem(svc.getId(), svc.getName()));
            }
            return a;
        } catch (FinderException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Collection getVoiceProfiles() {
        return getServices(ServiceLocal.TYPE_VOICEPROFILE);
    }
}
