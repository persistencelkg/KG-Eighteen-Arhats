package org.lkg.simple;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.lkg.request.InternalRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

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
        Collection<T> ts = readCollection(json, T);
        return Objects.isNull(ts) ? null : new ArrayList<>(ts);
    }

    public static <T> T readObj(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (IOException e) {
            log.error("readObj convert json to object list exception, {}", e.getMessage(), e);
        }
        return null;
    }

    public static Map<String, Object> readMap(String json) {
        return readMap(json, String.class, Object.class);
    }

    public static <K,V> Map<K, V> readMap(String json, Class<K> keyClass, Class<V> valueClass) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructMapType(Map.class, keyClass, valueClass));
        } catch (IOException e) {
            log.error("readCollection convert json to object list exception, {}", e.getMessage(), e);
        }
        return null;
    }

    public static <K,V> Map<K, V> readMap(String json, Class<K> keyClass, TypeReference<V> valueClass) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructMapType(Map.class,
                    mapper.getTypeFactory().constructType(keyClass),
                    mapper.getTypeFactory().constructType(valueClass)));
        } catch (IOException e) {
            log.error("readCollection convert json to object list exception, {}", e.getMessage(), e);
        }
        return null;
    }


    public static Map<String, Object> objToMap(Object obj) {
        String s = writeValue(obj);
        Map<String, Object> map = new HashMap<>();
        if (Objects.nonNull(s)) {
            return readObj(s, new TypeReference<HashMap<String, Object>>() {
            });
        }
        return map;
    }

    public static <T> Collection<T> readCollection(String str, Class<T> tClass) {
        try {
            return mapper.readValue(str, mapper.getTypeFactory().constructCollectionType(Collection.class, tClass));
        } catch (IOException e) {
            log.error("readCollection convert json to object list exception, {}", e.getMessage(), e);
        }
        return null;
    }


    public static void main(String[] args) {
        List<InternalRequest> list = new ArrayList<>();
        Collection<String> strings = readCollection("[23,34]", String.class);
        System.out.println(strings);

        InternalRequest internalRequest = new InternalRequest();
        internalRequest.setUrl("ste");
        internalRequest.setMethod("test");
        Map<Integer, List<InternalRequest>> map = new HashMap<>();
        list.add(internalRequest);
        map.put(1, list);
        internalRequest.setUrl("hhhhhhhh");
        list.add(internalRequest);
        map.put(222, list);
        String s = writeValue(map);

//        Map<Integer, String> integerInternalRequestMap = readMap(s, Integer.class, String.class);
        Map<Integer, List<InternalRequest>> map2= readMap(s, Integer.class, new TypeReference<List<InternalRequest>>() {});
        Map<String, Object> map3= readMap(s);
//        System.out.println(integerInternalRequestMap.values());
        System.out.println(map2.values());
        System.out.println(map3);
    }

}
