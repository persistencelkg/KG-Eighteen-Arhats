package org.lkg.core.meter.histogram;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import org.lkg.core.meter.LongHengHistogramSupport;

import java.util.Arrays;

/**
 * 无时间单位的事件跟踪
 */
public class LongHengDistributionSummary extends LongHengHistogramSupport implements DistributionSummary {

    private final double scale;

    public LongHengDistributionSummary(Id id, DistributionStatisticConfig distributionStatisticConfig, double scale) {
        super(id, distributionStatisticConfig);
        this.scale = scale;
    }

    private void recordNonNegative(double amount) {
        histogramManager.record((long) amount);
    }

    @Override
    public void record(double amount) {
        amount *= scale;
        recordNonNegative(amount);
    }

    @Override
    public long count() {
        return histogramManager.count();
    }

    @Override
    public double totalAmount() {
        return histogramManager.total();
    }

    @Override
    public double max() {
        return histogramManager.getMax();
    }


    @Override
    public Iterable<Measurement> measure() {
        return Arrays.asList(
                new Measurement(() -> (double) count(), Statistic.COUNT),
                new Measurement(this::totalAmount, Statistic.TOTAL),
                new Measurement(this::max, Statistic.MAX)
        );
    }
}
