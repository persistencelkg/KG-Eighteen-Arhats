package org.lkg.chain_responsibility.chain;

import org.lkg.chain_responsibility.Invocation.BizLoginInvocation;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/1/1 9:55 PM
 */
public interface Chain {

   Object invoke(BizLoginInvocation bizLoginInvocation);
}
