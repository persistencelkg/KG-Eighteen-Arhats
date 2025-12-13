package org.lkg.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 异常级别
 * @date: 2025/12/13 20:05
 * @author: li kaiguang
 */
@Getter
@AllArgsConstructor
public enum IErrorLevel {

    TRACE,

    DEBUG,

    INFO,

    WARN,

    /**
     * 错误
     */
    ERROR

    ;
}
