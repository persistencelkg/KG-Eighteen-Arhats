package org.lkg.spring;

import org.lkg.core.DefaultTraceDecorator;
import org.lkg.core.ExtraEntryInjector;
import org.lkg.core.TraceDecorator;
import org.lkg.core.TraceHolder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/24 9:53 PM
 */
@Configuration
@OnTraceEnable
public class FullTraceAutoConfiguration {

    @Bean
    public TraceHolder traceHolder(ObjectProvider<TraceDecorator> traceDecorator, ExtraEntryInjector extraEntryInjector) {
        return TraceHolder.getInstance(traceDecorator, extraEntryInjector);
    }

    @Bean
    @ConditionalOnMissingBean
    public TraceDecorator traceDecorator() {
        return new DefaultTraceDecorator();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExtraEntryInjector extraEntryInjector() {
        return ExtraEntryInjector.DEFAULT;
    }


    @Bean
    public EnvironmentPostProcessor environmentPostProcessor() {
        return new TraceLogConfigEnvironmentPostProcessor();
    }

}
