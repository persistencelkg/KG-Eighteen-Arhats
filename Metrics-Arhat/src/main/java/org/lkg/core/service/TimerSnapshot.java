package org.lkg.core.service;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import lombok.extern.slf4j.Slf4j;
import org.lkg.core.DynamicConfigManger;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.bo.TimePercentEnum;
import org.lkg.enums.StringEnum;
import org.lkg.utils.ObjectUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: for ttl component
 * Author: 李开广
 * Date: 2024/8/12 7:32 PM
 */
@Slf4j
public class TimerSnapshot {

    // 注意这里在高并发下，会存在gc压力，所以默认最多保留一个周期，因此基于此做的TTL耗时预警、流量管控等都是
    private static ConcurrentHashMap<String, ValueAtPercentile[]> TIMER_MAP = new ConcurrentHashMap<>(1024);

    public static void setMeter(Meter.Id id, MeterBo meterBo, ValueAtPercentile[] percentiles) {
        if (meterBo.getCount() > 0) {
            log.info("snapshot size：" + TIMER_MAP.size());
            TIMER_MAP.put(id.getName(), percentiles);
        } else {
            TIMER_MAP.remove(id.getName());
        }
    }

    private static String getKey(String namespace, String v) {
        return namespace + StringEnum.DOT + v;
    }

    public static void clear() {
        Integer anInt = DynamicConfigManger.getInt("longheng.snapshot.max-size", 4096);
        if (TIMER_MAP.size() > anInt) {
            TIMER_MAP.clear();
        }
    }

    public static double getValWithPercent(Meter.Id id, TimePercentEnum timePercentEnum) {
        ValueAtPercentile[] valueAtPercentiles = TIMER_MAP.get(id.getName());
        if (ObjectUtil.isEmpty(valueAtPercentiles)) {
            return 0;
        }
        for (ValueAtPercentile valueAtPercentile : valueAtPercentiles) {
            double value = valueAtPercentile.value();
//            log.info("time value:{}", value);
            if (timePercentEnum.getValue() == valueAtPercentile.percentile()) {
                return value;
            }
        }
        return 0;
    }
}

