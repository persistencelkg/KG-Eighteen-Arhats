package org.lkg.creatable_pattern.singleton;

import java.util.Objects;

/**
 * @date: 2025/5/10 17:01
 * @author: li kaiguang
 */
public class DCLSingleton {

    //防止指令重排序，因为new 对象过程存在指令重排序行为，导致对象可能进入半实例化状态, 就被其他线程获取到, 导致使用时出现不安全问题
    private static volatile DCLSingleton INSTANCE;

    private DCLSingleton() {}

    private static DCLSingleton getInstance() {
        // 初始化
        if (Objects.isNull(INSTANCE)) {
            // 避免线程安全问题 防止并行实例化，避免直接锁住方法降低吞吐量
            synchronized (DCLSingleton.class) {
                // 避免synchronized此时已经存在其他线程排队等待获取锁，如果首个线程已经拿到锁，并做了初始化后释放锁
                // 此时其他排队线程获取后 如果不加判断，还会继续执行实例化逻辑，导致获取多个实例对象 从而引发线程不安全
                if (Objects.isNull(INSTANCE)) {
                    INSTANCE = new DCLSingleton();
                }
            }
        }
        return INSTANCE;
    }
}
