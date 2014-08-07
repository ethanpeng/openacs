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
package org.openacs.ws;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.openacs.CPELocal;
import org.openacs.CPELocalHome;

public class OpenACSBean implements SessionBean {

    private SessionContext context;

    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Add code to acquire and use other enterprise resources (DataSource, JMS, enterprise bean, Web services)
    // TODO Add business methods or web service operations
    /**
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext aContext) {
        context = aContext;
    }

    /**
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    public void ejbActivate() {
    }

    /**
     * @see javax.ejb.SessionBean#ejbPassivate()
     */
    public void ejbPassivate() {
    }

    /**
     * @see javax.ejb.SessionBean#ejbRemove()
     */
    public void ejbRemove() {
    }
    // </editor-fold>

    /**
     * See section 7.10.3 of the EJB 2.0 specification
     * See section 7.11.3 of the EJB 2.1 specification
     */
    public void ejbCreate() {
        // TODO implement ejbCreate if necessary, acquire resources
        // This method has access to the JNDI context so resource aquisition
        // spanning all methods can be performed here such as home interfaces
        // and data sources.
    }

    // Add business logic below. (Right-click in editor and choose
    // "EJB Methods > Add Business Method" or "Web Service > Add Operation")
    /**
     * Lookup method for local EJB object
     */
    private CPELocal lookupCPEBean() {
        try {
            Context c = new InitialContext();
            CPELocalHome rv = (CPELocalHome) c.lookup("java:comp/env/ejb/CPEBean");
            return rv.create();
        } catch (Exception ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public void FactoryReset(String oui, String sn) throws RemoteException {
        try {
            //lookupCPEBean().FactoryReset(oui, sn);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    /**
     * Web service operation
     */
    public void RequestCPEConnection(String oui, String sn) throws RemoteException {
        //lookupCPEBean().RequestCPEConnection(oui, sn);
    }

    /**
     * Web service operation
     */
    public void CPESet(String oui, String sn, String cfgname) throws RemoteException {
        /*        HostsLocalHome hh = Ejb.lookupHostsBean();
        HostsLocal h  = null;
        try {
        h = hh.findByPrimaryKey(new HostsPK(oui, sn));
        } catch (FinderException ex) {
        try {
        h = hh.create(oui, sn, "");
        } catch (CreateException ex2) {
        throw new RemoteException(ex2.getMessage());
        }
        } catch (EJBException ex) {
        throw new RemoteException(ex.getMessage());
        }
        h.setConfigname(cfgname);
         */    }

    /**
     * Web service operation
     */
    public void CPEDelete(String oui, String sn) throws RemoteException {
        /*        HostsLocalHome hh = Ejb.lookupHostsBean();
        try {
        HostsLocal h = hh.findByPrimaryKey(new HostsPK(oui, sn));
        h.remove();
        } catch (Exception ex) {
        throw new RemoteException(ex.getMessage());
        } 
         */    }

    /**
     * Web service operation
     */
    public Cpe CPEGet(String oui, String sn) throws RemoteException {
        /*        HostsLocalHome hh = Ejb.lookupHostsBean();
        try {
        HostsLocal h = hh.findByPrimaryKey(new HostsPK(oui, sn));
        Cpe cpe = new Cpe();
        cpe.Cfgupdres = h.getCfgupdres();
        cpe.Cfgupdtime = h.getCfgupdtime().toString();
        cpe.Cfgversion = h.getCfgversion();
        cpe.Configname = h.getConfigname();
        cpe.Currentsoftware = h.getCurrentsoftware();
        cpe.Hardware = h.getHardware();
        cpe.Lastcontact = h.getLastcontact().toString();
        cpe.Oui = h.getOui();
        cpe.Serialno = h.getSerialno();
        cpe.Sfwupdres = h.getSfwupdres();
        cpe.Sfwupdtime = h.getSfwupdtime().toString();
        cpe.Url = h.getUrl();
        
        return cpe;
        } catch (Exception ex) {
        throw new RemoteException(ex.getMessage());
        } 
         */
        return null;
    }

    /**
     * Web service operation
     */
    public void ConfigSet(String name, String hw, String version, String filename, String config) throws RemoteException {
        /*
        ConfigurationLocalHome cfgs = Ejb.lookupConfigurationBean();
        ConfigurationLocal cfg  = null;
        try {
        cfg = cfgs.findByPrimaryKey(name);
        } catch (FinderException ex) {
        try {
        cfg = cfgs.create (name);
        } catch (CreateException ex2) {
        throw new RemoteException(ex2.getMessage());
        }
        } catch (EJBException ex) {
        throw new RemoteException(ex.getMessage());
        }
        //        cfg.setHardware(hw);
        cfg.setVersion(version);
        cfg.setFilename(filename);
        cfg.setConfig(config.getBytes());
         */
    }

    /**
     * Web service operation
     */
    public void ConfigDelete(String name) throws java.rmi.RemoteException {
        /*
        ConfigurationLocalHome cfgs = Ejb.lookupConfigurationBean();
        ConfigurationLocal cfg  = null;
        try {
        cfg = cfgs.findByPrimaryKey(name);
        cfg.remove();
        } catch (Exception ex) {
        throw new RemoteException(ex.getMessage());
        }
         */
    }
}
