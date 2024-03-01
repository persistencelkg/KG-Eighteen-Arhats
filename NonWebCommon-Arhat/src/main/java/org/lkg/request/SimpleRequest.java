package org.lkg.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.lkg.simple.JacksonUtil;
import org.springframework.lang.Nullable;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.lkg.simple.ObjectUtil.isEmpty;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/2/27 7:30 PM
 */
@Data
@Builder
public class SimpleRequest {
    private static final String GET = "GET";
    private static final String POST = "POST";


    private String method;

    private String url;

    private BodyEnum bodyEnum;

    private final Map<String, String> headers = new HashMap<>();

    private final Map<String, Object> body;


    public static SimpleRequest createGetRequest(String url, BodyEnum bodyEnum, Map<String, Object> body) {
        return createGetRequest(url, bodyEnum, body, null);
    }


    public static SimpleRequest createGetRequest(String url, BodyEnum bodyEnum, Map<String, Object> body, Map<String, String> headers) {
        return createRequest(url, GET, bodyEnum, body, headers);
    }

    public static SimpleRequest createPostRequest(String url, BodyEnum bodyEnum) {
        return createPostRequest(url, bodyEnum, null, null);
    }

    public static SimpleRequest createPostRequest(String url, BodyEnum bodyEnum, Map<String, Object> body) {
        return createPostRequest(url, bodyEnum, body, null);
    }

    public static SimpleRequest createPostRequest(String url, BodyEnum bodyEnum, Map<String, Object> body, Map<String, String> headers) {
        return createRequest(url, POST, bodyEnum, body, headers);
    }

    private static SimpleRequest createRequest(String url, String method, BodyEnum bodyEnum, Map<String, Object> body, Map<String, String> headers) {
        if (isEmpty(bodyEnum)) {
            throw new IllegalArgumentException(" request bodyEnum lack:");
        }
        SimpleRequest request = SimpleRequest.builder().body(body).method(method).bodyEnum(bodyEnum).build();
        request.addUrl(url);
        request.addHeaders(headers);
        request.addHeader("content-type", bodyEnum.getContentTypeValue());
        return request;
    }

    private void addUrl(String url) {
        if (BodyEnum.isUrlEncoded(bodyEnum) && !isEmpty(body)) {
            url = url.contains("?") ? url + "&" : url + "?";
            url += body.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));
        }
        this.url = url;
    }

    public byte[] bodyAsBytes() {
        if (isEmpty(bodyEnum) || isEmpty(body)) {
            return null;
        }
        try {
            return JacksonUtil.getMapper().writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void addHeader(String key, String object) {
        if (!isEmpty(key)) {
            this.headers.put(key, object);
        }
    }

    private void addHeaders(Map<String, String> headers) {
        if (!isEmpty(headers)) {
            this.headers.putAll(headers);
        }
    }


    @AllArgsConstructor
    @Getter
    public enum BodyEnum {

        /**
         * <ul>
         *     <li>form-data:  Content-Type: multipart/form-data; boundary=--------------------------647477930321132028113971</li>
         *     <li>x-www-form-urlencoded:  Content-Type: application/x-www-form-urlencoded</li>
         *     <li>raw: Content-Type: application/json</li>
         * </ul>
         */
        FORM_DATA("multipart/form-data"),
        URL_ENCODED("application/x-www-form-urlencoded"),
        RAW("application/json");
        private final String contentTypeValue;

        public static boolean isUrlEncoded(BodyEnum bodyEnum) {
            return Objects.equals(bodyEnum, FORM_DATA) || Objects.equals(bodyEnum, URL_ENCODED);
        }
    }

}
