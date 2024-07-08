package org.lkg.chain_responsibility.chain;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 默认Chain管理注册器
 * Author: 李开广
 * Date: 2024/1/1 10:16 PM
 */
public class DefaultChainRegistry implements ChainRegistry {

    private final List<Chain> list = new ArrayList<Chain>();

    private DefaultChainRegistry() {
        // 也可以通过适配器方式，将Chain的普通注册留给Chained
        // list添加的是Adapter，然后在getChainList获取Chained和ChainAdapter进行扩展和解耦，主要是为了扩展
        registerChain(new Chain_1());
        registerChain(new Chain_2());
    }

    @Override
    public Chain unwrap(Object chain) {
        return (Chain) chain;
    }

    @Override
    public void registerChain(Chain chain) {
        list.add(chain);
    }

    @Override
    public List<Chain> getChainList() {
        return list;
    }

    public static class SingleTonChangeRegistry {
        private final static DefaultChainRegistry INSTANCE = new DefaultChainRegistry();
        public static ChainRegistry<? extends Chain> getInstance() {
            return INSTANCE;
        }
    }
}
