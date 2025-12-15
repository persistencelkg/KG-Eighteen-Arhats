package org.lkg.behavior_pattern.chain_responsibility;

import org.lkg.behavior_pattern.chain_responsibility.chain.ChainTest;
import org.lkg.behavior_pattern.chain_responsibility.chain.ChainRegistry;
import org.lkg.behavior_pattern.chain_responsibility.chain.SingleTonChainRegistry;

import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/1/1 9:57 PM
 */
public class ChainFactory {



    public List<ChainTest> getChainList() {
        ChainRegistry<? extends ChainTest> instance = SingleTonChainRegistry.getInstance();
        return (List<ChainTest>) instance.getChainList();
    }
}
