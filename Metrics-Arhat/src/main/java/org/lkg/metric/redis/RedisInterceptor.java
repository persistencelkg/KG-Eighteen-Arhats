package org.lkg.metric.redis;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/18 7:21 PM
 */
public interface RedisInterceptor {

    Object intercept(Chain chain) throws Throwable;


    interface Chain {

        String cmd();

        Object process() throws Throwable;
    }
}
