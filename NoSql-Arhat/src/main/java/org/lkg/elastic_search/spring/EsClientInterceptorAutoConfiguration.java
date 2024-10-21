package org.lkg.elastic_search.spring;

import org.lkg.core.config.EnableLongHengMetric;
import org.lkg.core.config.OnTraceTimeoutEnable;
import org.lkg.elastic_search.interceptor.EsInterceptor;
import org.lkg.spring.OnTraceEnable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/21 5:02 PM
 */
@Configuration
public class EsClientInterceptorAutoConfiguration {



    @Configuration
    @OnTraceTimeoutEnable
    static class TraceTimeoutAutoConfiguration {

    }



    @Configuration
    @OnTraceEnable
    static class TraceAutoConfiguration {

    }



    @Configuration
    @EnableLongHengMetric
    static class MetricAutoConfiguration {


    }
}
