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
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.openacs.datamodel.Parameter;
import org.openacs.DeviceProfile2SoftwareLocal;
import org.openacs.DeviceProfile2SoftwareLocalHome;
import org.openacs.DeviceProfile2SoftwarePK;
import org.openacs.DeviceProfileLocal;
import org.openacs.HardwareModelLocal;
import org.openacs.ScriptLocal;
import org.openacs.SoftwareLocal;
import org.openacs.utils.Ejb;

public class DeviceProfileBean extends JsfBeanBase {

    private static final String SCRIPT_DEFAULT = "Default";

    /** Creates a new instance of DeviceProfileBean */
    public DeviceProfileBean() {
    }
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    protected Integer periodicInformInterval;

    public Integer getPeriodicInformInterval() {
        return periodicInformInterval;
    }

    public void setPeriodicInformInterval(Integer periodicInformInterval) {
        this.periodicInformInterval = periodicInformInterval;
    }
    protected Integer daysToKeepStats;

    public Integer getDaysToKeepStats() {
        return daysToKeepStats;
    }

    public void setDaysToKeepStats(Integer daysToKeepStats) {
        this.daysToKeepStats = daysToKeepStats;
    }
    protected Boolean saveStats;

    public Boolean getSaveStats() {
        return saveStats;
    }

    public void setSaveStats(Boolean saveStats) {
        this.saveStats = saveStats;
    }
    protected Boolean saveLog;

    public Boolean getSaveLog() {
        return saveLog;
    }

    public void setSaveLog(Boolean saveLog) {
        this.saveLog = saveLog;
    }
    protected Boolean saveParamValues;

    public Boolean getSaveParamValues() {
        return saveParamValues;
    }

    public void setSaveParamValues(Boolean saveParamValues) {
        this.saveParamValues = saveParamValues;
    }
    protected String scriptName = SCRIPT_DEFAULT;

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String clear() {
//        System.out.println ("DeviceProfileBean::clear");
        name = null;
        daysToKeepStats = 0;
        periodicInformInterval = 1800;
        saveStats = false;
        saveLog = false;
        saveParamValues = false;
        saveParamValuesInterval = 0;
        saveParamValuesOnBoot = false;
        saveParamValuesOnChange = false;
        scriptName = SCRIPT_DEFAULT;

        return "create";
    }

    private void save(DeviceProfileLocal d) {
        d.setInforminterval(periodicInformInterval);
        d.setDayskeepstats(daysToKeepStats);
        d.setSavestats(saveStats);
        d.setSaveLog(saveLog);
        d.setSaveParamValues(saveParamValues);
        d.setSavestats(saveStats);
        d.setSaveParamValuesInterval(saveParamValuesInterval);
        d.setSaveParamValuesOnBoot(saveParamValuesOnBoot);
        d.setSaveParamValuesOnChange(saveParamValuesOnChange);
        d.setScriptname(scriptName);
        try {
            props.Save();
        } catch (Exception ex) {
            setErrorMessage(ex.getMessage());
        }
    }

    public String create() {
        try {
            save(Ejb.lookupDeviceProfileBean().create(name));
            return "created";
        } catch (CreateException ex) {
            setErrorMessage(ex.getMessage());
        }
        return null;
    }

    public String save() {
        try {
            save(Ejb.lookupDeviceProfileBean().findByPrimaryKey(name));
            setSaved();
            return "saved";
        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
        }
        return null;
    }

    public String load() {
        try {
            DeviceProfileLocal d = Ejb.lookupDeviceProfileBean().findByPrimaryKey(name);
            setPeriodicInformInterval(d.getInforminterval());
            setDaysToKeepStats(d.getDayskeepstats());
            setSaveParamValuesInterval(d.getSaveParamValuesInterval());
            saveParamValuesOnBoot = d.getSaveParamValuesOnBoot();
            saveParamValuesOnChange = d.getSaveParamValuesOnChange();
            saveLog = d.getSaveLog();
            saveParamValues = d.getSaveParamValues();
            scriptName = d.getScriptname();

            props = new ProfilePropertySet(name);
            props.Load();
            return "loaded";
        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
        }
        return null;
    }

    public String delete() {
        if (name.equalsIgnoreCase("default")) {
            setErrorMessage("Builtin profile 'Default' can not be deleted.");
            return null;
        }
        try {
            Ejb.lookupDeviceProfileBean().findByPrimaryKey(name).remove();
            setDeleted();
            return "deleted";
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
            return Ejb.lookupDeviceProfileBean().findAll();
        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
            return null;
        }
    }

    public Collection getList() {
        try {
            Iterator lst = Ejb.lookupDeviceProfileBean().findAll().iterator();
            ArrayList<SelectItem> a = new ArrayList<SelectItem>();
            while (lst.hasNext()) {
                DeviceProfileLocal it = (DeviceProfileLocal) lst.next();
                a.add(new SelectItem(it.getName(), it.getName()));
            }
            return a;
        } catch (FinderException ex) {
            setErrorMessage(ex.getMessage());
        }
        return null;
    }
    protected Integer saveParamValuesInterval;

    public Integer getSaveParamValuesInterval() {
        return saveParamValuesInterval;
    }

    public void setSaveParamValuesInterval(Integer saveParamValuesInterval) {
        this.saveParamValuesInterval = saveParamValuesInterval;
        if (this.saveParamValuesInterval == null) {
            this.saveParamValuesInterval = 0;
        }
    }
    protected Boolean saveParamValuesOnChange;

    public Boolean getSaveParamValuesOnChange() {
        return saveParamValuesOnChange;
    }

    public void setSaveParamValuesOnChange(Boolean saveParamValuesOnChange) {
        this.saveParamValuesOnChange = saveParamValuesOnChange;
    }
    protected Boolean saveParamValuesOnBoot;

    public Boolean getSaveParamValuesOnBoot() {
        return saveParamValuesOnBoot;
    }

    public void setSaveParamValuesOnBoot(Boolean saveParamValuesOnBoot) {
        this.saveParamValuesOnBoot = saveParamValuesOnBoot;
    }

    public Collection getDevices() {
        //System.out.println("DeviceProfileBean.getDevices");
        try {
//            Iterator cfgs = Ejb.lookupConfigurationBean().findAll().iterator();
            Iterator<HardwareModelLocal> hwms = Ejb.lookupHardwareModelBean().findAll().iterator();
            ArrayList<SelectItem> a = new ArrayList<SelectItem>();
            while (hwms.hasNext()) {
                HardwareModelLocal hwm = hwms.next();
                a.add(new SelectItem(hwm.getId(), hwm.getDisplayName()));
            }
            return a;
        } catch (FinderException ex) {
        }
        return null;
    }
    private Integer hwid = -1;

    public Integer getHwid() {
        //System.out.println("DeviceProfileBean.getHwid = " + hwid);
        return hwid;
    }

    public void setHwid(Integer hwid) {
        //System.out.println("DeviceProfileBean.setHwid = " + hwid);
        this.hwid = (hwid != null) ? hwid : -1;
    }

    public Collection getScriptNames() {
        ArrayList<SelectItem> a = new ArrayList<SelectItem>();
        try {
            Iterator<ScriptLocal> fws = Ejb.lookupScriptBean().findAll().iterator();
            while (fws.hasNext()) {
                ScriptLocal script = fws.next();
                a.add(new SelectItem(script.getName(), script.getName()));
            }
        } catch (FinderException ex) {
        }
        return a;
    }

    public Collection getFwVersions() {
//        javax.faces.context.FacesContext.getCurrentInstance().
        //System.out.println("DeviceProfileBean.getFwVersions hwid=" + hwid);
        ArrayList<SelectItem> a = new ArrayList<SelectItem>();
        a.add(new SelectItem(DeviceProfile2SoftwareLocal.NOUPDATE, "No update"));
        a.add(new SelectItem(DeviceProfile2SoftwareLocal.AUTOUPDATE, "Automatic"));
        try {
//            Iterator cfgs = Ejb.lookupConfigurationBean().findAll().iterator();
            Iterator<SoftwareLocal> fws = Ejb.lookupSoftwareBean().findByHardware(hwid).iterator();
            while (fws.hasNext()) {
                SoftwareLocal fw = fws.next();
                a.add(new SelectItem(fw.getVersion(), fw.getVersion()));
            }
        } catch (FinderException ex) {
        }
        return a;
    }
    private String version = "";

    public String getVersion() {
        //System.out.println("DeviceProfileBean.getVersion = " + version);
        return version;
    }

    public void setVersion(String version) {
        //System.out.println("DeviceProfileBean.setVersion = " + version);
        this.version = (version != null) ? version : "";
    }

    public String AddFw() {
//        System.out.println("DeviceProfileBean.AddFw name="+name+" hwid="+hwid+" version="+version);
        DeviceProfile2SoftwareLocalHome h = Ejb.lookupDeviceProfile2SoftwareBean();
        try {
            h.create(name, hwid, version);
        } catch (CreateException ex) {
            setErrorMessage(ex.getMessage());
        }
        return null;
    }

    public String RemoveFwMapping() {
        //      System.out.println("DeviceProfileBean.RemoveFwMapping");
        DeviceProfile2SoftwareLocalHome h = Ejb.lookupDeviceProfile2SoftwareBean();
        try {
            h.remove(new DeviceProfile2SoftwarePK(hwidrem, name));
        } catch (Exception ex) {
            setErrorMessage(ex.getMessage());
        }
        return null;
    }
    private Integer hwidrem;

    public Integer getHwidrem() {
        return hwidrem;
    }

    public void setHwidrem(Integer hwidrem) {
        this.hwidrem = hwidrem;
    }

    public class FirmwareMapEntry {

        private String version;
        private String model;
        private Integer hwid;

        public FirmwareMapEntry(String model, Integer hwid, String version) {
            this.version = version;
            this.hwid = hwid;
            this.model = model;
        }

        public String getVersion() {
            return version;
        }

        public String getModel() {
            return model;
        }

        public Integer getHwid() {
            return hwid;
        }
    }

    public Collection getFwMap() {
        ArrayList<FirmwareMapEntry> a = new ArrayList<FirmwareMapEntry>();
        Map<Integer, HardwareModelLocal> hwmap = Ejb.getHardwareModelMap();

        try {
            Iterator<DeviceProfile2SoftwareLocal> i = Ejb.lookupDeviceProfile2SoftwareBean().findByProfile(name).iterator();
            while (i.hasNext()) {
                DeviceProfile2SoftwareLocal o = i.next();
                HardwareModelLocal hwm = hwmap.get(o.getHwid());
                a.add(new FirmwareMapEntry(hwm.getDisplayName(), (Integer) hwm.getId(), o.getVersion()));
            }
        } catch (FinderException ex) {
        }
        return a;
    }

    public class entry {

        public entry(String name) {
            setName(name);
        }
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "name=" + name;
        }
    }
    private ListDataModel lm = null;

    public ListDataModel getVars() {
        System.out.println("GetVars =" + lm);
        if (lm == null) {
            ArrayList<entry> l = new ArrayList<entry>();
            l.add(new entry("11"));
            l.add(new entry("22"));
            l.add(new entry("33"));
            l.add(new entry("44"));
            lm = new ListDataModel(l);

        } else {
            System.out.println("Wrapped data = " + lm.getWrappedData().getClass().getName());
            ArrayList<entry> l = (ArrayList<entry>) lm.getWrappedData();
            for (entry i : l) {
                System.out.println("d " + i);
            }
        }
        return lm;
    }

    public String vyksmas() {
        System.out.println("VYYYYYKSMAS");
        try {
            if (lm != null) {
                if (lm.isRowAvailable()) {
                    System.out.println("index=" + lm.getRowIndex() + " available=" + lm.isRowAvailable() + " data=" + lm.getRowData());
                } else {
                    System.out.println("index=" + lm.getRowIndex() + " available=" + lm.isRowAvailable());
                }
            }
        } catch (Exception e) {
            System.out.println("Nelaime: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    private ProfilePropertySet props;

    public Property[] getProperties() {

        ArrayList<Property> a = new ArrayList<Property>(props.getProperties().size());
        for (Map.Entry<String, Property> p : props.getProperties().entrySet()) {
            if (!p.getValue().isHidden()) {
                a.add(p.getValue());
            }
        }

        return a.toArray(new Property[0]);
    }
    String propertyToRemove = "";

    public void setPropertyToRemove(String propertyToRemove) {
        this.propertyToRemove = propertyToRemove;
    }

    public String RemoveProperty() {
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

    public String AddProperty() {
        //props.Add(propname, propvalue);
        //propname = DataModel.getNormalizedName("", propname);
        DataModelValidationResult r = DataModelJsfBean.Validate(Parameter.getNormalizedName("", propname), propvalue);
        if (r.isOk()) {
            props.Add(new Property(Parameter.getNameWithoutRoot(propname), propvalue));
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
        return null;
    }

    public Collection autocompletePropName(Object v) {
        return DataModelJsfBean.autocompletePropName(v);
    }
}
