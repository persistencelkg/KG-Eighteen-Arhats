package org.lkg.behavior_pattern.chain_responsibility.v2;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/8 9:04 PM
 */
public interface WorkHandler extends OrderChain{


    void beforeWork();

    void afterWork();

}
