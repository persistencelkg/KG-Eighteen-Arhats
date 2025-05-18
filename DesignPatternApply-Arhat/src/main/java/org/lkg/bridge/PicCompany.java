package org.lkg.bridge;

/**
 * @date: 2025/5/18 21:58
 * @author: li kaiguang
 */
public class PicCompany extends InsuranceCompany{
    public PicCompany(InsuranceProduct insuranceProduct) {
        super(insuranceProduct);
    }

    @Override
    public Object dealWithInsureApply(Object arg) {
        System.out.println("中国人保--投保");
        return insuranceProduct.insureApply(arg);
    }
}
