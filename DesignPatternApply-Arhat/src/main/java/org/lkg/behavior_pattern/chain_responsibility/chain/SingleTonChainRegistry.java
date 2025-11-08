package org.lkg.behavior_pattern.chain_responsibility.chain;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/1/1 10:26 PM
 */
public class SingleTonChainRegistry {


    public static ChainRegistry<? extends Chain> getInstance() {
        return DefaultChainRegistry.SingleTonChangeRegistry.getInstance();
    }

}
