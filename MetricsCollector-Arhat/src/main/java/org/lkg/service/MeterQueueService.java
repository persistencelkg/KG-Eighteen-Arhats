package org.lkg.service;

import lombok.extern.slf4j.Slf4j;
import org.lkg.config.MetricCollectorConfig;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.config.LongHongConst;
import org.lkg.dao.DataWriter;
import org.lkg.dao.InfluxDbWriter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/13 11:26 AM
 */
@Service
@EnableScheduling
@Slf4j
public class MeterQueueService {


//    @Resource
    private DataWriter dataWriter;

    private final LinkedBlockingQueue<MeterBo> meterBoQueue = new LinkedBlockingQueue<>(100000);


    public void off(MeterBo meterBo) {
        if (Objects.isNull(dataWriter) || Objects.isNull(meterBo)) {
            return;
        }
        MetricCollectorConfig metricCollectorConfig = MetricCollectorConfig.getInstance();
        if (Objects.isNull(metricCollectorConfig)) {
            return;
        }

        if (metricCollectorConfig.getDeptBlackList().contains(meterBo.getTagMap().get(LongHongConst.TagConst.DEPT))
                || metricCollectorConfig.getServerNameBlackList().contains(meterBo.getTagMap().get(LongHongConst.TagConst.SERVER_NAME))) {
            return;
        }
        try {
            meterBoQueue.offer(meterBo, 1, TimeUnit.SECONDS);
            if (meterBoQueue.size() >= metricCollectorConfig.getBatchSize()) {
                flush();
            }
        } catch (InterruptedException e) {
            log.error("meter queue has full，current size:{}", meterBoQueue.size(), e);
        }
    }


    @Scheduled(cron="0/5 * * * * ? ")
    public void flush() {
        MetricCollectorConfig metricCollectorConfig = MetricCollectorConfig.getInstance();
        if (Objects.isNull(metricCollectorConfig)) {
            return;
        }
        Integer batchSize = metricCollectorConfig.getBatchSize();
        List<MeterBo> list = new ArrayList<>((int) (batchSize * 1.5));
        while(!meterBoQueue.isEmpty()) {
            list.add(meterBoQueue.poll());
            if (list.size() >= batchSize) {
                 dataWriter.batchWrite(list);
            }
        }
        dataWriter.batchWrite(list);
    }

}
