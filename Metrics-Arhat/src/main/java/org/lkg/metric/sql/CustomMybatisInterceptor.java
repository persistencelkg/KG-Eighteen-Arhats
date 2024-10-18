package org.lkg.metric.sql;

import org.springframework.core.Ordered;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/18 3:02 PM
 */
public interface CustomMybatisInterceptor extends Ordered {

    Object interceptor(Chain chain) throws Exception;


    interface Chain {

        String sql();

        Object process() throws Exception;
    }
}
