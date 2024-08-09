package org.lkg.simple;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Description：
 * Author: li kai guang
 */
@Slf4j
public class JacksonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        // 美化输出
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // 输出时将属性变成小写带下划线 输入时还原成javaBean格式
//        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCaseStrategy.SNAKE_CASE);
        // 映射未知属性不抛出异常
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
        //
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, Boolean.TRUE);
        // 不序列化 值为null的数据
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static String writeValue(Object any) {
        try {
            return mapper.writeValueAsString(any);
        } catch (Exception e) {
            log.error("writeValue json exception, {}", e.getMessage(), e);
        }
        return null;
    }

    public static <T> T readValue(String json, Class<T> T) {
        try {
            return mapper.readValue(json, T);
        } catch (IOException e) {
            log.error("readValue convert json to object exception, {}", e.getMessage(), e);
        }
        return null;
    }

    public static <T> List<T> readList(String json, Class<T> T) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, T));
        } catch (IOException e) {
            log.error("readList convert json to object list exception, {}", e.getMessage(), e);
        }
        return null;
    }

    public static <T> T readObj(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (IOException e) {
            log.error("readObj convert json to object list exception, {}", e.getMessage(), e);
        }
        return null;
    }

    public static Map<String, Object> objToMap(Object obj) {
        String s = writeValue(obj);
        Map<String, Object> map = new HashMap<>();
        if (Objects.nonNull(s)) {
            return readObj(s, new TypeReference<HashMap<String, Object>>() {});
        }
        return map;
    }


//    public static void main(String[] args) {
//        String s = writeValue(new BeanUtil());
//        System.out.println(s);
//    }

}
