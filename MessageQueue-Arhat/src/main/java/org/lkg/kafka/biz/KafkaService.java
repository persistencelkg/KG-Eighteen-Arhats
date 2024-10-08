package org.lkg.kafka.biz;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/27 4:24 PM
 */
@Service
@Slf4j
public class KafkaService {

    @Resource
    private Map<String, KafkaTemplate<String, String>> kafkaTemplateMap;

//    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String PRIMARY_CLUSTER = "primary";

    private static final String SECOND_CLUSTER = "second";

    public KafkaTemplate<String, String> kafkaTemplate(String key) {
        return kafkaTemplateMap.get(key);
    }


    public void sendMsg(String topic, String msg) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, msg);
        if (Objects.nonNull(kafkaTemplate(PRIMARY_CLUSTER))) {
            kafkaTemplate = kafkaTemplate(PRIMARY_CLUSTER);
        }
        kafkaTemplate.send(record).addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("topic:{} send fail, reason:{}", topic, ex.getMessage(), ex);
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("topic:{} send success, data:{}", topic, msg);
            }
        });
    }

//    @KafkaListener(containerFactory = "primaryKafkaListenerContainerFactory", topics = "test-topic", groupId = "test-topic-group-id")

    @KafkaListener(topics = "test-topic", groupId = "test-topic-group-id")
    public void consume(ConsumerRecord<String, String> record) {
        if (Objects.isNull(record) || Objects.isNull(record.value())) {
            log.error("topic:[{}] get a null value","test-topic");
            return;
        }
        log.info(">>>> topic = [{}], offset = [{}], value = [{}]", record.topic(), record.offset(), record.value());
    }


}
