package org.lkg.creatable_pattern.factory.factory;

/**
 * @date: 2025/5/10 23:52
 * @author: li kaiguang
 */
public class FactoryHandler {

    public static Resp buildGood(int type) {
        IFactory factory ;
        if (type == 1) {
            factory = new GoodAFactory();
        } else {
            factory = new GoodBFactory();
        }
        IGood iGood = factory.buildGood(type);
        return iGood.getResult();
    }
}
