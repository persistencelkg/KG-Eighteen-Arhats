package org.lkg.service;

import lombok.extern.slf4j.Slf4j;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.simple.JacksonUtil;
import org.lkg.simple.ObjectUtil;
import org.lkg.metric.threadpool.TrackableThreadPoolUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
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


    private final LongHengMeterRegistry longHengMeterRegistry = LongHengMeterRegistry.getInstance();


    private final ExecutorService executorService = TrackableThreadPoolUtil.newTrackableExecutor("longheng-consume");

    @Resource  private MeterQueueService meterQueueService;


    //    @KafkaListener
    public void consume(String val) {
        List<MeterBo> meterBos = JacksonUtil.readList(val, MeterBo.class);
        if (ObjectUtil.isEmpty(meterBos)) {
            log.warn("consume metrics empty:{}", val);
            return;
        }

        executorService.submit(() -> {
            meterBos.forEach(meterQueueService::off);
        });

    }

}
