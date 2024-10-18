package org.lkg.core.limit;

import org.lkg.core.config.TraceLogEnum;
import org.lkg.metric.sql.CustomMybatisInterceptor;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/18 3:46 PM
 */
public class MybatisTimeoutInterceptor implements CustomMybatisInterceptor {


    @Override
    public Object interceptor(Chain chain) throws Exception {
        TraceTimeoutLimiter.getAndCheck(chain.sql(), TraceLogEnum.MySQL );
        return chain.process();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
