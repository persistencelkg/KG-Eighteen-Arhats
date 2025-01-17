package org.lkg.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.lkg.core.bo.MeterBo;
import org.lkg.utils.JacksonUtil;
import org.lkg.utils.ObjectUtil;
import org.lkg.metric.threadpool.TrackableThreadPoolUtil;
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
    private static final String METRIC_TOPIC = "metric-topic";


    private final ExecutorService executorService = TrackableThreadPoolUtil.newTrackableExecutor("longheng-consume");

    @Resource
    private MeterQueueService meterQueueService;


//    @KafkaListener(topics = METRIC_TOPIC, groupId = "longheng-group-id")
    public void consume(ConsumerRecord<String, String> record) {
        if (Objects.isNull(record) || Objects.isNull(record.value()) || !record.value().contains("long-heng")) {
            log.error("topic:[{}] get a null value", METRIC_TOPIC);
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
