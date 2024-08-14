package org.lkg.core.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.HistogramSupport;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.service.impl.SyncMetricExporter;
import org.lkg.metric.threadpool.TrackableThreadPoolUtil;
import org.lkg.simple.ObjectUtil;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/9 4:36 PM
 */
public class MetricExporterHandler {


    private MetricExporter metricExporter = new SyncMetricExporter();

    public void exportMeter(List<Meter> list) {
        if (ObjectUtil.isEmpty(list)) {
            return;
        }
        // CONVERT TO BO
        HashMap<Meter.Id, MeterBo> idMeterBoHashMap = convertToMeterBo(list);
        if (idMeterBoHashMap.isEmpty()) {
            return;
        }
        // publish
        MetricCoreExecutor.execute(() -> {
            metricExporter.publishMeter(idMeterBoHashMap);
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
            if (meterBo.getCount() == 0) {
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
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
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
        // p95 p99 通过界面来配置 而不是自己去计算
        if (val instanceof Timer) {
            // 为了方便后面的TTL 链路统计使用
            TimerSnapshot.setMeter(val.getId(), meterBo, valueAtPercentiles);
        }
        final double threshold = 0.00001;

        for (ValueAtPercentile valueAtPercentile : valueAtPercentiles) {
            double value = valueAtPercentile.value(timeUnit);
            double percentile = valueAtPercentile.percentile();
            if (Math.abs(percentile - 0.95) < threshold) {
                meterBo.setP95(value);
            }
            if (Math.abs(percentile - 0.99) < threshold) {
                meterBo.setP99(value);
            }
            if (Math.abs(percentile - 0.995) < threshold) {
                meterBo.setP995(value);
            }
            if (Math.abs(percentile - 0.999) < threshold) {
                meterBo.setP999(value);
            }
        }

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
