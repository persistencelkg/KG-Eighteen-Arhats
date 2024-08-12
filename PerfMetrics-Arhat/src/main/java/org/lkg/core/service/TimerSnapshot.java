package org.lkg.core.service;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import lombok.extern.slf4j.Slf4j;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.bo.TimePercentEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/12 7:32 PM
 */
@Slf4j
public class TimerSnapshot {

    private static Map<String, ValueAtPercentile[]> TIMER_MAP = new HashMap<>();

    public static void setMeter(Meter.Id id, MeterBo meterBo, ValueAtPercentile[] percentiles) {
        if (meterBo.getCount() > 0) {
            TIMER_MAP.put(id.getName(), percentiles);
        } else {
            TIMER_MAP.remove(id.getName());
        }
    }

    public static double getValWithPercent(String name, TimePercentEnum timePercentEnum) {
        ValueAtPercentile[] valueAtPercentiles = TIMER_MAP.get(name);
        for (ValueAtPercentile valueAtPercentile : valueAtPercentiles) {
            double value = valueAtPercentile.value();
            log.info("time value:{}", value);
            if (timePercentEnum.getValue() == value) {
                return value;
            }
        }
        return 0;
    }
}

