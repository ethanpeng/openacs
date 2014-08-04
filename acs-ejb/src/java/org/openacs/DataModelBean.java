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

import org.openacs.datamodel.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;

public abstract class DataModelBean implements EntityBean {

    private EntityContext context;

    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click on the + sign on the left to edit the code.">
    // TODO Consider creating Transfer Object to encapsulate data
    // TODO Review finder methods
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(EntityContext aContext) {
        context = aContext;
    }

    /**
     * @see javax.ejb.EntityBean#ejbActivate()
     */
    public void ejbActivate() {
    }

    /**
     * @see javax.ejb.EntityBean#ejbPassivate()
     */
    public void ejbPassivate() {
    }

    /**
     * @see javax.ejb.EntityBean#ejbRemove()
     */
    public void ejbRemove() {
    }

    /**
     * @see javax.ejb.EntityBean#unsetEntityContext()
     */
    public void unsetEntityContext() {
        context = null;
    }

    /**
     * @see javax.ejb.EntityBean#ejbLoad()
     */
    public void ejbLoad() {
    }

    /**
     * @see javax.ejb.EntityBean#ejbStore()
     */
    public void ejbStore() {
    }

    // </editor-fold>
    public abstract java.lang.String getName();

    public abstract void setName(java.lang.String key);

    public java.lang.String ejbCreate(java.lang.String name) throws CreateException {
        if (name == null) {
            throw new CreateException("The field \"name\" must not be null");
        }

        // TODO add additional validation code, throw CreateException if data is not valid
        setName(name);

        return null;
    }

    public java.lang.String ejbCreate(java.lang.String name, String type,
            long min, long max, int length, String description, String version,
            String def, boolean writable, String trname) throws CreateException {
        if (name == null) {
            throw new CreateException("The field \"name\" must not be null");
        }
        if (type == null) {
            throw new CreateException("The field \"name\" must not be null");
        }

        // TODO add additional validation code, throw CreateException if data is not valid
        setName(name);
        setType(type);
        setMin(min);
        setMax(max);
        setLength(length);
        setDescription(description.getBytes());
        setVersion(version);
        setDefaultvalue(def);
        setWritable(writable);
        setTrname(trname);

        return null;

    }

    public void ejbPostCreate(java.lang.String key) {
    }

    public void ejbPostCreate(java.lang.String name, String type,
            long min, long max, int length, String description, String version,
            String def, boolean writable, String trname) {
    }

    public abstract String getType();

    public abstract void setType(String type);

    public abstract long getMin();

    public abstract void setMin(long min);

    public abstract long getMax();

    public abstract void setMax(long max);

    public abstract int getLength();

    public abstract void setLength(int length);

    public abstract byte[] getDescription();

    public abstract void setDescription(byte[] description);

    public abstract String getVersion();

    public abstract void setVersion(String version);

    public abstract String getDefaultvalue();

    public abstract void setDefaultvalue(String def);

    public abstract boolean getWritable();

    public abstract void setWritable(boolean writable);

    public abstract String getTrname();

    public abstract void setTrname(String trname);

    public abstract int ejbSelectByCount() throws FinderException;

    public int ejbHomeGetCount() {
        try {
            return ejbSelectByCount();
        } catch (FinderException ex) {
            return 0;
        }
    }

    public Collection<String> ejbHomeGetChildNames(String parent, boolean fqdn) {
        if (parent == null) {
            return new ArrayList<String>();
        }
        //System.out.println("DataModelBean::ejbHomeGetChildNames " + parent);
        Collection<String> r;
        /*
        if (!parent.startsWith(IGD) && !parent.startsWith(D)) {
        ArrayList<String> l = new ArrayList<String>();
        l.add(IGD + ".");
        l.add(D + ".");
        r = l;
        } else {
         */
        String prefix = Parameter.getRootName(parent);
        if (prefix.equals(".")) {
            prefix = "";
        }
        parent = Parameter.getNormalizedName("", parent);
        //System.out.println("DataModelBean::ejbHomeGetChildNames " + parent + " prefix=" + prefix);
        try {
            r = ejbSelectByParent(parent);
            ArrayList<String> ar = new ArrayList<String>(r.size());
            for (String n : r) {
                if (fqdn) {
                    ar.add(prefix + n);
                } else {
                    ar.add(prefix + n.substring(prefix.length() + parent.length()));
                }
            }
            r = ar;
        } catch (FinderException ex) {
            r = new ArrayList<String>();
        }
//        }
        for (String ss : r) {
            System.out.println(ss);
        }
        return r;
    }

    public DataModelLocal ejbHomeLookupByName(String name) throws FinderException {
        name = Parameter.getNormalizedName(name);
        return ejbSelectByName(name);
    }

    public abstract DataModelLocal ejbSelectByName(String name) throws FinderException;

    public abstract Collection<String> ejbSelectObjectNames(String prefix) throws FinderException;

    public String[] ejbHomeGetObjectNames() {
        try {
            Collection<String> n = ejbSelectObjectNames("%");
            String[] ns = new String[n.size()];
            Iterator<String> it = n.iterator();
            for (int i = 0; it.hasNext(); i++) {
                ns[i] = it.next();
            }
            return ns;
        } catch (FinderException ex) {
            return new String[0];
        }
    }

    public abstract Collection<String> ejbSelectByParent(String parent) throws FinderException;
}
