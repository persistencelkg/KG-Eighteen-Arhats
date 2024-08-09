package org.lkg.core.service.impl;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.core.service.MetricExporter;

import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/9 2:04 PM
 */
public class KafkaMetricExporter implements MetricExporter {

    // TODO client

    @Override
    public void publishMeter(Map<Meter.Id, MeterBo> meterBoMap) {
        Timer register = Timer.builder("").publishPercentiles().register(new LongHengMeterRegistry());
//        for (ValueAtPercentile valueAtPercentile : register.takeSnapshot().percentileValues()) {
//            if (valueAtPercentile.percentile() == percentile) {
//                return valueAtPercentile.value(unit);
//            }
//        }

    }
}
