package org.lkg.metric.redis;

import org.springframework.data.redis.connection.RedisConnection;

import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/4 7:17 PM
 */
public class RedisConnectionProxy extends AbstractRedisConnectionProxy<RedisConnection> {

    public RedisConnectionProxy(RedisConnection delegate, List<RedisInterceptor> list) {
        super(delegate, list);
    }
}
