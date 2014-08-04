/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openacs.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author Administrator
 */
public class FormTest extends TestCase {

    public FormTest(String testName) {
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
    public void testLoad() {
        Form frm = new Form ();
        try {
            frm.Load("tst", new FileInputStream("/C:/Documents and Settings/netbeans/acs/acs-war/web/WEB-INF/forms/voiceprofile.xml"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FormTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
