package org.lkg.algorithm;

/**
 * Description: hash环的sharding配置
 * Author: 李开广
 * Date: 2024/9/18 2:15 PM
 */
public interface HashCircleNodeConfig {

    int N_NODE_COUNT = 65535;

    int SUGGEST_ACTUAL_COUNT = 128;

    default int getNodeCount() {
        return SUGGEST_ACTUAL_COUNT;
    }

    default int getVirtualCount() {
        return N_NODE_COUNT / getNodeCount();
    }

    default CommonHashAlgorithm getShardingAlgorithm() {
        return CommonHashAlgorithm.NATIVE_HASH;
    }
}
