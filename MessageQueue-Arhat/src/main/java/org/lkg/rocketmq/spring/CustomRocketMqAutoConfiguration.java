package org.lkg.rocketmq.spring;

import lombok.AllArgsConstructor;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQMessageConverter;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.lkg.core.FullLinkPropagation;
import org.lkg.core.TraceClose;
import org.lkg.core.TraceHolder;
import org.lkg.core.config.EnableLongHengMetric;
import org.lkg.rocketmq.core.MoreRocketMqConfig;
import org.lkg.rocketmq.core.OnEnableMoreRocket;
import org.lkg.rocketmq.core.RocketMqAspect;
import org.lkg.rocketmq.core.consume.ConsumeMessageProcessJoinPointInterceptor;
import org.lkg.rocketmq.core.consume.MetricMessageConsumeInterceptor;
import org.lkg.rocketmq.core.consume.TraceMessageInterceptor;
import org.lkg.spring.OnTraceEnable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/29 4:23 PM
 */
@Configuration
@ConditionalOnClass(RocketMQTemplate.class)
public class CustomRocketMqAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public RocketMqAspect rocketMqAspect(TraceHolder traceHolder, ObjectProvider<ConsumeMessageProcessJoinPointInterceptor> consumeMessageProcessJoinPointInterceptorObjectProvider) {
        return new RocketMqAspect(traceHolder, consumeMessageProcessJoinPointInterceptorObjectProvider.stream().collect(Collectors.toList()));
    }


    @Configuration
    @OnTraceEnable
    static class RocketMqTraceAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public TraceMessageInterceptor consumeMessageTraceInterceptor(TraceHolder traceHolder) {
            return new TraceMessageInterceptor(traceHolder);
        }
    }

    @Configuration
    @EnableLongHengMetric
    static class EnableMetricAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public MetricMessageConsumeInterceptor metricMessageConsumeInterceptor() {
            return new MetricMessageConsumeInterceptor();
        }
    }




    //    @Bean
    public BeanPostProcessor consumeMessageBeanPostProcessor(TraceHolder traceHolder) {
        return new ConsumeMessageBeanPostProcessor(traceHolder);
    }


    @AllArgsConstructor
    static class ConsumeMessageBeanPostProcessor implements BeanPostProcessor {

        private final TraceHolder traceHolder;
        private final static FullLinkPropagation.Getter<MessageExt, String> GETTER = Message::getUserProperty;

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            // 因为改类的初始化 依赖了其类上面的注解参数，而这里是拿不到的，除非模仿源码重写 对应RocketMqListenerContainer ，但这样太不友好了
            // 这里演示一个错误示例，思想是对的，但落地有点问题
            if (bean instanceof RocketMQListener) {
                return wrapTraceForListener(((RocketMQListener<MessageExt>) bean), traceHolder);
            }
            return bean;
        }

        private RocketMQListener<MessageExt> wrapTraceForListener(RocketMQListener<MessageExt> bean, TraceHolder traceHolder) {

            return message -> {
                try (TraceClose traceClose = traceHolder.newTraceScope(GETTER, message)) {
                    bean.onMessage(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }




}
