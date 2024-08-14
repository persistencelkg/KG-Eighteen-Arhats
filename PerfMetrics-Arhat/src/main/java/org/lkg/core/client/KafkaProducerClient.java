package org.lkg.core.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.lkg.core.DynamicConfigManger;
import org.lkg.core.config.LongHongConst;
import org.lkg.simple.JacksonUtil;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/13 9:03 PM
 */
@Slf4j
@Component
public class KafkaProducerClient {


    private KafkaProducer<String, String> producer;

    private String topic;

    private static KafkaProducerClient INSTANCE;


    public synchronized static KafkaProducerClient getInstance() {
        if (INSTANCE == null) {
            return INSTANCE = new KafkaProducerClient();
        }
        return INSTANCE;
    }

    private KafkaProducerClient() {
        DynamicConfigManger.initAndRegistChangeEvent(LongHongConst.KAFKA_CONFIG_KEY, DynamicConfigManger::getConfigValue, this::setProducer);
    }

    public Future<RecordMetadata> sendMsg(String value, Callback callback) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, null, value);
        return producer.send(producerRecord, callback);
    }

    public void setProducer(String config) {
        try {
            Map<String, Object> properties = JacksonUtil.readMap(config);
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            KafkaProducer<String, String> old = producer;
            this.topic = properties.get("topic").toString() + "_" + DynamicConfigManger.getEnv();
            log.info("init kafka producer,configKey={},topic={},config={}", LongHongConst.KAFKA_CONFIG_KEY, topic, config);
            this.producer = new KafkaProducer<>(properties);
            log.info("init kafka producer success");
            if (old != null) {
                old.close();
            }
        } catch (Exception e) {
            log.warn("parse perf producer config failed:{}", e.getMessage());
        }
    }
}
