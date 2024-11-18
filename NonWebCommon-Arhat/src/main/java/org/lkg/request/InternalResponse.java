package org.lkg.request;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.lkg.enums.ResponseBodyEnum;
import org.lkg.simple.JacksonUtil;

import java.util.*;

/**
 * Description: 对内请求的响应：包括详细的返回信息
 * Author: 李开广
 * Date: 2024/2/27 7:31 PM
 */
@Data
@Slf4j
public class InternalResponse {

    private String result;

    private byte[] resultBytes;

    private LinkedList<Exception> exceptionList;

    private int statusCode;

    private InternalRequest internalRequest;

    private long costTime;

    private String latestResponseUrl;

    public InternalResponse(InternalRequest request) {
        this.internalRequest = request;
    }

    public <T> T toEntity(Class<T> tClass) {
        return JacksonUtil.readValue(result, tClass);
    }

    public <T> T toEntity(TypeReference<T> tClass) {
        return JacksonUtil.readObj(result, tClass);
    }

    public GenericCommonResp toGenericResp(ResponseBodyEnum responseBodyEnum) {
        return JacksonUtil.deserialize(result, responseBodyEnum);
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

    public void addException(Exception e) {
        if (Objects.isNull(exceptionList)) {
            exceptionList = new LinkedList<>();
        }
        exceptionList.add(e);
    }


    public boolean is4XXFail() {
        return statusCode > 400;
    }
}
