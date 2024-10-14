package org.lkg.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lkg.exception.IResponseEnum;

/**
 * @author likaiguang
 * @date 2023/2/21 12:51 下午
 */
@AllArgsConstructor
@Getter
public enum PermissionExceptionEnum implements IResponseEnum {

    /**
     *
     */
    NOT_AUTH_ERROR("U40401", "你没有访问权限"),
    VIEW_FREQUENT_ERROR("U40402", "访问太过频繁，请{0}秒后再试")
    ;
    private final String code;
    private final String message;
}
