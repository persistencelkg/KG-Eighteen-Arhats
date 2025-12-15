package org.lkg.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lkg.exception.IErrorCode;

/**
 * @author likaiguang
 * @date 2023/2/20 8:52 下午
 */
@Getter
@AllArgsConstructor
public enum CommonExceptionEnum implements  IErrorCode {
    SAL_EXEC_SUCCESS("100000", "", IErrorType.SAL, IErrorLevel.INFO),

    /**
     * 多由于超时引起的异常
     */
    TIME_OUT_SAL_ERROR("100001", "调用外部超时", IErrorType.SAL, IErrorLevel.ERROR),
    UNKNOWN_SYS_ERROR("100002", "未受检查的系统错误", IErrorType.SYSTEM, IErrorLevel.ERROR),
    UNKNOWN_SAL_EXEC_ERROR("100003", "外部调用未知异常", IErrorType.SYSTEM, IErrorLevel.ERROR),

    /**
     * 参数
     */
    PARAM_VALID_ERROR("100003", "请求参数错误", IErrorType.BIZ, IErrorLevel.ERROR),

    SERVICE_INVOKE_ERROR("200000", "服务调用异常", IErrorType.SYSTEM, IErrorLevel.ERROR)

    ;



    private final String code;
    private final String message;
    private final String errorPrefix;
    private final IErrorType iErrorType;
    private final IErrorLevel iErrorLevel;

   CommonExceptionEnum(String code, String message, IErrorType iErrorType, IErrorLevel iErrorLevel) {
       this.code = code;
       this.message = message;
       this.iErrorLevel = iErrorLevel;
       this.iErrorType = iErrorType;
       this.errorPrefix = "KG";
   }
}


