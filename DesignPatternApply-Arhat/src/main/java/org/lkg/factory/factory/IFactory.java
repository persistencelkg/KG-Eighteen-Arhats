package org.lkg.factory.factory;

/**
 * 解决了简单工厂，只有1个工厂， 导致新的产品进来不得不添加新的方法，对工厂内部逻辑进行修改
 * @date: 2025/5/10 23:44
 * @author: li kaiguang
 */
public interface IFactory {

    IGood buildGood(int type);

}
