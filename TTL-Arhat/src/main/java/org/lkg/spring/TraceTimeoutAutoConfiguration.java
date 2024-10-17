package org.lkg.spring;

import org.lkg.core.config.OnTraceTimeoutEnable;
import org.lkg.core.limit.FeignTraceTimeoutInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/17 5:08 PM
 */
@Configuration
@OnTraceTimeoutEnable
public class TraceTimeoutAutoConfiguration {

    @Bean
    public FeignTraceTimeoutInterceptor feignTraceTimeoutInterceptor() {
        return new FeignTraceTimeoutInterceptor();
    }

}
