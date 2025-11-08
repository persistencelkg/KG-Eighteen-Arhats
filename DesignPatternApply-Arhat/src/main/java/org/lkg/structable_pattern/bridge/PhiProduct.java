package org.lkg.structable_pattern.bridge;

/**
 * @date: 2025/5/18 22:00
 * @author: li kaiguang
 */
public class PhiProduct implements InsuranceProduct{
    @Override
    public Object insureApply(Object arg) {
        System.out.println("投保----平台责任险" );
        return null;
    }

    @Override
    public Object claimApply(Object arg) {
        System.out.println("理赔----平台责任险" );
        return null;
    }

    @Override
    public void callBack() {
        System.out.println("理赔案件通知----平台责任险" );
    }
}
