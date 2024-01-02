package org.lkg.chain_responsibility;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/1/1 10:36 PM
 */
public class StartResponse {

    public static void main(String[] args) {
        ChainFactory chainFactory = new ChainFactory();
        try {
            // response chain always need current Context info
            ChainContext.setContext(new Object[]{});

            System.out.println(new BizLogicProcessManager(chainFactory.getChainList()).process());
        } finally {
            ChainContext.setContext(null);
        }

    }
}
