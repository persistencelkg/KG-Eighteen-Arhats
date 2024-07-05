package org.lkg.function;

/**
 * Description:自检的函数式接口
 * Author: 李开广
 * Date: 2024/5/15 1:55 PM
 */
@FunctionalInterface
public interface CheckedConsumer<T, E extends Exception> {

    void check(T t) throws E;
}
