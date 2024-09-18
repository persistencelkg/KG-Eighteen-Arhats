package org.lkg.algorithm.sharding;

/**
 * Description: 仅供演示
 * Author: 李开广
 * Date: 2024/9/18 2:24 PM
 */
public class UserIdMockPreciseShardingTableAlgorithm extends AbstractHashPreciseShardingTableAlgorithm {

    @Override
    public int getNodeCount() {
        return 4;
    }

}
