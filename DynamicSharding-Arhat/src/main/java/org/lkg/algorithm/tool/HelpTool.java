package org.lkg.algorithm.tool;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.lkg.algorithm.sharding.UserIdMockPreciseShardingTableAlgorithm;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/18 3:36 PM
 */
@Slf4j
public class HelpTool {

    public static String getTableName(Long val) {
        UserIdMockPreciseShardingTableAlgorithm userIdMockPreciseShardingTableAlgorithm = new UserIdMockPreciseShardingTableAlgorithm();
        String s = userIdMockPreciseShardingTableAlgorithm.doSharding(null, new PreciseShardingValue<>("user", "user_id", val));
        log.info("hit table:{}", s);
        return s;
    }

    public static void main(String[] args) {
        long start;
        for (int i = 0; i < 10 ; i++) {
            Long aLong = (long) (Math.random() * 100000L);
            getTableName(aLong);
        }

    }
}
