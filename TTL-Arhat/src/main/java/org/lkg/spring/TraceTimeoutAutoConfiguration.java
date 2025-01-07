package org.lkg.spring;

import org.lkg.core.limit.FeignTraceTimeoutInterceptor;
import org.lkg.core.limit.MybatisTimeoutInterceptor;
import org.lkg.core.limit.RedisTraceTimeoutInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/17 5:08 PM
 */
@Configuration
public class TraceTimeoutAutoConfiguration {

    // feign interceptor
    @Bean
    public FeignTraceTimeoutInterceptor feignTraceTimeoutInterceptor() {
        return new FeignTraceTimeoutInterceptor();
    }

    // mybatis interceptor
    @Bean
    public MybatisTimeoutInterceptor mybatisTimeoutInterceptor() {
        return new MybatisTimeoutInterceptor();
    }

    // redis

    @Bean
    public RedisTraceTimeoutInterceptor redisTraceTimeoutInterceptor() {
        return new RedisTraceTimeoutInterceptor();
    }
}
