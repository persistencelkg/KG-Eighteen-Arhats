package org.lkg.core.service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.distribution.HistogramSupport;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.service.impl.KafkaMetricExporter;
import org.lkg.simple.ObjectUtil;
import org.lkg.thread.ThreadPoolUtil;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/9 4:36 PM
 */
public class MetricExporterHandler {

    private static final ExecutorService publishExecutorService = ThreadPoolUtil.newNonBizExecutor("metric-publish");

    private MetricExporter metricExporter = new KafkaMetricExporter();

    public void exportMeter(List<Meter> list) {
        if (ObjectUtil.isEmpty(list)) {
            return;
        }
        // CONVERT TO BO
        HashMap<Meter.Id, MeterBo> idMeterBoHashMap = convertToMeterBo(list);


        // publish
        publishExecutorService.execute(() -> {
            metricExporter.publishMeter(idMeterBoHashMap);
        });
    }

    private HashMap<Meter.Id, MeterBo> convertToMeterBo(List<Meter> list) {
        HashMap<Meter.Id, MeterBo> idMeterBoHashMap = new HashMap<>(list.size());
        list.forEach(val -> {
            MeterBo meterBo = populateDefault(val, meterBo);
            if (val instanceof Gauge) {
                populateGauge((Gauge) val, meterBo);
            } else if (val instanceof HistogramSupport) {
                populateTimer((HistogramSupport) val, meterBo);
            }
            if (meterBo.getCount() == 0) {
                return;
            }
        });
        return null;
    }

    private void populateDefault(Meter val, MeterBo meterBo) {
        Meter.Id id = val.getId();
    }

    private void populateTimer(HistogramSupport val, MeterBo meterBo) {

    }

    private void populateGauge(Gauge val, MeterBo meterBo) {

    }

    public void setMetricExporter(MetricExporter exporter) {
        this.metricExporter = exporter;
    }
}
