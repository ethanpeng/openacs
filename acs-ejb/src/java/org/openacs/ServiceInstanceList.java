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

import java.util.ArrayList;
import java.util.Collection;

public class ServiceInstanceList {

    private class svcentry {

        public Integer svcid;
        public Integer instance;
        public Integer parentsvcid;
        public Integer parentinstance;

        public svcentry(Integer svcid, Integer instance, Integer parentsvcid, Integer parentinstance) {
            this.instance = instance;
            this.parentinstance = parentinstance;
            this.parentsvcid = parentsvcid;
            this.svcid = svcid;
        }
    }
    private final static String INDEX = "{i}";
    ArrayList<svcentry> svcs;

    public ServiceInstanceList(Collection<Host2ServiceLocal> svcicollection) {
        svcs = new ArrayList<svcentry>(svcicollection.size());
        for (Host2ServiceLocal i : svcicollection) {
            svcs.add(new svcentry(i.getServiceid(), i.getInstance(), i.getParentServiceId(), i.getParentServiceInstance()));
        }
    }

    private svcentry find(Host2ServiceLocal svcinstance) {
        return find(svcinstance.getServiceid(), svcinstance.getInstance());
    }

    private svcentry find(Integer svcid, Integer instance) {
        for (svcentry e : svcs) {
            if (e.svcid.equals(svcid) && e.instance.equals(instance)) {
                return e;
            }
        }
        return null;
    }

    public Integer[] getInstancesArray(Host2ServiceLocal svcinstance) {
        ArrayList<Integer> is = new ArrayList<Integer>();

        svcentry e = find(svcinstance);

        while (e != null) {
            is.add(0, e.instance);
            if (e.parentinstance == null) {
                e = null;
            } else {
                e = find(e.parentsvcid, e.parentinstance);
            }
        }

        return is.toArray(new Integer[is.size()]);
    }

    public String mapName(String name, Integer[] ix) {
        StringBuffer b = new StringBuffer(name);
        int start = 0;
        for (int ii = 0; ii < ix.length; ii++) {
            int i = b.indexOf(INDEX, start);
            if (i == -1) {
                break;
            }
            b.replace(i, i + INDEX.length(), ix[ii].toString());
            start = i;
        }
        return b.toString();
    }
}
