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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.faces.context.FacesContext;
import org.openacs.ScriptLocal;
import org.openacs.js.Script;
import org.openacs.utils.Ejb;

public class ScriptJsfBean extends JsfBeanBase {

    /** Creates a new instance of ScriptJsfBean */
    public ScriptJsfBean() {
        String name = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("scriptname");
        if (name != null) {
            this.name = name;
            try {
                ScriptLocal s = Ejb.lookupScriptBean().findByPrimaryKey(name);
                description = s.getDescription();
                text = new String(s.getScript());
            } catch (FinderException ex) {
                //Logger.getLogger(ScriptJsfBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }
    private Object[] arrayScriptNames = null;

    public Object[] getAll() throws FinderException {
//        System.out.println ("ScriptJsfBean.getAll");
        if (arrayScriptNames != null) {
            return arrayScriptNames;
        } else {
            return arrayScriptNames = Ejb.lookupScriptBean().findAll().toArray();
        }
    }
    private String description;

    public String getDescription() {
//        System.out.println ("ScriptJsfBean.getDescription");
        return description;
    }

    public void setDescription(String description) {
//        System.out.println ("ScriptJsfBean.setDescription");
        this.description = description;
    }
    private String name;

    public String getName() {
//        System.out.println ("ScriptJsfBean.getName "+name);
        return name;
    }

    public void setName(String name) {
//        System.out.println ("ScriptJsfBean.setName="+name);
        this.name = name;
        /*
        if (this.name == null || !this.name.equals(name)) {
        this.name = name;
        try {
        ScriptLocal s = Ejb.lookupScriptBean().findByPrimaryKey(name);
        description = s.getDescription();
        text = new String (s.getScript());
        } catch (FinderException ex) {
        //Logger.getLogger(ScriptJsfBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
         */
    }
    protected String text = "";

    /**
     * Get the value of text
     *
     * @return the value of text
     */
    public String getText() {
        return text;
    }

    /**
     * Set the value of text
     *
     * @param text new value of text
     */
    public void setText(String text) {
        this.text = text;
    }

    public boolean isNew() {
        return name == null || name.equals("");
    }

    public String Save() {
//        System.out.println ("ScriptJsfBean.Save () name="+name);
        try {
            String m = Script.checkSyntax(text);
            if (m != null) {
                setErrorMessage(m);
            } else {
                ScriptLocal s = Ejb.lookupScriptBean().findByPrimaryKey(name);
                s.setDescription(description);
                s.setScript(text.getBytes());
                setInfoMessage("Saved.");
            }
        } catch (FinderException ex) {
            Logger.getLogger(ScriptJsfBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String Create() {
//        System.out.println ("ScriptJsfBean.Create () name="+name);
        try {
            ScriptLocal s = Ejb.lookupScriptBean().create(name);
            s.setDescription(description);
            s.setScript(text.getBytes());
        } catch (CreateException ex) {
            Logger.getLogger(ScriptJsfBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String Delete() {
//        System.out.println ("ScriptJsfBean.Delete () name="+name);
        try {
            Ejb.lookupScriptBean().findByPrimaryKey(name).remove();
        } catch (FinderException ex) {
            Logger.getLogger(ScriptJsfBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EJBException ex) {
            Logger.getLogger(ScriptJsfBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoveException ex) {
            Logger.getLogger(ScriptJsfBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        name = text = description = null;
        return null;
    }
}
