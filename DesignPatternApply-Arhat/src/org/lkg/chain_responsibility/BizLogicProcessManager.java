package org.lkg.chain_responsibility;

import org.lkg.chain_responsibility.Invocation.BizLoginInvocation;
import org.lkg.chain_responsibility.chain.Chain;

import java.util.List;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/1/1 9:52 PM
 */
public class BizLogicProcessManager implements BizLoginInvocation {

    private List<Chain> chainList;

    private int currentIndex = -1;


    public BizLogicProcessManager(List<Chain> list) {
        // 根据顺序执行或者倒序
        this.chainList = list;
    }

    @Override
    public Object process() {

        if (currentIndex == chainList.size() - 1) {
            // 最终结束的逻辑
            return null;
        }

//        if ( xxx) { other need recursive scene
//            return process();;
//        }

        return chainList.get(++currentIndex).invoke(this);
    }

}
