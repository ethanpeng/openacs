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
package org.openacs;

import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;

public interface DataModelLocalHome extends EJBLocalHome {

    org.openacs.DataModelLocal findByPrimaryKey(java.lang.String key) throws FinderException;

    org.openacs.DataModelLocal create(java.lang.String key) throws CreateException;

    public org.openacs.DataModelLocal create(java.lang.String name, String type,
            long min, long max, int length, String description, String version,
            String def, boolean writable, String trname) throws CreateException;

    int getCount();

    DataModelLocal findByName(String name) throws FinderException;

    Collection<DataModelLocal> findAll() throws FinderException;

    Collection<String> getChildNames(String parent, boolean fqdn);

    DataModelLocal lookupByName(String name) throws FinderException;

    String[] getObjectNames();

    public Collection<String> findChildNames(String parent) throws FinderException;
}
