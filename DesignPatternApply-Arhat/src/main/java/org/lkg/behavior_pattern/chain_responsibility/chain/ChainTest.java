package org.lkg.behavior_pattern.chain_responsibility.chain;

import org.lkg.behavior_pattern.chain_responsibility.Invocation.BizLoginInvocation;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/1/1 9:55 PM
 */
public interface ChainTest {

   Object invoke(BizLoginInvocation bizLoginInvocation);
}
