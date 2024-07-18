package org.lkg.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.*;
import org.lkg.simple.JacksonUtil;
import org.lkg.simple.ObjectUtil;

import java.util.*;
import java.util.stream.Collectors;

import static org.lkg.simple.ObjectUtil.isEmpty;

/**
 * Description: 对内请求的参数
 * Author: 李开广
 * Date: 2024/2/27 7:30 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InternalRequest {
    private static final String GET = "GET";
    private static final String POST = "POST";


    private String method;

    private String url;

    private BodyEnum bodyEnum;

    private Map<String, String> headers = new HashMap<>();

    private Map<String, Object> body;

    public static InternalRequest createGetRequest(String url) {
        return createGetRequest(url, null, null, null);
    }


    public static InternalRequest createGetRequest(String url, BodyEnum bodyEnum, Map<String, Object> body) {
        return createGetRequest(url, bodyEnum, body, null);
    }


    public static InternalRequest createGetRequest(String url, BodyEnum bodyEnum, Map<String, Object> body, Map<String, String> headers) {
        return createRequest(url, GET, bodyEnum, body, headers);
    }

    public static InternalRequest createPostRequest(String url, BodyEnum bodyEnum) {
        return createPostRequest(url, bodyEnum, null, null);
    }


    public static InternalRequest createPostRequest(String url, BodyEnum bodyEnum, Map<String, Object> body) {
        return createPostRequest(url, bodyEnum, body, null);
    }

    public static InternalRequest createPostRequest(String url, BodyEnum bodyEnum, Map<String, Object> body, Map<String, String> headers) {
        return createRequest(url, POST, bodyEnum, body, headers);
    }

    private static InternalRequest createRequest(String url, String method, BodyEnum bodyEnum, Map<String, Object> body, Map<String, String> headers) {
        if (isEmpty(bodyEnum)) {
            bodyEnum = BodyEnum.URL_ENCODED;
        }
        InternalRequest request = InternalRequest.builder().body(body).method(method).bodyEnum(bodyEnum).build();
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

    public String bodyAsString() {
        if (isEmpty(bodyEnum) || isEmpty(body)) {
            return null;
        }
        return JacksonUtil.writeValue(body);
    }

    private void addHeader(String key, String object) {
        if (isEmpty(key)) {
            return;
        }
        if (isEmpty(headers)) {
            headers = new HashMap<>();
        }
        this.headers.put(key, object);
    }

    private void addHeaders(Map<String, String> paramHeader) {
        if (isEmpty(paramHeader)) {
            return;
        }
        if (isEmpty(headers)) {
            headers = new HashMap<>();
        }
        this.headers.putAll(paramHeader);
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
        RAW("application/json"),
        HTML("text/html");
        private final String contentTypeValue;

        public static boolean isUrlEncoded(BodyEnum bodyEnum) {
            return Objects.equals(bodyEnum, FORM_DATA) || Objects.equals(bodyEnum, URL_ENCODED);
        }
    }

}
