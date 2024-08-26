package org.lkg.core.service.impl;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import lombok.extern.slf4j.Slf4j;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.client.KafkaProducerClient;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.core.service.MetricExporter;
import org.lkg.simple.JacksonUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/9 2:04 PM
 */
//@Component
@Slf4j
public class KafkaMetricExporter extends AbstractMetricExporter {


    @Override
    protected void writeMsg(List<MeterBo> list) {
        KafkaProducerClient.getInstance().sendMsg(JacksonUtil.writeValue(list), (meta, exception) -> {
            if (Objects.isNull(exception)) {
                log.info("send kafka msg finish:{}", list);
            } else {
                log.error("send kafka msg error", exception);
            }
        });
    }
}
