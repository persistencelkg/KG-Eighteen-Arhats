package org.lkg.algorithm.sharding;

import com.google.common.collect.Range;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.lkg.algorithm.ConsistenceHash;
import org.lkg.utils.DateTimeUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Description: 根据时间划分的分表算法
 * Author: 李开广
 * Date: 2024/9/18 11:26 AM
 */
@Slf4j
public abstract class AbstractTimeShardingTableAlgorithm implements TimeShardingConfig, PreciseShardingAlgorithm<Timestamp>, RangeShardingAlgorithm<Timestamp> {


    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Timestamp> shardingValue) {
        // 按月分表
        Timestamp value = shardingValue.getValue();
        LocalDateTime localDateTime = value.toLocalDateTime();
        String suffix = DateTimeUtils.timeConvertToString(localDateTime, getPattern());
        String actualTableName = ConsistenceHash.joinWithSpit(shardingValue.getLogicTableName(), suffix);
        if (log.isDebugEnabled()) {
            log.debug("get actual time sharding val:{}", actualTableName);
        }
        return actualTableName;
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Timestamp> shardingValue) {
        Range<Timestamp> valueRange = shardingValue.getValueRange();
        System.out.println(valueRange);
        LocalDateTime start = valueRange.lowerEndpoint().toLocalDateTime();
        LocalDateTime end = valueRange.upperEndpoint().toLocalDateTime();
        ArrayList<String> hitTableNames = new ArrayList<>();
        while (start.plusNanos(1).isBefore(end)) {
            hitTableNames.add(ConsistenceHash.joinWithSpit(shardingValue.getLogicTableName(), DateTimeUtils.timeConvertToString(start, getPattern())));
            start = LocalDateTime.from(nextTemporal(start));
        }
        if (log.isDebugEnabled()) {
            log.debug("get actual time range sharding val:{}", hitTableNames);
        }
        return hitTableNames;
    }
}
