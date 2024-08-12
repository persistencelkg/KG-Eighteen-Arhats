package org.lkg.core.service;

import io.micrometer.core.instrument.Meter;
import org.lkg.core.bo.MeterBo;

import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/9 10:42 AM
 */
public interface MetricExporter {

    void publishMeter(Map<Meter.Id, MeterBo> meterBoMap);
}
