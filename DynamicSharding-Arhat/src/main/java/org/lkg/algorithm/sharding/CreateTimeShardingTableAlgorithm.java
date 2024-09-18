package org.lkg.algorithm.sharding;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/18 3:04 PM
 */
public class CreateTimeShardingTableAlgorithm extends AbstractTimeShardingTableAlgorithm{
    @Override
    public int byWay() {
        return byMonth;
    }

}
