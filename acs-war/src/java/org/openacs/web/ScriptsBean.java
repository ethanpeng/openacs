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

import java.util.Iterator;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import org.openacs.ScriptLocalHome;
import org.openacs.utils.Ejb;
import org.richfaces.model.ScrollableTableDataModel.SimpleRowKey;
import org.richfaces.model.selection.Selection;
import org.openacs.ScriptLocal;

public class ScriptsBean {

    /**
     * Creates a new instance of ScriptsBean
     */
    public ScriptsBean() {
    }
    /**
     * Getter for property allHosts.
     * @return Value of property allHosts.
     */
    private Object[] arrayScriptNames = null;

    public Object[] getAll() throws FinderException {
        if (arrayScriptNames != null) {
            return arrayScriptNames;
        } else {
            return arrayScriptNames = Ejb.lookupScriptBean().findAll().toArray();
        }
    }

    public String deleteItem() {
        if (selection != null && selection.size() > 0) {
            Iterator k = selection.getKeys();
            while (k.hasNext()) {
                SimpleRowKey rk = (SimpleRowKey) k.next();
                ScriptLocal sw = (ScriptLocal) arrayScriptNames[rk.intValue()];
                try {
                    sw.remove();
                } catch (EJBException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                } catch (RemoveException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                }
            }
        }
        arrayScriptNames = null; // force reload
        edit = true;
        return null;
    }
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    private String text;

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
    private String description;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Holds value of property edit.
     */
    private boolean edit = true;

    /**
     * Getter for property edit.
     * @return Value of property edit.
     */
    public boolean isEdit() {
        return this.edit;
    }

    /**
     * Setter for property edit.
     * @param edit New value of property edit.
     */
    public void setEdit(boolean edit) {
        this.edit = edit;
        if (edit) {
            prepareEdit();
        } else {
            prepareNew();
        }
    }

    public String prepareNew() {
        edit = false;
        name = "";
        text = "";
        description = "";
        return null;
    }

    public String prepareEdit() {
        edit = true;
        //System.out.println ("SCRIPTS: prepare edit");
        if (selection != null && selection.size() == 1) {
            SimpleRowKey rk = (SimpleRowKey) selection.getKeys().next();
            ScriptLocal script = (ScriptLocal) arrayScriptNames[rk.intValue()];
            name = script.getName();
            text = new String(script.getScript());
            description = script.getDescription();
            //System.out.println ("SCRIPTS: prepare edit, "+name);
        }

        return null;
    }
    /**
     * Holds value of property selection.
     */
    private Selection selection;

    /**
     * Getter for property selection.
     * @return Value of property selection.
     */
    public Selection getSelection() {
        return this.selection;
    }

    /**
     * Setter for property selection.
     * @param selection New value of property selection.
     */
    public void setSelection(Selection selection) {
        this.selection = selection;
        //System.out.println ("SCRIPTS: setSelection1");
        if (selection != null && selection.size() == 1) {
            SimpleRowKey rk = (SimpleRowKey) selection.getKeys().next();
            ScriptLocal script = (ScriptLocal) arrayScriptNames[rk.intValue()];
            name = script.getName();
            text = new String(script.getScript());
            description = script.getDescription();
            //System.out.println ("SCRIPTS: setSelection2, "+name);
            edit = true;
        }
    }

    public String editItem() {
        //System.out.println ("editItem: eidt="+edit);
        ScriptLocalHome h = Ejb.lookupScriptBean();
        if (edit) {
            try {
                ScriptLocal script = h.findByPrimaryKey(name);
                script.setScript(text.getBytes());
                script.setDescription(description);
            } catch (FinderException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                ScriptLocal script = h.create(name);
                script.setScript(text.getBytes());
                script.setDescription(description);
            } catch (CreateException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
        arrayScriptNames = null; // force reload
        edit = true;
        return null;
    }
}
