package org.lkg.core.meter;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/29 9:51 PM
 */
public interface LongHengSnapShot {

    default double getCount() {
        return 0;
    }

    default double getTotal() {
        return 0;
    }

    default double getAvg() {
        return 0;
    }

    default double getMin() {
        return 0;
    }

    default double getMax() {
        return 0;
    }

    default double getValue() {
        return 0;
    }

    default double getPercentile(double percent){
        return 0;
    }
}
