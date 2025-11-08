package org.lkg.behavior_pattern.chain_responsibility.v2;

import lombok.Data;

import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/8 9:12 PM
 */
@Data
public class ChainBuilder {

    private DutyChain dutyChain;

    // other chain


    public void buildChain(WorkHandler workHandler) {
        if (workHandler instanceof  DefaultWorkHandler) {
            if (Objects.isNull(dutyChain)) {
                dutyChain = new DutyChain((DefaultWorkHandler) workHandler);
            } else {
                dutyChain.addLast(((DefaultWorkHandler) workHandler));
            }
        }
        // else other workHandler
    }
}
