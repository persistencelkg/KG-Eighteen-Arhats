package org.lkg.kafka.spring;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;
import org.checkerframework.checker.units.qual.K;
import org.lkg.core.FullLinkPropagation;
import org.lkg.core.TraceClose;
import org.lkg.core.TraceHolder;
import org.lkg.kafka.core.KafkaAspect;
import org.lkg.kafka.core.MoreKafkaConfig;
import org.lkg.spring.OnTraceEnable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Description:
 * 1. 定制化kafka集群能力
 * 2. 对spring注入的kafka# client增加trace 能力
 * Author: 李开广
 * Date: 2024/9/26 9:05 PM
 */
@Configuration
@ConditionalOnClass(value = {Producer.class, Consumer.class, MoreKafkaConfig.class})
public class CustomKafkaAutoConfiguration {


    /* default bean from spring#KafkaAutoConfiguration */
    @Bean
    public KafkaTemplate<?, ?> primary(MoreKafkaConfig moreKafkaConfig, TraceHolder traceHolder) {
        KafkaProperties kafkaProperties = moreKafkaConfig.getKafka().get(MoreKafkaConfig.first);
        DefaultKafkaProducerFactory<?, ?> defaultKafkaProducerFactory = new AdvanceKafkaProducerFactory<>(traceHolder, kafkaProperties.buildProducerProperties());
        return new KafkaTemplate<>(defaultKafkaProducerFactory);
    }

    @Bean
    public KafkaTemplate<?, ?> second(MoreKafkaConfig moreKafkaConfig, TraceHolder traceHolder) {
        KafkaProperties kafkaProperties = moreKafkaConfig.getKafka().get(MoreKafkaConfig.second);
        DefaultKafkaProducerFactory<?, ?> defaultKafkaProducerFactory = new AdvanceKafkaProducerFactory<>(traceHolder, kafkaProperties.buildProducerProperties());
        return new KafkaTemplate<>(defaultKafkaProducerFactory);
    }


    /*  from KafkaAnnotationDrivenConfiguration */

    @Bean
    @ConditionalOnMissingBean(name = "primaryKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<?, ?> primaryKafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory,
            MoreKafkaConfig moreKafkaConfig) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        KafkaProperties kafkaProperties = moreKafkaConfig.getKafka().get(MoreKafkaConfig.first);
        configurer.configure(factory, kafkaConsumerFactory
                .getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties())));
        return factory;
    }


    @Bean
    @ConditionalOnMissingBean(name = "secondKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<?, ?> secondKafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory,
            MoreKafkaConfig moreKafkaConfig) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        KafkaProperties kafkaProperties = moreKafkaConfig.getKafka().get(MoreKafkaConfig.second);
        configurer.configure(factory, kafkaConsumerFactory
                .getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties())));
        return factory;
    }


    @Configuration
    @OnTraceEnable
    static class TraceKafkaConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public KafkaAspect kafkaAspect(TraceHolder traceHolder) {
            return new KafkaAspect(traceHolder);
        }

//        ProducerInterceptor 仅仅适合不需要构造参数能扩展对象的请问，因为内部通过class名字反射创建
    }
}
