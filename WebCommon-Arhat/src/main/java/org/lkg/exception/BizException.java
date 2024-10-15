package org.lkg.exception;

import org.lkg.exception.enums.BizExceptionEnum;

/**
 * Description: 通用业务异常
 * Author: 李开广
 * Date: 2024/10/14 8:30 PM
 */
public class BizException extends BaseException{

    public BizException(BizExceptionEnum responseEnum) {
        super(responseEnum);
    }
}
