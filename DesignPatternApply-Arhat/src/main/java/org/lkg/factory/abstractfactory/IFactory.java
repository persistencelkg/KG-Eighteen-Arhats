package org.lkg.factory.abstractfactory;

import org.lkg.factory.factory.IGood;

/**
 * 可以创建多个同类等级的产品族
 * @date: 2025/5/10 23:44
 * @author: li kaiguang
 */
public interface IFactory {

    IGood buildGood(int type);

    IVegetable buildVegetable();
}
