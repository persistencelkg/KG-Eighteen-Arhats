package org.lkg.behavior_pattern.chain_responsibility.v2;

import lombok.Data;

import java.util.Objects;

/**
 * Description: 责任链第二版
 * Author: 李开广
 * Date: 2024/7/8 9:03 PM
 */
@Data
public class DutyChain implements WorkHandler{

    private DutyChain next;

    private DefaultWorkHandler workHandler;


    public DutyChain(DefaultWorkHandler workHandler) {
        this.workHandler = workHandler;
    }

    public void addLast(DefaultWorkHandler workHandler) {
        DutyChain dutyChain = this;
        while(Objects.nonNull(dutyChain.next)) {
            dutyChain = dutyChain.next;
        }
        dutyChain.setWorkHandler(workHandler);
    }

    @Override
    public void beforeWork() {
        this.workHandler.beforeWork();
        if (Objects.nonNull(this.next)){
            this.next.beforeWork();
        }
    }

    @Override
    public void afterWork() {
        this.workHandler.afterWork();
        if (Objects.nonNull(this.next)){
            this.next.afterWork();
        }
    }
}
