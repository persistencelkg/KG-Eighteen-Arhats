package org.lkg.behavior_pattern.chain_responsibility.v2;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/8 9:05 PM
 */
public interface OrderChain extends Chain{

    default int order() {
        return 0;
    }
}
