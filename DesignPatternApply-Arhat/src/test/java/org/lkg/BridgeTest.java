package org.lkg;

import org.junit.Test;
import org.lkg.bridge.CpicCompany;
import org.lkg.bridge.InsuranceCompany;
import org.lkg.bridge.PhiProduct;
import org.lkg.bridge.PicCompany;

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
