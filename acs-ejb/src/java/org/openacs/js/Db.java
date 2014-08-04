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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Db extends ScriptableObject {

    private static final String DEFAULTDS = "java:ACS";

    public Db() {
    }

    @Override
    public String getClassName() {
        return "Db";
    }

    public static Scriptable jsConstructor(Context cx, Object[] args, Function ctorObj, boolean inNewExpr) {
        return new Db();
    }

    private static void ThrowException(Scriptable thisObj, String msg) {
        Scriptable tc = Context.getCurrentContext().newObject(thisObj);
        tc.put("message", tc, msg);
        throw new JavaScriptException(tc);
    }

    private static void ThrowException(Scriptable thisObj, Exception ex) {
        ThrowException(thisObj, ex.getMessage());
    }

    public static Scriptable jsFunction_Query(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        try {
            InitialContext c = new InitialContext();
            if (args.length < 1) {
                return null;
            }
            Object o = c.lookup((args.length == 2) ? (String) args[0] : DEFAULTDS);
            String q = (args.length == 2) ? (String) args[1] : (String) args[0];
            Scriptable res = null;
            if (o != null) {
                DataSource ds = (DataSource) o;
                Connection conn = null;
                try {
                    conn = ds.getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(q);
                    System.out.println("Column count = " + rs.getMetaData().getColumnCount());

                    int cols = rs.getMetaData().getColumnCount();
                    String colnames[] = new String[cols];
                    for (int col = 1; col <= cols; col++) {
                        colnames[col - 1] = rs.getMetaData().getColumnName(col);
                        System.out.println("Column name = " + colnames[col - 1]);
                    }
                    ArrayList<Scriptable> resal = new ArrayList<Scriptable>();
                    while (rs.next()) {
                        Scriptable r = cx.newObject(thisObj);
                        for (int col = 1; col <= cols; col++) {
                            r.put(colnames[col - 1], r, rs.getString(col));
                        }
                        resal.add(r);
                    }
                    res = cx.newArray(thisObj, resal.toArray());
                } catch (SQLException e) {
                    ThrowException(thisObj, e);
                } catch (Exception e) {
                    System.out.println(e);
                    ThrowException(thisObj, e);
                } finally {
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException ex) {
                        }
                    }
                }
                return res;
            } else {
                ThrowException(thisObj, "Data source no found");
            }
        } catch (NamingException e) {
            ThrowException(thisObj, e);
        }
        return null;
    }

    public static int jsFunction_Update(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        try {
            InitialContext c = new InitialContext();
            int res = -1;
            if (args.length < 1) {
                return res;
            }
            Object o = c.lookup((args.length == 2) ? (String) args[0] : DEFAULTDS);
            String q = (args.length == 2) ? (String) args[1] : (String) args[0];

            if (o != null) {
                DataSource ds = (DataSource) o;
                Connection conn = null;
                try {
                    conn = ds.getConnection();
                    Statement stmt = conn.createStatement();
                    res = stmt.executeUpdate(q);
                } catch (SQLException e) {
                    ThrowException(thisObj, e);
                } catch (Exception e) {
                    System.out.println(e);
                    ThrowException(thisObj, e);
                } finally {
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException ex) {
                        }
                    }
                }
                return res;
            } else {
                ThrowException(thisObj, "Data source no found");
            }
        } catch (NamingException e) {
            ThrowException(thisObj, e);
        }
        return -1;
    }

    public void setScriptResult(String result) {
    }
}
