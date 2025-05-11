package org.lkg.singleton;

import java.io.Serializable;

/**
 * 在线程安全和懒加载做了共同取舍
 * @date: 2025/5/10 17:19
 * @author: li kaiguang
 */
public class InnerClassSingleton implements Serializable{


    /**
     * 反射和序列化都会对单例造成不安全问题
     */
    private InnerClassSingleton() {}

    private static class Singleton {
        private static final InnerClassSingleton INSTANCE = new InnerClassSingleton();
    }

    public static InnerClassSingleton getInstance() {
        return Singleton.INSTANCE;
    }

    /**
     * 解决反序列化 对象被破坏问题
     * 改名字是固定的
     * {@link  java.io.ObjectInputStream#readOrdinaryObject}
     * @return 固定的对象
     */
    private Object readResolve() {
        return getInstance();
    }
}
