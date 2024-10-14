package org.lkg.exception;

/**
 * @author likaiguang
 * @date 2023/2/19 10:47 下午
 */
public interface IResponseEnum {

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

}