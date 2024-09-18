package org.lkg.algorithm.sharding;

import org.lkg.simple.DateTimeUtils;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/18 2:31 PM
 */
public interface TimeShardingConfig {

    int byDay = 1;
    // default
    int byMonth = 2;
    int byYear = 3;

    int byWay();

    default String getPattern() {
        switch (byWay()) {
            case byDay:
                return DateTimeUtils.YYYYMMDD_WITH_SHARDING;
            case byYear:
                return DateTimeUtils.YYYY_WITH_SHARDING;
            default:
                return DateTimeUtils.YYYYMM_WITH_SHARDING;
        }
    }

    default Temporal nextTemporal(Temporal temporal) {
        switch (byWay()) {
            case byDay:
                return temporal.plus(1, ChronoUnit.DAYS);
            case byYear:
                return temporal.plus(1, ChronoUnit.YEARS);
            default:
                return temporal.plus(1, ChronoUnit.MONTHS);
        }
    }
}
