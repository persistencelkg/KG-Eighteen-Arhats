package org.lkg.factory;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/23 4:59 PM
 */
@Getter
@AllArgsConstructor
public enum StageStepEnum {

     INIT_STEP("init"),
    CALC_STEP("calc"),


    ;
    private final String desc;
}
