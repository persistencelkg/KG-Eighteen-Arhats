package org.lkg.core.meter.histogram;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.util.TimeUtils;
import org.lkg.core.meter.LongHengHistogramSupport;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/2 5:05 PM
 */
public class LongHengTimer extends LongHengHistogramSupport implements Timer {
    private final TimeUnit baseTimeUnit;

    public LongHengTimer(Id id, DistributionStatisticConfig distributionStatisticConfig, TimeUnit timeUnit) {
        super(id, distributionStatisticConfig);
        this.baseTimeUnit = timeUnit;
    }

    @Override
    public void record(long amount, TimeUnit unit) {
        if (amount >= 0) {
            recordNonNegative(amount, unit);
        }
    }

    private void recordNonNegative(long amount, TimeUnit unit) {
        histogramManager.record((long) TimeUtils.convert(amount, unit, baseTimeUnit));
    }

    @Override
    public <T> T record(Supplier<T> f) {
        final long s = System.nanoTime();
        try {
            return f.get();
        } finally {
            record(Duration.ofNanos(System.nanoTime() - s));
        }
    }

    @Override
    public <T> T recordCallable(Callable<T> f) throws Exception {
        final long s = System.nanoTime();
        try {
            return f.call();
        } finally {
            record(Duration.ofNanos(System.nanoTime() - s));
        }
    }

    @Override
    public void record(Runnable f) {
        final long s = System.nanoTime();
        try {
            f.run();
        } finally {
            record(Duration.ofNanos(System.nanoTime() - s));
        }
    }

    @Override
    public long count() {
        return histogramManager.count();
    }

    @Override
    public double totalTime(TimeUnit unit) {
        return TimeUtils.nanosToUnit(histogramManager.total(), unit);
    }

    @Override
    public double max(TimeUnit unit) {
        return TimeUtils.nanosToUnit(histogramManager.getMax(), unit);
    }


    @Override
    public TimeUnit baseTimeUnit() {
        return baseTimeUnit;
    }
}
