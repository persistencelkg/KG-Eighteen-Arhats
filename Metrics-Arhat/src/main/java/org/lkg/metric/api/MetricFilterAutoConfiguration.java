package org.lkg.metric.api;

import org.lkg.core.config.EnableLongHengMetric;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/5 7:15 PM
 */
@Configuration
@EnableLongHengMetric
@ConditionalOnClass(Filter.class)
public class MetricFilterAutoConfiguration {

    // 扩展对bean进行注入即可
    @Bean
    public CommonFilter metricFilter() {
        return new MetricFilter();
    }


    @Bean
    public FilterRegistrationBean<Filter> registrationBean(ObjectProvider<CommonFilter> commonFilterObjectProvider) {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterFilterRegistrationBean.setDispatcherTypes(EnumSet.of(DispatcherType.INCLUDE,
                DispatcherType.ASYNC, DispatcherType.REQUEST, DispatcherType.ERROR, DispatcherType.FORWARD));
        filterFilterRegistrationBean.setFilter(new CommonFilterSupport(commonFilterObjectProvider.iterator()));
        return filterFilterRegistrationBean;
    }

}
