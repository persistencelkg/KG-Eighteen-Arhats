package org.lkg.structable_pattern.bridge;

import lombok.AllArgsConstructor;

/**
 * 桥接模式的核心，针对两个独立维度的对象 进行单独扩展
 * @date: 2025/5/18 21:53
 * @author: li kaiguang
 */
@AllArgsConstructor
public abstract class InsuranceCompany {

    protected InsuranceProduct insuranceProduct;

    public abstract Object dealWithInsureApply(Object arg);

}
