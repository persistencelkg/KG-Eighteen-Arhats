package org.lkg.metric.redis;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConnection;

import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/4 9:22 PM
 */
public class RedisMetricConnectionFactory implements InitializingBean, DisposableBean, RedisConnectionFactory {

    private final RedisConnectionFactory delegate;
    private final List<RedisInterceptor> list;

    public RedisMetricConnectionFactory(RedisConnectionFactory redisConnectionFactory, List<RedisInterceptor> list) {
        if (redisConnectionFactory instanceof  RedisMetricConnectionFactory) {
            this.delegate = ((RedisMetricConnectionFactory) redisConnectionFactory).delegate;
        } else {
            this.delegate = redisConnectionFactory;
        }
        this.list = list;

    }


    @Override
    public void destroy() throws Exception {
        if (delegate instanceof  DisposableBean) {
            ((DisposableBean) delegate).destroy();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (delegate instanceof  InitializingBean) {
            ((InitializingBean) delegate).afterPropertiesSet();
        }
    }

    @Override
    public RedisConnection getConnection() {
        return new RedisConnectionProxy(delegate.getConnection(), list).get();
    }

    @Override
    public RedisClusterConnection getClusterConnection() {
        return new RedisClusterConnectionProxy(delegate.getClusterConnection(), list).get();
    }

    @Override
    public boolean getConvertPipelineAndTxResults() {
        return delegate.getConvertPipelineAndTxResults();
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        return delegate.getSentinelConnection();
    }

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        return delegate.translateExceptionIfPossible(ex);
    }
}
