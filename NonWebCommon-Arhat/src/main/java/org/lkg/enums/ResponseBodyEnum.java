package org.lkg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * Description: 兼容不同响应结果
 * Author: 李开广
 * Date: 2024/11/15 4:34 PM
 */
@Getter
@AllArgsConstructor
public enum ResponseBodyEnum implements Serializable {

    DATA_CODE_MESSAGE("code", "message", "data"),
    DATA_CODE_MSG("code", "msg", "data"),

    RESULT_CODE_MESSAGE("code", "msg", "result"),
    RESULT_CODE_MSG("code", "message", "result"),


    DATA_ERR_CODE_MESSAGE("errcode", "message", "data"),

    ;
    private String code;
    private String message;
    private String data;

}
