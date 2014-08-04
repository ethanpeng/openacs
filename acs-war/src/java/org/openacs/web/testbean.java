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

import java.util.Map.Entry;
import java.util.Properties;
import org.openacs.web.tags.Form;

public class testbean {

    private Form form;
    private Properties data;

    public testbean() {
        System.out.println("testbean::testbean");
        //fld = new cwmpvar();
    }

    public Form getForm() {
        System.out.println("testbean::getForm");
        return form;
    }

    public void setForm(Form form) {
        System.out.println("testbean::setForm " + form);
        this.form = form;
        if (form.getSource() == null) {
            form.setSource(getData());
        }
    }

    public void setStr(String s) {
        System.out.println("testbean::setStr " + s);
    }

    public String getStr() {
        System.out.println("testbean::getStr ");
        return "STR";
    }

    public String act() {
        System.out.println("testbean::act");
        Properties p = new Properties();
        for (Entry<Object, Object> e : p.entrySet()) {
            System.out.println("P1: " + e.getKey() + " -> " + e.getValue());
        }
        form.mergeValues(p);
        for (Entry<Object, Object> e : p.entrySet()) {
            System.out.println("P2: " + e.getKey() + " -> " + e.getValue());
        }
        return null;
    }

    public Properties getData() {
        System.out.println("testbean::getData " + data);
        if (data == null) {
            data = new Properties();/*
            data.put(".UDPEchoConfig.Enable", "1");
            data.put(".Time.DaylightSavingsEnd", "2011-03-11");
            data.put (".DownloadDiagnostics.EthernetPriority", "123");
            data.put (".CaptivePortal.CaptivePortalURL", "http://aaaaa");
             * */
        }
        return data;
    }

    public void setData(Properties p) {
        System.out.println("testbean::setData " + data);
    }
}
