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

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.UniqueTag;
import org.openacs.HostsLocal;
import org.openacs.ICpe;
import org.openacs.Message;
import org.openacs.message.*;
import org.openacs.message.Fault.SetParameterValueFault;
import org.openacs.message.GetOptionsResponse.OptionStruct;

public class Cpe extends ScriptableObject {

    private Inform lastInform;
    private ICpe cpe;
    private String lastCommandKey = "";
    private HostsLocal host;
    private TransferComplete transferComplete;

    public Cpe() {
    }

    public Cpe(Context cx, Function ctor, Object[] args) {
//        System.out.println ("Construct: "+lastInform);
        this.lastInform = (Inform) args[0];
        this.host = (HostsLocal) args[1];
        if (args.length > 2 && args[2] != null) {
            transferComplete = (TransferComplete) args[2];
            put("TransferComplete", this, ExportTransferComplete(cx, ctor));

        }
//        cpe = Ejb.lookupCPEBean();
        cpe = (ICpe) args[3];
        put("Inform", this, ExportInform(cx, ctor));
    }

    @Override
    public String getClassName() {
        return "Cpe";
    }

    private Message Call(Message _msg) {
        //msg =  cpe.Call (oui, sn, msg);
        long timeout = 0;
        Object to = getProperty(this, "timeout");
        //System.out.println ("TIMEOUT to = "+to +" "+to.getClass().getName());
        if (to != null && to != NOT_FOUND) {
            timeout = Object2Int(to);
        }

        Message msg = cpe.Call(host, _msg, timeout);
        if (msg == null) {
            Context cx = Context.getCurrentContext();
            Scriptable tc = cx.newObject(this);
            tc.put("message", tc, "request timed out");
            throw new JavaScriptException(tc);
        } else if (msg.isFault()) {
            Fault fault = (Fault) msg;
            Context cx = Context.getCurrentContext();
            Scriptable tc = cx.newObject(this);
            tc.put(Message.FAULT_STRING, tc, fault.getFaultString());
            tc.put("CwmpFaultString", tc, fault.getFaultStringCwmp());
            tc.put(Message.FAULT_CODE, tc, fault.getFaultCode());
            tc.put("CwmpFaultCode", tc, fault.getCwmpFaultCode());
            tc.put("message", tc, "Fault in " + _msg.getName());

            if (fault.SetParameterValuesFaults != null) {
                Scriptable pfs = cx.newArray(this, fault.SetParameterValuesFaults.size());
                int ix = 0;
                for (SetParameterValueFault f : fault.SetParameterValuesFaults) {
                    Scriptable pf = cx.newObject(pfs);
                    pf.put("ParameterName", pf, f.ParameterName);
                    pf.put(Message.FAULT_CODE, pf, f.FaultCode);
                    pf.put(Message.FAULT_STRING, pf, f.FaultString);
                    pfs.put(ix++, pfs, pf);
                }
                tc.put("SetParameterValuesFault", tc, pfs);
            }

            /*
            19:55:23,593 INFO  [STDOUT] jsFunction_log: Exception prop: fileName = skriptas
            19:55:23,593 INFO  [STDOUT] jsFunction_log: Exception prop: rhinoException = org.mozilla.javascript.WrappedException: Wrapped java.l
            ang.RuntimeException: CWMP faultInvalid arguments (skriptas#18)
            19:55:23,593 INFO  [STDOUT] jsFunction_log: Exception prop: message = java.lang.RuntimeException: CWMP faultInvalid arguments
            19:55:23,609 INFO  [STDOUT] jsFunction_log: Exception prop: javaException = java.lang.RuntimeException: CWMP faultInvalid arguments
            19:55:23,609 INFO  [STDOUT] jsFunction_log: Exception prop: name = JavaException
            19:55:23,609 INFO  [STDOUT] jsFunction_log: Exception prop: lineNumber = 18
             */
            JavaScriptException ex = new JavaScriptException(tc);
            throw ex;
//            throw new RuntimeException(fault.getFaultString () +fault.getFaultStringCwmp ());
        }
        return msg;
    }

    private Scriptable ExportTransferComplete(Context cx, Scriptable top) {
        Scriptable tc = cx.newObject(top);
        tc.put("CommandKey", tc, transferComplete.CommandKey);
        tc.put("CompleteTime", tc, transferComplete.CompleteTime);
        tc.put("FaultCode", tc, transferComplete.FaultCode);
        tc.put("FaultString", tc, transferComplete.FaultString);
        tc.put("StartTime", tc, transferComplete.StartTime);

        return tc;
    }

    private Scriptable ExportInform(Context cx, Scriptable top) {
        Scriptable io = cx.newObject(top);

        Scriptable d = cx.newObject(io);
        d.put("Manufacturer", d, lastInform.Manufacturer);
        d.put("OUI", d, lastInform.getOui());
        d.put("ProductClass", d, lastInform.ProductClass);
        d.put("SerialNumber", d, lastInform.sn);
        io.put("DeviceId", io, d);

        io.put("MaxEnvelopes", io, lastInform.MaxEnvelopes);
        io.put("RetryCount", io, lastInform.RetryCount);
        io.put("CurrentTime", io, lastInform.CurrentTime);

        Scriptable e = cx.newArray(io, lastInform.getEvents().size());
        int i = 0;
        for (Entry<String, String> ev : lastInform.getEvents()) {
            Scriptable event = cx.newObject(io);
            event.put("EventCode", event, ev.getKey());
            event.put("CommandKey", event, ev.getValue());
            e.put(i++, e, event);
        }
        io.put("Event", io, e);

        Scriptable pl = cx.newArray(io, lastInform.params.size());
        i = 0;
        for (String p : lastInform.params.keySet()) {
            Scriptable param = cx.newObject(io);
            param.put("Name", param, p);
            param.put("Value", param, lastInform.params.get(p));
            pl.put(i++, pl, param);
        }
        io.put("ParameterList", io, pl);

        return io;
    }

    public static Scriptable jsConstructor(Context cx, Object[] args, Function ctorObj, boolean inNewExpr) {
        return new Cpe(cx, ctorObj, args);
    }
    /*
    public String jsGet_sn() {
    return lastInform.sn;
    }
    public Scriptable jsGet_ssnn() {
    return inf;
    }
    public String jsGet_inform() {
    System.out.println ("jsGet_Inform()");
    return lastInform.sn;
    //        return ExportInform ();
    }
     */

    public static int jsFunction_SetParameterValues(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        SetParameterValues msg_in = new SetParameterValues();

        Scriptable params = (Scriptable) args[0];
        if (args.length > 1) {
            msg_in.key = (String) args[1];
        }
        //      int l = (int)params.getLength();
        int l = (int) params.getIds().length;
        for (int i = 0; i < l; i++) {
            Scriptable nv = (Scriptable) params.get(i, params);
            String n = (String) nv.get("name", nv);
            Object ov = nv.get("value", nv);
            String v = ov.toString();
            Object ot = nv.get("type", nv);
            if (ot.equals(UniqueTag.NOT_FOUND)) {
                msg_in.AddValue(n, v);
            } else {
                msg_in.AddValue(n, v, (String) ot);
            }
        }
        SetParameterValuesResponse msg_out = (SetParameterValuesResponse) _this.Call(msg_in);
        return msg_out.Status;
    }

    public static /*NativeArray*/ Scriptable jsFunction_GetRPCMethods(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        GetRPCMethods msg_in = new GetRPCMethods();
        GetRPCMethodsResponse msg_out = (GetRPCMethodsResponse) _this.Call(msg_in);
//        NativeArray ra = new NativeArray (msg_out.methods.length);
        Scriptable ra = cx.newArray(thisObj, msg_out.methods.length);
        for (int i = 0; i < msg_out.methods.length; i++) {
            ra.put(i, ra, msg_out.methods[i]);
        }
        return ra;
    }

    public static Scriptable /*NativeArray*/ jsFunction_GetParameterValues(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        GetParameterValues msg_in = new GetParameterValues();
        //NativeArray parameterNames = (NativeArray)args [0];
        Scriptable parameterNames = (Scriptable) args[0];
        //int l = (int)parameterNames.getLength();
        int l = (int) parameterNames.getIds().length;
        msg_in.parameterNames = new String[l];
        for (int i = 0; i < l; i++) {
            msg_in.parameterNames[i] = (String) parameterNames.get(i, parameterNames);
        }
        GetParameterValuesResponse msg_out = (GetParameterValuesResponse) _this.Call(msg_in);
        return toNativeArray(cx, thisObj, msg_out.values.entrySet(), "name", "value");
    }
    /*
    private static String [] toStringArray (NativeArray na) {
    int l = (int)na.getLength();
    String [] sa = new String [l];
    for (int i = 0; i < l; i++) {
    sa [i] = (String) na.get(i, na);
    }
    return sa;
    }
     */

    private static String[] toStringArray(Scriptable na) {
        int l = (int) na.getIds().length;
        String[] sa = new String[l];
        for (int i = 0; i < l; i++) {
            sa[i] = (String) na.get(i, na);
        }
        return sa;
    }

    public static Scriptable /*NativeObject*/ jsFunction_AddObject(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        AddObject msg_in = new AddObject();
        msg_in.ObjectName = (String) args[0];
        msg_in.ParameterKey = (String) args[1];

        AddObjectResponse msg_out = (AddObjectResponse) _this.Call(msg_in);
        //      System.out.println ("Add: "+msg_out.InstanceNumber+" Status="+msg_out.Status);
//        NativeObject ro = new NativeObject();
        Scriptable ro = cx.newObject(thisObj);
        ro.put("Status", ro, msg_out.Status);
        ro.put("InstanceNumber", ro, msg_out.InstanceNumber);
        return ro;
    }

    public static int jsFunction_DeleteObject(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
//        System.out.println ("Delete: "+args[0]+" key="+args[1]);
        Cpe _this = (Cpe) thisObj;
        DeleteObject msg_in = new DeleteObject();
        msg_in.ObjectName = (String) args[0];
        msg_in.ParameterKey = (String) args[1];

        DeleteObjectResponse msg_out = (DeleteObjectResponse) _this.Call(msg_in);
        return msg_out.Status;
    }

    private static String paramString(Object p, String def) {
        return (p != null) ? (String) p : def;
    }

    public static void jsFunction_Reboot(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        _this.Call(new Reboot(paramString(args[0], "cmdReboot")));
    }

    public static void jsFunction_FactoryReset(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        _this.Call(new FactoryReset());
    }

    public static void jsFunction_SetParameterAttributes(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        //NativeArray attrs = (NativeArray)args[0];
        Scriptable attrs = (Scriptable) args[0];
        SetParameterAttributes msg_in = new SetParameterAttributes();
//        int c = (int)attrs.getLength();
        int c = (int) attrs.getIds().length;
        for (int i = 0; i < c; i++) {
//            NativeObject o = (NativeObject)attrs.get(i, attrs);
            Scriptable o = (Scriptable) attrs.get(i, attrs);
//            NativeArray acl = (NativeArray)o.get("AccessList", o);
            Scriptable acl = (Scriptable) o.get("AccessList", o);
            String[] sacl = toStringArray(acl);

            String Name = (String) o.get("Name", o);
            boolean NotificationChange = (Boolean) o.get("NotificationChange", o);
//            int Notification = ((Double) o.get("Notification", o)).intValue();
            int Notification = Object2Int(o.get("Notification", o));
            boolean AccessListChange = (Boolean) o.get("AccessListChange", o);
            msg_in.AddAttribute(Name, NotificationChange, Notification, AccessListChange, sacl);
        }

        //SetParameterAttributesResponse msg_out = (SetParameterAttributesResponse) 
        _this.Call(msg_in);
    }

    /*
    private static NativeArray toNativeArray (Context cx, Scriptable scriptable, Set <Entry <String, String>> s, String n, String k) {
    
    NativeArray ra = new NativeArray (s.size());
    Iterator <Entry <String, String>> it = s.iterator();
    
    int i = 0;
    while (it.hasNext()) {
    Entry <String, String> e = it.next();
    //NativeObject o = new NativeObject();
    Scriptable o = cx.newObject(scriptable);
    o.put(n, o, e.getKey());
    o.put(k, o, e.getValue());
    ra.put(i++, ra, o);
    }
    return ra;
    }
     */
    private static Scriptable toNativeArray(Context cx, Scriptable scriptable, Set<Entry<String, String>> s, String n, String k) {

        Scriptable ra = cx.newArray(scriptable, s.size());
        Iterator<Entry<String, String>> it = s.iterator();

        int i = 0;
        while (it.hasNext()) {
            Entry<String, String> e = it.next();
            //NativeObject o = new NativeObject();
            Scriptable o = cx.newObject(scriptable);
            o.put(n, o, e.getKey());
            o.put(k, o, e.getValue());
            ra.put(i++, ra, o);
        }
        return ra;
    }

    public static Scriptable /*NativeArray*/ jsFunction_GetParameterNames(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        GetParameterNames msg_in = new GetParameterNames();
        msg_in.parameterPath = (String) args[0];
        msg_in.nextLevel = (Boolean) args[1];
        //System.out.println ("nextlevel "+args[1].getClass().getName());

        GetParameterNamesResponse msg_out = (GetParameterNamesResponse) _this.Call(msg_in);
        return toNativeArray(cx, thisObj, msg_out.names.entrySet(), "name", "writable");

    }

    public static Scriptable /*NativeArray*/ jsFunction_GetParameterAttributes(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        GetParameterAttributes msg_in = new GetParameterAttributes();
//        NativeArray parameterNames = (NativeArray)args [0];
//        int l = (int)parameterNames.getLength();
        Scriptable parameterNames = (Scriptable) args[0];
        int l = (int) parameterNames.getIds().length;
        msg_in.parameterNames = new String[l];
        for (int i = 0; i < l; i++) {
            msg_in.parameterNames[i] = (String) parameterNames.get(i, parameterNames);
        }
        GetParameterAttributesResponse msg_out = (GetParameterAttributesResponse) _this.Call(msg_in);

//        NativeArray ra = new NativeArray (msg_out.attributes.length);
        Scriptable ra = cx.newArray(thisObj, msg_out.attributes.length);
        for (int i = 0; i < msg_out.attributes.length; i++) {
//            NativeArray acl = new NativeArray (msg_out.attributes[i].AccessList);
            Object[] acla = new Object[msg_out.attributes[i].AccessList.length];
            for (int aclix = 0; aclix < msg_out.attributes[i].AccessList.length; aclix++) {
                acla[aclix] = msg_out.attributes[i].AccessList[aclix];
            }
            Scriptable acl = cx.newArray(thisObj, acla);
//            NativeObject attr = new NativeObject();
            Scriptable attr = cx.newObject(thisObj);
            attr.put("Name", attr, msg_out.attributes[i].Name);
            attr.put("Notification", attr, msg_out.attributes[i].Notification);
            attr.put("AccessList", attr, acl);
            ra.put(i, ra, attr);
        }
        return ra;
    }

    public static void jsFunction_log(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        if (args.length > 0 && args[0] != Context.getUndefinedValue()) {
            System.out.println("jsFunction_log: " + Context.toString(args[0]));
        }
    }

    public static Scriptable jsFunction_Download(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        Download msg_in = new Download();
        /*        for (Object a : args) {
        System.out.println ("arg="+a.getClass().getName()+" value= "+a);
        }*/
        msg_in.CommandKey = _this.lastCommandKey = (String) args[0];
        msg_in.FileType = (String) args[1];
        msg_in.url = (String) args[2];
        msg_in.UserName = (String) args[3];
        msg_in.Password = (String) args[4];
//        msg_in.FileSize = Long.parseLong((String)args[5]);
        msg_in.FileSize = Object2Int(args[5]);
        msg_in.TargetFileName = (String) args[6];
        if (args.length >= 8) {
            msg_in.DelaySeconds = Object2Int(args[7]);
        }
        if (args.length >= 9) {
            msg_in.SuccessUrl = (String) args[8];
        }
        if (args.length >= 10) {
            msg_in.FailureUrl = (String) args[9];
        }
        DownloadResponse msg_out = (DownloadResponse) _this.Call(msg_in);
        Scriptable r = cx.newObject(thisObj);

        r.put("StartTime", r, msg_out.StartTime);
        r.put("CompleteTime", r, msg_out.CompleteTime);
        r.put("Status", r, msg_out.Status);
        return r;
    }
    /*
    public static Scriptable jsFunction_WaitForTransferComplete(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    Cpe _this = (Cpe) thisObj;
    System.out.println("Wait for trasnfercomplete cmd=" + _this.lastCommandKey);
    if (_this.lastCommandKey.equals("")) {
    return null;
    }
    long timeout = 10 * 1000;
    if (args.length > 0) {
    timeout = (Integer) args[0] * 1000;
    }
    try {
    TransferComplete tc = (TransferComplete) _this.cpe.WaitJmsReply("JMSCorrelationID='" + _this.lastCommandKey + "'", timeout);
    Scriptable ro = cx.newObject(thisObj);
    ro.put("CompleteTime", ro, tc.CompleteTime);
    ro.put("StartTime", ro, tc.StartTime);
    ro.put("FaultCode", ro, tc.FaultCode);
    ro.put("FaultString", ro, tc.FaultString);
    return ro;
    } catch (JMSException ex) {
    Logger.getLogger(Cpe.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
    _this.lastCommandKey = "";
    }
    return null;
    }
     */

    private static int Object2Int(Object o) {
        if (o == null) {
            return 0;
        }
        String c = o.getClass().getName();
        if (c.equals("java.lang.Double")) {
            return ((Double) o).intValue();
        }
        if (c.equals("java.lang.Integer")) {
            return (Integer) o;
        }
        throw new RuntimeException("Failed to convert object to int");
    }

    public static Scriptable jsFunction_Upload(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        Upload msg_in = new Upload();

        msg_in.CommandKey = _this.lastCommandKey = (String) args[0];
        msg_in.FileType = (String) args[1];
        msg_in.URL = (String) args[2];
        msg_in.Username = (String) args[3];
        msg_in.Password = (String) args[4];
        msg_in.DelaySeconds = Object2Int(args[5]);

        UploadResponse msg_out = (UploadResponse) _this.Call(msg_in);
        Scriptable r = cx.newObject(thisObj);

        r.put("StartTime", r, msg_out.StartTime);
        r.put("CompleteTime", r, msg_out.CompleteTime);
        r.put("Status", r, msg_out.Status);
        return r;

    }

    public static void jsFunction_ScheduleInform(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        ScheduleInform msg_in = new ScheduleInform();

        msg_in.DelaySeconds = Object2Int(args[0]);
        msg_in.CommandKey = _this.lastCommandKey = (String) args[1];

        _this.Call(msg_in);
    }

    public static Scriptable jsFunction_GetOptions(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        GetOptions msg_in = new GetOptions();
        msg_in.OptionName = (String) args[0];
        GetOptionsResponse msg_out = (GetOptionsResponse) _this.Call(msg_in);

        Scriptable ra = cx.newArray(thisObj, msg_out.OptionList.size());
        Iterator<OptionStruct> it = msg_out.OptionList.iterator();

        int i = 0;
        while (it.hasNext()) {
            OptionStruct e = it.next();
            Scriptable o = cx.newObject(thisObj);
            o.put("OptionName", o, e.OptionName);
            o.put("VoucherSN", o, e.VoucherSN);
            o.put("State", o, e.State);
            o.put("Mode", o, e.Mode);
            o.put("StartDate", o, e.StartDate);
            o.put("ExpirationDate", o, e.ExpirationDate);
            o.put("IsTransferable", o, e.IsTransferable);
            ra.put(i++, ra, o);
        }
        return ra;

    }

    public static Scriptable jsFunction_X_00000C_ShowStatus(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        X_00000C_ShowStatus msg_in = new X_00000C_ShowStatus();

        Scriptable cmds = (Scriptable) args[0];
        int l = (int) cmds.getIds().length;
        for (int i = 0; i < l; i++) {
            msg_in.addCommand((String) cmds.get(i, cmds));
        }
        X_00000C_ShowStatusResponse msg_out = (X_00000C_ShowStatusResponse) _this.Call(msg_in);

        Scriptable ra = cx.newArray(thisObj, msg_out.response.size());
        Iterator<Entry<String, String>> i = msg_out.response.entrySet().iterator();
        int ix = 0;
        while (i.hasNext()) {
            Entry<String, String> e = i.next();
            Scriptable o = cx.newObject(thisObj);
            o.put("Command", o, e.getKey());
            o.put("Response", o, e.getValue());
            ra.put(ix++, ra, o);
        }
        return ra;

    }

    public static Scriptable jsFunction_RGCommand(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        return jsFunction_X_JUNGO_COM_RGCommand(cx, thisObj, args, funObj);
    }

    public static Scriptable jsFunction_X_JUNGO_COM_RGCommand(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        X_JUNGO_COM_RGCommand msg_in = new X_JUNGO_COM_RGCommand((String) args[0]);
        X_JUNGO_COM_RGCommandResponse msg_out = (X_JUNGO_COM_RGCommandResponse) _this.Call(msg_in);
        Scriptable o = cx.newObject(thisObj);
        o.put("Result", o, msg_out.getResult());
        o.put("Status", o, msg_out.getStatus());
        return o;
    }

    public static Scriptable jsFunction_CiscoShowStatus(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        return jsFunction_X_00000C_ShowStatus(cx, thisObj, args, funObj);
    }

    public static void jsFunction_BackupCWMPTree(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        _this.cpe.BackupCWMPTree();
    }

    public static void jsFunction_SaveDSLStats(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        _this.cpe.SaveDSLStats();
    }

    public static void jsFunction_SaveATMErrorsStats(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        _this.cpe.SaveATMErrorsStats();
    }

    public static void jsFunction_SyncParameterValues(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Cpe _this = (Cpe) thisObj;
        Scriptable vars = (Scriptable) args[0];
        int l = (int) vars.getIds().length;
        Properties p = new Properties();
        for (int i = 0; i < l; i++) {
            Object o = vars.get(i, vars);
            if (o instanceof String) {
                String[] sa = ((String) o).split("=");
                p.setProperty(sa[0], sa[1]);
            } else if (o instanceof Scriptable) {
                Scriptable v = (Scriptable) o;
                p.setProperty((String) v.get("Name", v), (String) v.get("Value", v));
            }
        }
        _this.cpe.SyncParameterValues(p);
    }
}
