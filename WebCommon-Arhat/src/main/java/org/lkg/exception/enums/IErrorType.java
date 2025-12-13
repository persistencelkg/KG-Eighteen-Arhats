package org.lkg.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @date: 2025/12/13 20:03
 * @author: li kaiguang
 */
@Getter
@AllArgsConstructor
public enum IErrorType {

    /**
     * 内部业务，关注量级
     */
    BIZ,

    /**
     * 内部系统异常：非预期的异常，关注单条报错
     */
    SYSTEM,


    /**
     * 外部调用，包括两方、三方调用
     */
    SAL

    ;
}
