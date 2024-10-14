package org.lkg.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lkg.exception.IResponseEnum;

/**
 * @author likaiguang
 * @date 2023/2/20 8:59 下午
 */
@Getter
@AllArgsConstructor
public enum LoginExceptionEnum implements IResponseEnum {
    /**
     *
     */
    USERNAME_OR_PASSWORD_ERROR("U20001", "用户名或密码错误"),
    LOGIN_FAIL_COUNT_SLEEP_ERROR("U20002", "密码错误次数过多，请{0}秒后再试"),
    LOGIN_COUNT_USE_UP_ERROR("U20003", "今日登录失败次数({0}次)已经用尽，请次日再试"),

    VERIFY_CODE_NOT_MATCH_ERROR("U20100", "验证码错误"),
    VERIFY_CODE_EXPIRED_ERROR("U20100", "验证码失效"),

    TOKEN_EXPIRED_ERROR("U40001", "登录有效期已过，请重新登录")

    ;
    private final String code;
    private final String message;
}
