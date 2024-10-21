package org.lkg.elastic_search.interceptor;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/21 4:01 PM
 */
public interface EsInterceptor {

    Object intercept(EsChain chain) throws Throwable;

    interface EsChain {

        Object process() throws Throwable;

        String esDsl();

    }
}
