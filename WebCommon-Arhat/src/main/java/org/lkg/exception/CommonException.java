package org.lkg.exception;

import lombok.Getter;

/**
 * Description: 通用业务异常
 * Author: 李开广

 * Date: 2024/10/14 8:30 PM
 */
@Getter
public class CommonException extends RuntimeException{


    private final IErrorCode iErrorCode;
    /**
     * 对客返回错误信息，如果未定义将采用IErrorCode
     */
    private final String tipMsg;
    CommonException(IErrorCode iErrorCode) {
        super(iErrorCode.getMessage());
        this.iErrorCode = iErrorCode;
        this.tipMsg = iErrorCode.getMessage();
    }

    CommonException(IErrorCode iErrorCode, String tipMsg) {
        super(iErrorCode.getMessage());
        this.iErrorCode = iErrorCode;
        this.tipMsg = tipMsg;
    }


    CommonException(IErrorCode iErrorCode, String tipMsg, Throwable th) {
        super(iErrorCode.getMessage(), th);
        this.iErrorCode = iErrorCode;
        this.tipMsg = tipMsg;
    }

    public static CommonException fail(IErrorCode iErrorCode) {
        return new CommonException(iErrorCode);
    }

    public static CommonException fail(IErrorCode iErrorCode, String tipMsg) {
        return new CommonException(iErrorCode, tipMsg);
    }

    public static CommonException fail(IErrorCode iErrorCode, String tipMsg, Throwable th) {
        return new CommonException(iErrorCode, tipMsg, th);
    }
}
