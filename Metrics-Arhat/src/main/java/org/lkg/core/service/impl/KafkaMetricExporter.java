package org.lkg.core.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.client.KafkaProducerClient;
import org.lkg.utils.JacksonUtil;

import java.util.List;
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
//        System.out.println(list);
        KafkaProducerClient.getInstance().sendMsg(JacksonUtil.writeValue(list), (meta, exception) -> {
            if (Objects.isNull(exception)) {
                log.info("send kafka msg finish:{}", meta);
            } else {
                log.error("send kafka msg error", exception);
            }
        });
    }
}
