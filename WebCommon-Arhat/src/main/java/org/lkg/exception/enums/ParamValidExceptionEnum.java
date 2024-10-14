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
public enum ParamValidExceptionEnum implements IResponseEnum {
    /**
     * 参数
     */
    VALID_ERROR("U10001", "请求参数错误，请检查后再试"),
    TIME_RANGE_ERROR("U10002", "非法的时间范围");



    private final String code;
    private final String message;
}


