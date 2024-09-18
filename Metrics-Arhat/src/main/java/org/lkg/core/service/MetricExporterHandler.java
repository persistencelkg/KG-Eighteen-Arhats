package org.lkg.core.service;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.HistogramSupport;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.bo.TimePercentEnum;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.core.meter.LongHengHistogramSupport;
import org.lkg.core.service.impl.SyncMetricExporter;
import org.lkg.metric.threadpool.TrackableThreadPoolUtil;
import org.lkg.simple.ObjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/9 4:36 PM
 */
@Slf4j
public class MetricExporterHandler {

    private MetricExporter metricExporter;


    public void exportMeter(List<Meter> list) {
        if (ObjectUtil.isEmpty(list) || Objects.isNull(metricExporter)) {
            return;
        }
        // CONVERT TO BO
        HashMap<Meter.Id, MeterBo> idMeterBoHashMap = convertToMeterBo(list);
        if (idMeterBoHashMap.isEmpty()) {
            return;
        }
        // async publish
        MetricCoreExecutor.execute(() -> {
            try {
                metricExporter.publishMeter(idMeterBoHashMap);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    private HashMap<Meter.Id, MeterBo> convertToMeterBo(List<Meter> list) {
        HashMap<Meter.Id, MeterBo> idMeterBoHashMap = new HashMap<>(list.size());
        list.forEach(val -> {
            MeterBo meterBo = populateDefault(val);
            // 浮动计数
            if (val instanceof Gauge) {
                populateGauge((Gauge) val, meterBo);
                // 耗时统计
            } else if (val instanceof HistogramSupport) {
                populateTimer((HistogramSupport) val, meterBo);
            } else if (val instanceof Counter) {
                populateCounter(((Counter) val), meterBo);
            }
            if (meterBo.getCount() <= 0) {
                return;
            }
            idMeterBoHashMap.put(val.getId(), meterBo);
        });
        return idMeterBoHashMap;
    }

    private void populateCounter(Counter val, MeterBo meterBo) {
        meterBo.setCount(val.count());

    }

    private MeterBo populateDefault(Meter val) {
        Meter.Id id = val.getId();
        MeterBo build = MeterBo.builder()
                .namespace(id.getName())
                .build();
        // init tag
        build.init();
        id.getTags().forEach(build::addTag);
        build.setBaseUnit(id.getBaseUnit());
        return build;
    }

    private void populateTimer(HistogramSupport val, MeterBo meterBo) {
        HistogramSnapshot histogramSnapshot = val.takeSnapshot();
        TimeUnit timeUnit = TimeUnit.MICROSECONDS;
        if (val instanceof Timer) {
            Timer timer = (Timer) val;
            timeUnit = timer.baseTimeUnit();
        }
        meterBo.setBaseUnit(timeUnit.toString());
        meterBo.setMax(histogramSnapshot.max(timeUnit));
        meterBo.setMean(histogramSnapshot.mean());
        meterBo.setCount(histogramSnapshot.count());
        meterBo.setTotal(histogramSnapshot.total(timeUnit));
        ValueAtPercentile[] valueAtPercentiles = histogramSnapshot.percentileValues();
        if (val instanceof Timer) {
            // 为了方便后面的TTL 链路统计使用
            TimerSnapshot.setMeter(val.getId(), meterBo, valueAtPercentiles);
        }
        meterBo.setP95(TimerSnapshot.getValWithPercent(val.getId(), TimePercentEnum.P95));
        meterBo.setP999(TimerSnapshot.getValWithPercent(val.getId(), TimePercentEnum.P999));
        meterBo.setP99(TimerSnapshot.getValWithPercent(val.getId(), TimePercentEnum.P99));
        meterBo.setP995(TimerSnapshot.getValWithPercent(val.getId(), TimePercentEnum.P995));
    }

    private void populateGauge(Gauge val, MeterBo meterBo) {
        double value = val.value();
        meterBo.setTotal(value);
        meterBo.setCount(value);
        meterBo.setMean(value);
        meterBo.setMax(value);
        meterBo.setMin(value);
        meterBo.setP95(value);
        meterBo.setP99(value);
        meterBo.setP995(value);
        meterBo.setP999(value);


    }

    public void setMetricExporter(MetricExporter exporter) {
        this.metricExporter = exporter;
    }
}
