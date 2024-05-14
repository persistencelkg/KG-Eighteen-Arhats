package org.lkg.request;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.lkg.simple.JacksonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/2/27 7:31 PM
 */
@Data
@Slf4j
public class SimpleResponse {

    private String body;

    private String url;

    private Exception exception;

    private Map<String, List<String>> header;

    private int statusCode;

    public <T> T toEntity(Class<T> tClass) {
        return JacksonUtil.readValue(body, tClass);
    }

    public <T> T toEntity(TypeReference<T> tClass) {
        return JacksonUtil.readObj(body, tClass);
    }

    public boolean is2XXSuccess() {
        return statusCode >= 200 && statusCode < 400;
    }

    public boolean isSuccess(String statusCode, Object expectSuccessCode) {
        HashMap<String, Object> result = toEntity(new TypeReference<HashMap<String, Object>>() {
        });
        if (Objects.isNull(result) || !result.containsKey(statusCode) || !Objects.equals(result.get(statusCode), expectSuccessCode)) {
//            log.info("response unexpect result:{}", this);
            return false;
        }
        return true;
    }


    public boolean is4XXFail() {
        return statusCode > 400;
    }
}
