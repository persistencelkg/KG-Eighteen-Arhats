package org.lkg.exception;

import lombok.Getter;

/**
 * @description: 统一处理服务异常：
 * 1. 用户服务异常 【自定义为主】
 * 2. 系统异常 【默认】
 * 3. Http/RPC 调用异常 【默认】
 * @author likaiguang
 * @date  2021/3/13 9:29 下午
 **/
@Getter
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final IResponseEnum responseEnum;
    /**
     * 异常消息参数
     */
    protected Object[] args;

    public BaseException(IResponseEnum responseEnum) {
        super(responseEnum.getMessage());
        this.responseEnum = responseEnum;
    }



    public BaseException(String code, String message) {
        super(message);
        this.responseEnum = new IResponseEnum() {
            @Override
            public String getCode() {
                return code;
            }

            @Override
            public String getMessage() {
                return message;
            }
        };
    }

    public BaseException(IResponseEnum responseEnum, String message, Object... args) {
        super(message);
        this.responseEnum = responseEnum;
        this.args = args;
    }

    public BaseException(Throwable throwable, IResponseEnum responseEnum, String message, Object... args) {
        super(message, throwable);
        this.responseEnum = responseEnum;
        this.args = args;
    }


}
