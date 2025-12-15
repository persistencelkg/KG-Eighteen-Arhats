package org.lkg.behavior_pattern.chain_responsibility;

import org.lkg.behavior_pattern.chain_responsibility.chain.ChainTest;
import org.lkg.behavior_pattern.chain_responsibility.Invocation.BizLoginInvocation;

import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/1/1 9:52 PM
 */
public class BizLogicProcessManager implements BizLoginInvocation {

    private List<ChainTest> chainTestList;

    private int currentIndex = -1;


    public BizLogicProcessManager(List<ChainTest> list) {
        // 根据顺序执行或者倒序
        this.chainTestList = list;
    }

    @Override
    public Object process() {

        if (currentIndex == chainTestList.size() - 1) {
            // 最终结束的逻辑
            return null;
        }

//        if ( xxx) { other need recursive scene
//            return process();;
//        }

        return chainTestList.get(++currentIndex).invoke(this);
    }

}
