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

import java.io.File;
import org.openacs.Application;

public class SettingsBean extends JsfBeanBase {

    /** Creates a new instance of SettingsBean */
    public SettingsBean() {
        overrideServerName = Application.getOverrideServerName();
        firmwarePath = Application.getFirmwarePath();
        autoCreateCpe = Application.getAutoCreateCpe();
        STUNPort = Application.getSTUNport();
        noNatNet = Application.getNoNATNet();
    }

    public String Save() {
        try {
            File fwdir = new File(firmwarePath);
            String msg = null;
            if (!fwdir.exists()) {
                msg = "Firmware path " + firmwarePath + " does not exist";
            } else if (!fwdir.isDirectory()) {
                msg = firmwarePath + " is not directory";
            } else if (!fwdir.canRead()) {
                msg = firmwarePath + " is not readable";
            } else if (!fwdir.canWrite()) {
                msg = firmwarePath + " is not writable";
            }
            if (msg != null) {
                setErrorMessage(msg);
                return null;
            }

            Application.setOverrideServerName(overrideServerName);
            Application.setAutoCreateCpe(autoCreateCpe);
            Application.setFirmwarePath(firmwarePath);
            Application.setSTUNport(STUNPort);
            Application.setNoNATNet(noNatNet);
            setSaved();
        } catch (Exception ex) {
            setErrorMessage(ex.getMessage());
        }
        return null;
    }
    protected String overrideServerName = null;

    public String getOverrideServerName() {
        return overrideServerName;
    }

    public void setOverrideServerName(String overrideServerName) {
        this.overrideServerName = overrideServerName;
    }
    protected String firmwarePath = null;

    public String getFirmwarePath() {
        return firmwarePath;
    }

    public void setFirmwarePath(String firmwarePath) {
        this.firmwarePath = firmwarePath;
    }
    protected boolean autoCreateCpe = true;

    public boolean getautoCreateCpe() {
        return autoCreateCpe;
    }

    public void setautoCreateCpe(boolean autoCreateCpe) {
        this.autoCreateCpe = autoCreateCpe;
    }
    private int STUNPort;

    public int getSTUNPort() {
        return STUNPort;
    }

    public void setSTUNPort(int STUNPort) {
        this.STUNPort = STUNPort;
    }
    private String noNatNet = "";

    public String getNoNatNet() {
        return noNatNet;
    }

    public void setNoNatNet(String noNatNet) {
        this.noNatNet = noNatNet;
    }
}
