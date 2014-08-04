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

import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import org.openacs.HostPropertyLocal;
import org.openacs.HostPropertyLocalHome;
import org.openacs.HostPropertyPK;
import org.openacs.utils.Ejb;

public class HostPropertySet extends PropertySet<Property> {

    private HostPropertyLocalHome home;
    private Integer hostid;

    public HostPropertySet(Integer hostid) {
        this.home = Ejb.lookupHostPropertyBean();
        this.hostid = hostid;
    }

    public void Load(Collection<HostPropertyLocal> props) {
        //System.out.println ("HostPropertySet::Load");
        for (HostPropertyLocal p : props) {
            //System.out.println ("HostPropertySet::Load add property "+p.getName()+"="+p.getValue());
            original.put(p.getName(), new Property(p.getName(), p.getValue()));
        }
        super.Load();
    }

    @Override
    protected void DeleteEntry(Property p) throws RemoveException {
        //System.out.println ("HostPropertySet::DeleteEntry "+hostid+" "+p.getName());
        home.remove(new HostPropertyPK(hostid, p.getName()));
    }

    @Override
    protected void SetEntry(Property p) throws FinderException {
        //System.out.println ("HostPropertySet::SetEntry "+hostid+" "+p.getName());
        HostPropertyLocal pl = home.findByPrimaryKey(new HostPropertyPK(hostid, p.getName()));
        pl.setValue(p.getValue());
    }

    @Override
    protected void InsertEntry(Property p) throws CreateException {
        home.create(hostid, p.getName(), p.getValue());
    }
}
