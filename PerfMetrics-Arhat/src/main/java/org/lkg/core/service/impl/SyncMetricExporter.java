package org.lkg.core.service.impl;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.core.service.MetricExporter;

import java.util.Map;

/**
 * Description: 本地验证使用
 * Author: 李开广
 * Date: 2024/8/9 2:04 PM
 */
public class SyncMetricExporter implements MetricExporter {


    @Override
    public void publishMeter(Map<Meter.Id, MeterBo> meterBoMap) {
        System.out.println(meterBoMap);
    }
}
