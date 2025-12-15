package org.lkg.behavior_pattern.chain_responsibility.chain;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 默认Chain管理注册器
 * Author: 李开广
 * Date: 2024/1/1 10:16 PM
 */
public class DefaultChainRegistry implements ChainRegistry {

    private final List<ChainTest> list = new ArrayList<ChainTest>();

    private DefaultChainRegistry() {
        // 也可以通过适配器方式，将Chain的普通注册留给Chained
        // list添加的是Adapter，然后在getChainList获取Chained和ChainAdapter进行扩展和解耦，主要是为了扩展
        registerChain(new Chain_Test_1());
        registerChain(new Chain_Test_2());
    }

    @Override
    public ChainTest unwrap(Object chain) {
        return (ChainTest) chain;
    }

    @Override
    public void registerChain(ChainTest chainTest) {
        list.add(chainTest);
    }

    @Override
    public List<ChainTest> getChainList() {
        return list;
    }

    public static class SingleTonChangeRegistry {
        private final static DefaultChainRegistry INSTANCE = new DefaultChainRegistry();
        public static ChainRegistry<? extends ChainTest> getInstance() {
            return INSTANCE;
        }
    }
}
