package org.lkg;

import org.junit.Test;
import org.lkg.structable_pattern.bridge.CpicCompany;
import org.lkg.structable_pattern.bridge.PhiProduct;

/**
 * @date: 2025/5/18 22:01
 * @author: li kaiguang
 */
public class BridgeTest {

    @Test
    public void testBridge() {
        CpicCompany cpicCompany = new CpicCompany(new PhiProduct());
        cpicCompany.dealWithInsureApply(new Object());
    }
}
