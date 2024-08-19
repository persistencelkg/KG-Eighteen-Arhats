package org.lkg.core.service.impl;

import io.micrometer.core.instrument.Meter;
import org.lkg.core.DynamicConfigManger;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.config.LongHongAlarmConfig;
import org.lkg.core.config.LongHongConst;
import org.lkg.core.service.MetricExporter;
import org.lkg.simple.ObjectUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/13 9:23 PM
 */
public abstract class AbstractMetricExporter implements MetricExporter {

    @Override
    public void publishMeter(Map<Meter.Id, MeterBo> meterBoMap) {
        if (ObjectUtil.isEmpty(meterBoMap)) {
            return;
        }
        // 先报警感知
        LongHongAlarmManger.alarm(meterBoMap);
        Integer batchSize = DynamicConfigManger.getInt(LongHongConst.KAFKA_CONFIG_BATCH_SIZE, 100);
        List<MeterBo> list = new ArrayList<>();
        meterBoMap.forEach((k, v) -> {
            list.add(v);
            if (list.size() >= batchSize) {
                exportMsg(list);
            }
        });
        exportMsg(list);
    }

    protected void exportMsg(List<MeterBo> list) {
        try {
            writeMsg(list);
        } finally {
            list.clear();
        }
    }

    protected abstract void writeMsg(List<MeterBo> list);
}
