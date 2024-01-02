package org.lkg.chain_responsibility.chain;

import org.lkg.chain_responsibility.Invocation.BizLoginInvocation;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/1/1 9:55 PM
 */
public class Chain_1 implements Chain{

    @Override
    public String toString() {
        return this.getClass().getName();
    }

    @Override
    public Object invoke(BizLoginInvocation bizLoginInvocation) {
        System.out.println(this +" before start");
        bizLoginInvocation.process();
        return null;
    }
}
