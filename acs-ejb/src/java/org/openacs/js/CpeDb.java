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
package org.openacs.js;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.openacs.HostsLocal;

public class CpeDb extends ScriptableObject {

    private HostsLocal host;

    public CpeDb() {
    }

    public CpeDb(Context cx, Function ctor, HostsLocal host) throws IOException {
        this.host = host;
        Properties p = new Properties();
        byte[] bp = host.getProps();
        if (bp != null) {
            p.load(new ByteArrayInputStream(bp));
            if (p != null) {
                for (Entry e : p.entrySet()) {
                    String k = (String) e.getKey();
                    k.replace('.', '_');
                    //System.out.println ("PROPSAS: "+e.getKey()+"="+e.getValue());
                    put(k, this, e.getValue());
                }
            }
        }
    }

    @Override
    public String getClassName() {
        return "CpeDb";
    }

    public static Scriptable jsConstructor(Context cx, Object[] args, Function ctorObj, boolean inNewExpr) {
        try {
            return new CpeDb(cx, ctorObj, (HostsLocal) args[0]);
        } catch (IOException ex) {
            Logger.getLogger(CpeDb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void jsFunction_Save(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        CpeDb _this = (CpeDb) thisObj;
        Object[] ids = _this.getIds();
        /*
        Properties p = new Properties ();
        for (Object oid : ids) {
        String id = (String)oid;
        p.put(id, (String)CpeDb.getProperty(thisObj, id));
        }
        _this.host.setProps(p);
         */
        String props = "";
        for (Object oid : ids) {
            String id = (String) oid;
            props += id + "=" + (String) CpeDb.getProperty(thisObj, id) + "\n";
        }
        _this.host.setProps(props.getBytes());
    }

    public void setScriptResult(String result) {
    }
}
