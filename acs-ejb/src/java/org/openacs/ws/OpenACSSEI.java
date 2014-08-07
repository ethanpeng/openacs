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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;

public interface OpenACSSEI extends Remote {

    /**
     * Web service operation
     */
    public void FactoryReset(String oui, String sn) throws RemoteException;

    /**
     * Web service operation
     */
    public void RequestCPEConnection(String oui, String sn) throws RemoteException;

    /**
     * Web service operation
     */
    public void CPESet(String oui, String sn, String cfgname) throws RemoteException;

    /**
     * Web service operation
     */
    public void CPEDelete(String oui, String sn) throws RemoteException;

    /**
     * Web service operation
     */
    public Cpe CPEGet(String oui, String sn) throws RemoteException;

    /**
     * Web service operation
     */
    public void ConfigSet(String name, String hw, String version, String filename, String config) throws RemoteException;

    /**
     * Web service operation
     */
    public void ConfigDelete(String name) throws java.rmi.RemoteException;
}
