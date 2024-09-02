package org.lkg.core.meter;

import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/29 9:51 PM
 */
public class LongHengHistogramSnapshot implements LongHengSnapShot {
    private final long count;

    private final double total;

    private final double max;

    private final double min;

    private final double variance;

    private double avg;


    private final HistogramSnapshot internalSnapshot;

    private static final CountAtBucket[] EMPTY_COUNT_AT_BUCKETS = new CountAtBucket[0];

    public LongHengHistogramSnapshot(double[] percentArr, Map<Long, LongAdder> longAdderMap, long max, long min, long count, double total) {
        this.count = count;
        this.total = total;
        this.max = max;
        this.min = min;
        this.avg = 0;
        ValueAtPercentile[] valueAtPercentile;
        int n = percentArr.length;
        // 方差 = [（样本0 - 样本均值)^2  + （样本1 - 样本均值)^2 .....  +（样本n - 样本均值)^2  ] / [n - 1]
        double variance = 0;
        int index = 0;
        if (total <= 0) {
            valueAtPercentile = new ValueAtPercentile[0];
        } else {
            valueAtPercentile = new ValueAtPercentile[percentArr.length];
            this.avg = this.count / this.total;
            AtomicLong accumulator = new AtomicLong(0);
            // 按最近的值去计算pxx
            TreeMap<Long, Long> countMap = new TreeMap<>(Comparator.reverseOrder());
            longAdderMap.forEach((k, v) -> countMap.put(k, v.longValue()));

            for (Map.Entry<Long, Long> entry : countMap.entrySet()) {
                long k = entry.getKey();
                long val = entry.getValue();
                variance += Math.pow(k - avg, 2);
                accumulator.addAndGet(val);
                double currentPercent = 1.0d - (accumulator.get() * 1.0d / count);
                while (index < n && currentPercent < percentArr[index]) {
                    valueAtPercentile[index] = new ValueAtPercentile(percentArr[index++], k);
                }
            }
        }

        this.variance = variance / (longAdderMap.size() - 1);
        this.internalSnapshot = new HistogramSnapshot(count, total, max, valueAtPercentile, EMPTY_COUNT_AT_BUCKETS, (p, d) -> {
        });
    }

    @Override
    public HistogramSnapshot getInternalSnapshot() {
        return internalSnapshot;
    }


    @Override
    public double getMax() {
        return max;
    }

    @Override
    public double getMin() {
        return min;
    }

    @Override
    public double getAvg() {
        return avg;
    }

    @Override
    public double getCount() {
        return count;
    }

    @Override
    public double getTotal() {
        return total;
    }


    public static void main(String[] args) {
        Map<Long, LongAdder> map = new HashMap<>();
        long a = 0;
        long c = 0;
        for (long i = 0; i < 50000; i++) {
            LongAdder count = new LongAdder();
            count.increment();
            map.put(i, count);
            a++;
            c += i;
//            try {
//                TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 10));
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        }

        LongHengHistogramSnapshot snapshot = new LongHengHistogramSnapshot(new double[]{0.995, 0.99, 0.95, 0.5}, map, 10000, 10000, a, c);
        System.out.println(snapshot.getInternalSnapshot());
    }
}
