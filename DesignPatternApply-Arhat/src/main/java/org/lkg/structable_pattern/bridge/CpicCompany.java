package org.lkg.structable_pattern.bridge;

/**
 *
 * @date: 2025/5/18 21:58
 * @author: li kaiguang
 */
public class CpicCompany extends InsuranceCompany{

    public CpicCompany(InsuranceProduct insuranceProduct) {
        super(insuranceProduct);
    }

    @Override
    public Object dealWithInsureApply(Object arg) {
        System.out.println("太平洋保险--投保");
        return insuranceProduct.insureApply(arg);
    }
}
