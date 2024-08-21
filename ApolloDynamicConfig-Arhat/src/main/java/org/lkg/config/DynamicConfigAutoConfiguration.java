package org.lkg.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import org.lkg.apollo.ApolloConfigService;
import org.lkg.core.DynamicConfigService;
import org.lkg.enums.StringEnum;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/20 2:32 PM
 */
@Configuration
@ConditionalOnClass(Config.class)
@ConditionalOnProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED)
public class DynamicConfigAutoConfiguration {

    @Bean
    public BeanPostProcessor dynamicConfigBeanPostProcessor() {
        return new DynamicConfigBeanPostProcessor();
    }

    @Bean
    public DynamicConfigService dynamicConfigService(Environment environment) {
        ApolloConfigService apolloConfigService = new ApolloConfigService();
        String property = environment.getProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED, ConfigConsts.NAMESPACE_APPLICATION);
        Arrays.stream( property.split(StringEnum.COMMA)).forEach(apolloConfigService::registerNameSpace);
        return apolloConfigService;
    }

}
