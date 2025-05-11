package org.lkg.factory.simple;

/**
 * @date: 2025/5/10 23:42
 * @author: li kaiguang
 */
public class SimpleFactory {

    public static Object buildObject(int type) {
        if (type == 1) {
            return new Object();
        } else if (type ==2 ) {
            return new Object();
        } else if (type == 3) {
            return new Object();
        } else {
            return new Object();
        }
    }
}
