package org.lkg.algorithm.sharding;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.lkg.algorithm.ConsistenceHash;
import org.lkg.algorithm.ConsistenceHashFactory;
import org.lkg.algorithm.HashCircleNodeConfig;

import java.util.Collection;

/**
 * 实践告诉我们 基于user_id 能解决很多问题
 * Description:基于Long的in、eq
 * Author: 李开广
 * Date: 2024/9/18 11:26 AM
 */
@Slf4j
public abstract class AbstractHashPreciseShardingTableAlgorithm implements PreciseShardingAlgorithm<Long>, HashCircleNodeConfig {

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
        ConsistenceHash consistenceHash = ConsistenceHashFactory.buildConsistenceHash(getShardingAlgorithm(), getNodeCount(), getVirtualCount());
        String actualNode = consistenceHash.getActualNode(shardingValue.getValue().toString());
        String actualTableName = ConsistenceHash.joinWithSpit(shardingValue.getLogicTableName(), actualNode);
        if (log.isDebugEnabled()) {
            log.info("get actual table name:{}", actualTableName);
        }
        return actualTableName;
    }
}
