package org.lkg.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lkg.exception.IResponseEnum;

/**
 * @author likaiguang
 * @date 2023/2/20 8:52 下午
 */
@Getter
@AllArgsConstructor
public enum BizExceptionEnum implements IResponseEnum {

    /**
     * 多由于超时引起的异常
     */
    TIME_OUT_ERROR("S00001", "系统繁忙，请稍后再试" ),
    UNCHECKED_ERROR("S00002", "系统出错，请联系客服" ),
    /**
     * 无法被感知的异常，需要额外关注，例如oom，StackOverflowError
     */
    UNKNOWN_ERROR("S00004", "系统繁忙，请联系客服" ),
    SHARDING_ROUTE_ERROR("S00002", "数据分片异常")
    ;



    private final String code;
    private final String message;
}


