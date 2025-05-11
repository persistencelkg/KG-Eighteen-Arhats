package org.lkg.factory.factory;

/**
 * @date: 2025/5/10 23:46
 * @author: li kaiguang
 */
public class GoodAFactory implements IFactory{
    @Override
    public IGood buildGood(int type) {
        return new GoodA();
    }
}
