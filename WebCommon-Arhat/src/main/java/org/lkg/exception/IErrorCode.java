package org.lkg.exception;

import org.lkg.exception.enums.IErrorLevel;
import org.lkg.exception.enums.IErrorType;

/**
 * @author likaiguang
 * @date 2023/2/19 10:47 下午
 */
public interface IErrorCode {

    /**
     * 自定义的状态码
     *
     * @return 5位消息code : 类型标识 + 数字
     */
    String getCode();


    /**
     * 自定义的异常消息
     *
     * @return 消息内容
     */
    String getMessage();


    /**
     * 错误级别
     * @return
     */
    IErrorLevel getIErrorLevel();


    /**
     * 错误类型
     * @return
     */
    IErrorType getIErrorType();


    /**
     * 错误码前缀
     * @return
     */
    String getErrorPrefix();
}