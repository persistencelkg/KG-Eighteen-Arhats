
package org.lkg.creatable_pattern.factory.factory;

/**
 * @date: 2025/5/10 23:46
 * @author: li kaiguang
 */
public class GoodB implements IGood {
    @Override
    public Resp getResult() {
        System.out.println("good B build.....");
        return null;
    }
}
