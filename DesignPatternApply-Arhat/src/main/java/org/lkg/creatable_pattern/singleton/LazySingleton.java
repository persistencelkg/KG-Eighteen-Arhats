package org.lkg.creatable_pattern.singleton;

import java.util.Objects;

/**
 * @date: 2025/5/10 23:01
 * @author: li kaiguang
 */
public class LazySingleton {

    private static LazySingleton singleton;

    private LazySingleton() {}

    public static synchronized LazySingleton getInstance() {
        if (Objects.isNull(singleton)) {
            singleton = new LazySingleton();
        }
        return singleton;
    }
}
