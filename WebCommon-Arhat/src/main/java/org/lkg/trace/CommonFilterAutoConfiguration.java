package org.lkg.trace;

import org.lkg.core.TraceHolder;
import org.lkg.spring.OnTraceEnable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/25 9:20 PM
 */
@Configuration
@OnTraceEnable
public class CommonFilterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CommonTraceFilter commonTraceFilter(TraceHolder traceHolder) {
        return new CommonTraceFilter(traceHolder);
    }

}
