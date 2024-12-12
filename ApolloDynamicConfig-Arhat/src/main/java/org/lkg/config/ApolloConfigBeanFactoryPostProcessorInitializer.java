package org.lkg.config;

import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import lombok.NoArgsConstructor;
import org.lkg.apollo.ApolloConfigService;
import org.lkg.core.DynamicConfigManger;
import org.lkg.core.KeyConfigService;
import org.lkg.enums.StringEnum;
import org.lkg.simple.ServerInfo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.Arrays;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/23 3:54 PM
 */
@Configuration
@NoArgsConstructor
@ConditionalOnProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED)
public class ApolloConfigBeanFactoryPostProcessorInitializer
        implements ApplicationContextInitializer<GenericApplicationContext>,
        BeanFactoryPostProcessor , EnvironmentAware {

    private Environment environment;


    @Override
    public void initialize(GenericApplicationContext applicationContext) {
//        applicationContext.addBeanFactoryPostProcessor(this); 这个比@Configuration解析的实际还早，这会导致apollo 配置本身还没准备好
        ConfigurableEnvironment contextEnvironment = applicationContext.getEnvironment();
        this.environment = contextEnvironment;
        // internal config service
        DynamicConfigManger.registerConfigService(contextEnvironment::getProperty);
        // 值过滤器，处理占位符的value
        DynamicConfigManger.addValueFilter(s -> {
            try {
                return contextEnvironment.resolvePlaceholders(s);
            } catch (Exception e) {
                return s;
            }
        });
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 让配置中心早于 LongHengMeterRegistry的初始化，以便于能在一开始就能拿到初始化好的配置
        // 1. 避免long heng 相关的初始化配置获取不到 而一开始就走默认值
        // 2. 避免初始化依赖问题
        ApolloConfigService apolloConfigService = ApolloConfigService.getInstance();
        String property = environment.getProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_NAMESPACES, ConfigConsts.NAMESPACE_APPLICATION);
        Arrays.stream(property.split(StringEnum.COMMA)).forEach(apolloConfigService::registerNameSpace);

        // 初始化应用底层的环境信息
        initServerInfo();
    }

    private void initServerInfo() {
        ServerInfo.setServerName(DynamicConfigManger.getServerName());
        ServerInfo.setEnv(DynamicConfigManger.getEnv());
        ServerInfo.setPort(DynamicConfigManger.getInt("server.port", -1));
        ServerInfo.setInnerIp(ServerInfo.innerIp());
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
