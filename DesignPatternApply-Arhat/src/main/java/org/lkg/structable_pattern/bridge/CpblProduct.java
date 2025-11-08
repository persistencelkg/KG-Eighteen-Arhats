package org.lkg.structable_pattern.bridge;

/**
 * @date: 2025/5/18 21:59
 * @author: li kaiguang
 */
public class CpblProduct implements InsuranceProduct{
    @Override
    public Object insureApply(Object arg) {
        System.out.println("投保---跨境邮包险");
        return null;
    }

    @Override
    public Object claimApply(Object arg) {
        System.out.println("理赔---跨境邮包险");
        return null;
    }

    @Override
    public void callBack() {
        System.out.println("理赔案件通知回调---跨境邮包险");
    }
}
