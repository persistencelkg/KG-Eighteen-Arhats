package org.lkg.core.limit;

import org.lkg.core.config.TraceLogEnum;
import org.lkg.metric.redis.RedisInterceptor;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/18 7:33 PM
 */
public class RedisTraceTimeoutInterceptor implements RedisInterceptor {
    @Override
    public Object intercept(Chain chain) throws Throwable {
        TraceTimeoutLimiter.getAndCheck(TraceLogEnum.Redis);
        return chain.process();
    }
}
