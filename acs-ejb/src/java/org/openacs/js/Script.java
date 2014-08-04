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

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.FinderException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.openacs.HostsLocal;
import org.openacs.ICpe;
import org.openacs.ScriptLocal;
import org.openacs.message.Inform;
import org.openacs.message.TransferComplete;
import org.openacs.utils.Ejb;

public class Script extends Thread {

    private Inform lastInform;
    private String script;
    private HostsLocal host;
    private TransferComplete transferComplete;
    private ICpe cpe;
    private String sessionid;

    public Script(Inform lastInform, String script, HostsLocal host, TransferComplete transferComplete, ICpe cpe, String sessionid) {
        this.lastInform = lastInform;
        this.script = script;
        this.host = host;
        this.transferComplete = transferComplete;
        this.cpe = cpe;
        this.sessionid = sessionid;
    }
    private Logger logger = Logger.getLogger(Script.class.getName());

    private void log(Level level, String msg) {
        logger.log(level, lastInform.getOui() + ":" + lastInform.sn + " " + msg);
    }

    private void log(Level level, String msg, Throwable ex) {
        logger.log(level, lastInform.getOui() + ":" + lastInform.sn + " " + msg, ex);
    }

    private static Level getLevel(String l) {
        if (l.compareToIgnoreCase("severe") == 0) {
            return Level.SEVERE;
        }
        if (l.compareToIgnoreCase("warning") == 0) {
            return Level.WARNING;
        }
        //if (l.compareToIgnoreCase("info")==0) return Level.INFO;
        return Level.INFO;
    }

    public static Object logger(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        //System.out.println("logger with "+args.length+" parameters");
        String msg = "";
        Level level;
        switch (args.length) {
            case 0:
                level = Level.WARNING;
                msg = "logger called with no severity and message";
                break;
            case 1:
                level = Level.INFO;
                msg = args[0].toString();
                break;
            case 2:
                level = getLevel((String) args[0]);
                msg = args[1].toString();
                break;
            default:
                level = getLevel((String) args[0]);
                for (Object m : args) {
                    msg += m.toString();
                }
                msg = args[0].toString();
                break;
        }
        Logger.getLogger(Script.class.getName()).log(level, msg);
        //((Script)thisObj).log (level, msg);
        return null;
    }

    public static Object call(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        if (args.length < 1) {
            ((Script) thisObj).log(Level.WARNING, "call function with zero args");
            return null;
        }
        ScriptLocal script;
        try {
            script = Ejb.lookupScriptBean((String) args[0]);
            /*
            Object [] newargs = null;
            if (args.length > 1) {
            newargs = new Object[args.length - 1];
            System.arraycopy(args, 1, newargs, 0, args.length - 1);
            Scriptable a = cx.newArray(thisObj, newargs);
            thisObj.put("arguments", thisObj, newargs);
            }
             */
            return cx.evaluateString(thisObj, new String(script.getScript()), (String) args[0], 1, null);
        } catch (FinderException ex) {
            ((Script) thisObj).log(Level.SEVERE, "CALL: function " + args[0] + " not found.");
        }
        return null;
    }

    private void exportFunction(Scriptable scope, String name) {
        try {
            Method m = Script.class.getMethod(name, Context.class, Scriptable.class, Object[].class, Function.class);
            scope.put(name, scope, new FunctionObject(name, m, scope));
        } catch (NoSuchMethodException ex) {
            log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
//        System.out.println ("Script::Run");
        Context cx = Context.enter();
        try {
            Scriptable scope = cx.initStandardObjects();
            scope.put("sessionid", scope, sessionid);

            ScriptableObject.defineClass(scope, Cpe.class);
            ScriptableObject.defineClass(scope, CpeDb.class);
            ScriptableObject.defineClass(scope, Db.class);
            Object[] arg = {lastInform, host, transferComplete, cpe};
            scope.put("cpe", scope, (Cpe) cx.newObject(scope, "Cpe", arg));

            arg = new Object[1];
            arg[0] = host;
            CpeDb cpedb = (CpeDb) cx.newObject(scope, "CpeDb", arg);
            scope.put("cpedb", scope, cpedb);

            Db db = (Db) cx.newObject(scope, "Db", arg);
            scope.put("db", scope, db);

            exportFunction(scope, "call");
            exportFunction(scope, "logger");

            org.mozilla.javascript.Script scr = cx.compileString(script, "skriptas", 0, null);
            Object result = scr.exec(cx, scope);
            //Object result = cx.evaluateString(scope, script, "<cmd>", 1, null);
            String resultString = Context.toString(result);
            if (resultString != null) {
                // cpedb.setScriptResult (resultString);
            }
//            System.err.println("Returned: " + Context.toString(result));
        } catch (RhinoException e) {
            log(Level.SEVERE, "Run exception: " + e.getMessage() + " line: " + e.lineNumber() + " column: " + e.columnNumber() + "\n" + e.getScriptStackTrace());
        } catch (Exception e) {
            log(Level.SEVERE, "Run exception: " + e.getMessage());
        } finally {
            Context.exit();
        }

    }

    public static String checkSyntax(String script) {
        Context cx = Context.enter();
        try {
            cx.compileString(script, "", 0, null);
        } catch (RhinoException e) {
            return e.getMessage() + " line: " + e.lineNumber() + " column: " + e.columnNumber();
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            Context.exit();
        }

        return null;
    }
}
