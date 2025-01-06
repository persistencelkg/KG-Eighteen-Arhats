package org.lkg.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.checkerframework.checker.units.qual.C;
import org.lkg.algorithm.DynamicDataSourceCompatibleShardingConfiguration;
import org.lkg.elastic_search.spring.EsClientInterceptorAutoConfiguration;
import org.lkg.metric.api.MetricFilterAutoConfiguration;
import org.lkg.metric.redis.RedisMetricAutoConfiguration;
import org.lkg.metric.rpc.feign.FeignMetricAutoConfiguration;
import org.lkg.metric.sql.mybatis.MybatisMonitorAutoConfiguration;
import org.lkg.metric.system.SystemMetricAutoConfiguration;
import org.lkg.metric.system.TomcatMonitorAutoConfiguration;
import org.lkg.metric.threadpool.ThreadPoolMetricAutoConfiguration;
import org.lkg.redis.config.MoreRedisDataSourceConfig;
import org.lkg.spring.FullTraceAutoConfiguration;
import org.lkg.spring.TraceTimeoutAutoConfiguration;

/**
 * Description:
 * Author: 李开广
 * Date: 2025/1/3 4:11 PM
 */
@Getter
@AllArgsConstructor
public enum DynamicConfigOption {


    DYNAMIC_CONFIG_OPTION(new Class[]{
            ApolloConfigBeanFactoryPostProcessorInitializer.class,
            DynamicConfigAutoConfiguration.class
    }),


    METRIC_CONFIG(new Class[]{
            FeignMetricAutoConfiguration.class,
            MetricFilterAutoConfiguration.class,
            RedisMetricAutoConfiguration.class,
            MybatisMonitorAutoConfiguration.class,
            ThreadPoolMetricAutoConfiguration.class,
            SystemMetricAutoConfiguration.class,
            TomcatMonitorAutoConfiguration.class}),

    TTL_CONFIG(new Class[]{TraceTimeoutAutoConfiguration.class}),

    NO_SQL_CONFIG(new Class[] {MoreRedisDataSourceConfig.class, EsClientInterceptorAutoConfiguration.class}),

//    SHARDING_CONFIG(new Class[]{DynamicDataSourceCompatibleShardingConfiguration.class}),  optional 会影响最好以字符串方式注入

    TRACE_CONFIG(new Class[]{FullTraceAutoConfiguration.class}),

    // 暂时到这 剩余 mq、notice、long poll、cache

    ;

    private final Class<?>[] configClass;

}
