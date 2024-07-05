package org.lkg.function;

/**
 * Description: 可停止的函数
 * Author: 李开广
 * Date: 2024/5/15 2:34 PM
 */
@FunctionalInterface
public interface StopAbleConsumer {

    boolean stop();
}
