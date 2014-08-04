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
package open.acs.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.openacs.vendors.Vendor;

public class thomsoncfg extends TestCase {

    public thomsoncfg(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    public void test1() {
        FileInputStream fin = null;
        try {
            Vendor v = Vendor.getVendor("00147F", "sss", "BANT-V");
            fin = new FileInputStream("c:/tmp/user_780.ini");
            byte[] b = new byte[fin.available()];
            fin.read(b);
            fin.close();
            String cfg = new String(b);
            String r[] = v.CheckConfig("user.ini", "badname", "badversion", cfg);
            Logger.getLogger(thomsoncfg.class.getName()).log(Level.SEVERE, null, r);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(thomsoncfg.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(thomsoncfg.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fin.close();
            } catch (IOException ex) {
                Logger.getLogger(thomsoncfg.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }
}
