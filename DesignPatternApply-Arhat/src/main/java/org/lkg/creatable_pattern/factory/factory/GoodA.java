package org.lkg.creatable_pattern.factory.factory;

/**
 * @date: 2025/5/10 23:46
 * @author: li kaiguang
 */
public class GoodA implements IGood {
    @Override
    public Resp getResult() {
        System.out.println("good A build.....");
        return null;
    }
}
