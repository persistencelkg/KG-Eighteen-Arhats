package org.lkg.core.meter;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.lkg.core.bo.TimePercentEnum;
import org.lkg.simple.ObjectUtil;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 *
 * Description: 操作histogram的核心
 * Author: 李开广
 * Date: 2024/9/2 2:06 PM
 */
public class HistogramManager {
    // 计算百分比pxx的核心 key: 采样样本 value 累计值
    private final Map<Long, LongAdder> longAdderMap = new HashMap<>();
    private final LongAdder count = new LongAdder();
    private final LongAdder total = new LongAdder();
    private final AtomicLong min = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong max = new AtomicLong(Long.MIN_VALUE);

    private final double[] percentArr;
    private final double[] internalPercentArr = TimePercentEnum.percentValues();

    public HistogramManager(double[] percentiles) {
        this.percentArr = mergerPercent(percentiles);
    }

    private double[] mergerPercent(double[] percentiles) {
        TreeSet<Double> ts = new TreeSet<>(Comparator.reverseOrder());
        if (ObjectUtil.isNotEmpty(percentiles)) {
            for (double percentile : percentiles) {
                ts.add(percentile);
            }
        }
        for (double v : internalPercentArr) {
            ts.add(v);
        }
        return ts.stream().mapToDouble(Double::doubleValue).toArray();
    }

    public void record(Long record) {
        longAdderMap.computeIfAbsent(record, ref ->  new LongAdder()).add(1);
        count.add(1);
        total.add(record);
        min.updateAndGet(old -> Math.min(old, record));
        max.updateAndGet(old -> Math.max(old, record));
    }


    public LongHengHistogramSnapshot getSnapShot() {
        return new LongHengHistogramSnapshot(percentArr, longAdderMap, getMax(), getMin(), count(), total());
    }


    public long getMax() {
        return count() == 0 ? 0 : max.get();
    }

    public long getMin() {
        return count() == 0 ? 0 : min.get();
    }

    public long count() {
        return count.longValue();
    }

    public double total() {
        return total.doubleValue();
    }

    public void reset() {
        count.reset();
        total.reset();
        min.set(Long.MAX_VALUE);
        max.set(Long.MIN_VALUE);
        longAdderMap.clear();
    }
}
