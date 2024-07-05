package org.lkg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

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

    public static boolean isTrue(int code) {
        return Objects.equals(TRUE.code, code);
    }


    public static boolean isFalse(int code) {
        return Objects.equals(FALSE.code, code);
    }
}
