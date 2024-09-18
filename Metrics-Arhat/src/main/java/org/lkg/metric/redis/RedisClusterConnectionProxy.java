package org.lkg.metric.redis;

import org.springframework.data.redis.connection.RedisClusterConnection;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/4 7:23 PM
 */
public class RedisClusterConnectionProxy extends AbstractRedisConnectionProxy<RedisClusterConnection>{
    public RedisClusterConnectionProxy(RedisClusterConnection proceed) {
        super(proceed);
    }


}
