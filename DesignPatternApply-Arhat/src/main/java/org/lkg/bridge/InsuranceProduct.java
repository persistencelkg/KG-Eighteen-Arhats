package org.lkg.bridge;

/**
 * @description:
 * @date: 2025/5/18 21:54
 * @author: li kaiguang
 */
public interface InsuranceProduct {


    /**
     * 投保
     * @param arg
     * @return
     */
    Object insureApply(Object arg);


    /**
     * 理赔
     * @param arg
     * @return
     */
    Object claimApply(Object arg);


    /**
     * 保司回调
     */
    void callBack();
}
