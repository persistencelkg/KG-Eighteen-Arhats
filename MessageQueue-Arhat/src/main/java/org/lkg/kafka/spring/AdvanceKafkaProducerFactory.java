package org.lkg.kafka.spring;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.checkerframework.checker.units.qual.K;
import org.lkg.core.TraceHolder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

import java.util.Map;

/**
 * 为多集群方式创建的trace拦截器
 * Description:
 * Author: 李开广
 * Date: 2024/9/29 11:28 AM
 */
public class AdvanceKafkaProducerFactory<K, V> extends DefaultKafkaProducerFactory<K, V> {

    private final TraceHolder traceHolder;

    public AdvanceKafkaProducerFactory(TraceHolder traceHolder, Map<String, Object> configs ) {
        super(configs);
        this.traceHolder = traceHolder;
    }

//    @Override 父层也是调用createProducer(String txIdPrefixArg) 避免多次创建
//    public Producer<K, V> createProducer() {
//        return (Producer<K, V>) KafkaProducerMethodInterceptor.proxyFactoryBean(super.createProducer(), traceHolder);
//    }

    @Override
    public Producer<K, V> createProducer(String txIdPrefixArg) {
        return (Producer<K, V>) KafkaProducerMethodInterceptor.proxyFactoryBean(super.createProducer(txIdPrefixArg), traceHolder);
    }
}
