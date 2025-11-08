package org.lkg.creatable_pattern.singleton;

/**
 * 类加载阶段就已经初始化好了私有静态对象
 * @date: 2025/5/10 16:56
 * @author: li kaiguang
 */
public class HungrySingleton {
    private static final HungrySingleton INSTANCE = new HungrySingleton();
    private HungrySingleton() {}

    public static HungrySingleton getInstance() {
        return INSTANCE;
    }
}
