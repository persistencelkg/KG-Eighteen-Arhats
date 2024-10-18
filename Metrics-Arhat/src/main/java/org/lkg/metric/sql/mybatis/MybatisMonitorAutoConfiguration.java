package org.lkg.metric.sql.mybatis;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.lkg.core.config.EnableLongHengMetric;
import org.lkg.metric.sql.CustomMybatisInterceptor;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/28 8:57 PM
 */
@Configuration
@ConditionalOnClass(StatementHandler.class)
@EnableLongHengMetric
public class MybatisMonitorAutoConfiguration {

    /**
     * mybatis、mybatis-plus 通用
     * @return
     */
    @Bean
    public Interceptor mybatisInterceptor(ObjectProvider<CustomMybatisInterceptor> customMybatisInterceptorObjectProvider) {
        return new MybatisStatementInterceptor(customMybatisInterceptorObjectProvider.stream().collect(Collectors.toList()));
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer(Interceptor mybatisInterceptor) {

        return ref -> {
            ref.setCacheEnabled(false);
        };
    }


}
