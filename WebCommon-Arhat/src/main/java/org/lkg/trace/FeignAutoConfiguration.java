package org.lkg.trace;

import org.lkg.core.TraceHolder;
import org.lkg.metric.rpc.feign.SelfFeignInterceptor;
import org.lkg.spring.OnTraceEnable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/24 9:49 PM
 */
@Configuration
@OnTraceEnable
public class FeignAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public SelfFeignInterceptor feignTraceableInterceptor(TraceHolder traceHolder) {
        return new FeignTraceInterceptor(traceHolder);
    }
}
