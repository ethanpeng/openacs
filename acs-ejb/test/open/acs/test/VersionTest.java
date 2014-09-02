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

import junit.framework.*;
import org.openacs.utils.Version;

public class VersionTest extends TestCase {

    public VersionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testSet() {
        System.out.println("Set");

        String sv = "1.2.3.4.5";
        Version instance = new Version(sv);
//        fail("The test case is a prototype.");
        instance = new Version("");
    }

    public void testIsUptodate() {
        System.out.println("isUptodate");

        Version instance = new Version("2.0.3.0");

        assertEquals(true, instance.isUptodate(new Version("2.0.3.0")));
        assertEquals(true, instance.isUptodate(new Version("2.0.4.0")));
        assertEquals(false, instance.isUptodate(new Version("2.0.2.0")));
        assertEquals(true, instance.isUptodate(new Version("2.0.3.0.1")));
        assertEquals(false, instance.isUptodate(new Version("2.0.3")));

        instance = new Version(null);
    }
}
