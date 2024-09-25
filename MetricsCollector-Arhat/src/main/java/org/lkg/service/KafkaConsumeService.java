package org.lkg.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.simple.JacksonUtil;
import org.lkg.simple.ObjectUtil;
import org.lkg.metric.threadpool.TrackableThreadPoolUtil;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/12 9:09 PM
 */
// TODO kafka listener
@Service
@Slf4j
public class KafkaConsumeService {


    private final ExecutorService executorService = TrackableThreadPoolUtil.newTrackableExecutor("longheng-consume");

    @Resource  private MeterQueueService meterQueueService;


     @KafkaListener(topics = "user_feature_collect_topic",groupId = "longheng-group-id")
    public void consume(ConsumerRecord<String, String> record) {
         if (Objects.isNull(record) || Objects.isNull(record.value()) || !record.value().contains("longheng")) {
             log.error("topic:[user_feature_collect_topic] get a null value");
             return;
         }
         log.info("topic = [{}], offset = [{}], value = [{}]", record.topic(), record.offset(), record.value());
        List<MeterBo> meterBos = JacksonUtil.readList(record.value(), MeterBo.class);
        if (ObjectUtil.isEmpty(meterBos)) {
            return;
        }

        executorService.submit(() -> {
            meterBos.forEach(meterQueueService::off);
        });

    }

}