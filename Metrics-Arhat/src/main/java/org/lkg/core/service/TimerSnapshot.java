package org.lkg.core.service;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import lombok.extern.slf4j.Slf4j;
import org.lkg.core.DynamicConfigManger;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.bo.TimePercentEnum;
import org.lkg.enums.StringEnum;
import org.lkg.simple.ObjectUtil;

import java.util.*;
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

    private static ConcurrentHashMap<String, MeterBo> METER_MAP = new ConcurrentHashMap<>(1024);

    private static List<String> specialTagKey;

    static {
        DynamicConfigManger.initAndRegistChangeEvent("ttl.check.tag.key-list", DynamicConfigManger::toList, (ref) -> {
            specialTagKey = ref;
            // third.success.url
            // third.fail.url
            log.info("specail ttl keylist:{}", specialTagKey);
        });
    }

    public static void setMeter(Meter.Id id, MeterBo meterBo, ValueAtPercentile[] percentiles) {
        if (meterBo.getCount() > 0) {
            log.info("snapshot size：" + TIMER_MAP.size());
            TIMER_MAP.put(id.getName(), percentiles);
            if (ObjectUtil.isEmpty(specialTagKey)) {
                return;
            }
            // key namespace:tag-key  value meter
            Map<String, String> notInternalTag = meterBo.getNotInternalTag();
            notInternalTag.forEach((k, v) -> {
                String key = getKey(meterBo.getNamespace(), k);
                if (specialTagKey.contains(key)) {
                    METER_MAP.put(getKey(meterBo.getNamespace(), v), meterBo);
                }
            });

        } else {
            TIMER_MAP.remove(id.getName());
        }
    }

    private static String getKey(String namespace, String v) {
        return namespace + StringEnum.DOT + v;
    }


    public static MeterBo getMeter(String namespace, Tag tag) {
        return METER_MAP.get(getKey(namespace, tag.getValue()));
    }

    public static void clearTimeSnap(String namespace, Tag tagVal) {
        METER_MAP.remove(getKey(namespace, tagVal.getValue()));
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

