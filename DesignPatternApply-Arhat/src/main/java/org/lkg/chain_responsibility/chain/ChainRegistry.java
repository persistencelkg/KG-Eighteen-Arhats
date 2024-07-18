package org.lkg.chain_responsibility.chain;

import org.lkg.chain_responsibility.chain.Chain;

import java.util.List;

/**
 * Description: 注册工程，也可以基于InitializeBean#afterPropertiesSet 配合 Map构建
 * Author: 李开广
 * Date: 2024/1/1 9:57 PM
 */
public interface ChainRegistry<T extends Chain> {

    /**
     * select optional，adapt Your apply product common Object
     * such spring bean，if you ensure your biz login chain，this way is not use
     * @param chain
     * @return
     */
    T unwrap(Object chain);

    void registerChain(T t);

    List<T> getChainList();
}
