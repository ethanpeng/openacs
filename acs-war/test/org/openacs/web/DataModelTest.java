/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openacs.web;

import junit.framework.TestCase;
import org.richfaces.model.TreeNodeImpl;

/**
 *
 * @author Administrator
 */
public class DataModelTest extends TestCase {

    public DataModelTest(String testName) {
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
    public void testBuildNames() {
        DataModelJsfBean b = new DataModelJsfBean();
        TreeNodeImpl<String> root = new TreeNodeImpl<String>();
        root.setData("InternetGatewayDevice");

        String[] n = {".Capabilities.",
            ".Capabilities.PerformanceDiagnostic.",
            ".CaptivePortal.",
            ".DeviceConfig.",
            ".DeviceInfo.",
            ".DeviceInfo.VendorConfigFile.{i}.",
            ".DownloadDiagnostics.",
            ".IPPingDiagnostics.",
            ".LANConfigSecurity.",
            ".LANDevice.{i}.",
            ".LANDevice.{i}.Hosts.",
            ".LANDevice.{i}.Hosts.Host.{i}.",
            ".LANDevice.{i}.LANEthernetInterfaceConfig.{i}.",
            ".LANDevice.{i}.LANEthernetInterfaceConfig.{i}.Stats.",
            ".LANDevice.{i}.LANHostConfigManagement.",
            ".LANDevice.{i}.LANHostConfigManagement.DHCPConditionalServingPool.{i}.",
            ".LANDevice.{i}.LANHostConfigManagement.DHCPConditionalServingPool.{i}.DHCPOption.{i}.",
            ".LANDevice.{i}.LANHostConfigManagement.DHCPConditionalServingPool.{i}.DHCPStaticAddress.{i}.",
            ".LANDevice.{i}.LANHostConfigManagement.DHCPOption.{i}.",
            ".LANDevice.{i}.LANHostConfigManagement.DHCPStaticAddress.{i}.",
            ".LANDevice.{i}.LANHostConfigManagement.IPInterface.{i}.",
            ".LANDevice.{i}.LANUSBInterfaceConfig.{i}.",
            ".LANDevice.{i}.LANUSBInterfaceConfig.{i}.Stats.",
            ".LANDevice.{i}.WLANConfiguration.{i}.",
            ".LANDevice.{i}.WLANConfiguration.{i}.APWMMParameter.{i}.",
            ".LANDevice.{i}.WLANConfiguration.{i}.AssociatedDevice.{i}."
        };

        b.buildObjectNamesSubtree(n, ".", 0, root);

    }
}
