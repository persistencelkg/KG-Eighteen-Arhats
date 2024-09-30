package org.lkg.rocketmq.biz;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description:
 * Author: 李开广
 * Date: 2023/10/11 7:14 PM
 */
@Getter
@AllArgsConstructor
public enum DelayLevelEnum {
    FIRST_0S(0, "0s", 0L),
    SECOND_1S(1, "1s", 1L),
    THIRD_5S(2, "5s", 5L),
    FOURTH_10S(3, "10s", 10L),
    FIFTH_30S(4, "30s", 30L),
    SIXTH_1M(5, "1m",60L),
    SEVENTH_2M(6, "2m", 120L),
    EIGHTH_3M(7, "3m", 180L),
    NINTH_4M(8, "4m", 240L),
    TENTH_5M(9, "5m", 300L),
    ELEVENTH_6M(10, "6m", 360L),
    TWELFTH_7M(11, "7m", 420L),
    THIRTEENTH_8M(12, "8m", 480L),
    THIRTEENTH_9M(13, "9m", 540L),
    FOURTEENTH_10M(14, "10m", 600L),
    FIFTEENTH_20M(15, "20m", 1200L),
    SIXTEENTH_30M(16, "30m", 1800L),
    SEVENTEENTH_1H(17, "1h", 3600L),
    EIGHTEENTH_2H(18, "2h", 7200L);

    private final Integer level;

    private final String queueName;

    private final Long actualSecond;
}
