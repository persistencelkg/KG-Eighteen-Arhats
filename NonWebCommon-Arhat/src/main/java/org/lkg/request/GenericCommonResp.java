package org.lkg.request;

import lombok.*;
import org.lkg.utils.JacksonUtil;
import org.lkg.utils.ObjectUtil;

import java.util.List;
import java.util.Optional;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/15 4:48 PM
 */

@AllArgsConstructor
@ToString
@Getter
public class GenericCommonResp {

    private final Object code;

    private final String data;

    private final String msg;

    private final boolean dataIsArr;

    private final boolean codeIsStr;

    public static  <T> List<T> safeList(GenericCommonResp resp, Class<T> resultClass) {
        return Optional.ofNullable(resp).map(ref -> ref.unSafeGetList(resultClass)).orElse(null);
    }

    public static <T> T safeGet(GenericCommonResp resp, Class<T> resultClass) {
        return Optional.ofNullable(resp).map(ref -> ref.unSafeGet(resultClass)).orElse(null);
    }


    public <T> List<T> unSafeGetList(Class<T> resultClass) {
        if (!dataIsArr) {
            throw new UnsupportedOperationException("data can't not convert to array of class: " + resultClass);
        }
        if (ObjectUtil.isEmpty(data)) {
            return null;
        }
        return JacksonUtil.readList(data, resultClass);
    }

    public <T> T unSafeGet(Class<T> resultClass) {
        if (dataIsArr) {
            throw new UnsupportedOperationException("data is typeof arr, can't not convert to obj class: " + resultClass);
        }
        if (ObjectUtil.isEmpty(data)) {
            return null;
        }
        return JacksonUtil.readValue(data, resultClass);
    }


}
