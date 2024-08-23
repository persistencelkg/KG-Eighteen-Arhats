package org.lkg.config;

import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import lombok.NoArgsConstructor;
import org.lkg.apollo.ApolloConfigService;
import org.lkg.enums.StringEnum;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/23 3:54 PM
 */
@Configuration
@NoArgsConstructor
public class ApolloCOnfigBeanFactoryPostProcessor implements ApplicationContextInitializer<ConfigurableApplicationContext>,
        BeanFactoryPostProcessor, EnvironmentAware {

    private Environment environment;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.addBeanFactoryPostProcessor(this);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ApolloConfigService apolloConfigService = ApolloConfigService.getInstance();
        String property = environment.getProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_NAMESPACES, ConfigConsts.NAMESPACE_APPLICATION);
        Arrays.stream(property.split(StringEnum.COMMA)).forEach(apolloConfigService::registerNameSpace);
        beanFactory.registerSingleton("apolloConfigService", apolloConfigService);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
