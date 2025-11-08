package org.lkg.behavior_pattern.chain_responsibility.chain;

import org.lkg.behavior_pattern.chain_responsibility.Invocation.BizLoginInvocation;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/1/1 9:56 PM
 */
public class Chain_2 implements Chain{

    @Override
    public String toString() {
        return this.getClass().getName();
    }

    @Override
    public Object invoke(BizLoginInvocation bizLoginInvocation) {
        bizLoginInvocation.process();
        System.out.println(this +" after");
        return null;
    }
}
