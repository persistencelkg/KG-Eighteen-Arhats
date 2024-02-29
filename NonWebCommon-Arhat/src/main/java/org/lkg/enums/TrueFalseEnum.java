package org.lkg.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/2/28 9:15 PM
 */
@AllArgsConstructor
@Getter
public enum TrueFalseEnum {

    TRUE(1, "true"), FALSE(0, "false")
    ;
    private final Integer code;
    private final String desc;


}
