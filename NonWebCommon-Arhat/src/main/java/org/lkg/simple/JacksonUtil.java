package org.lkg.simple;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.lkg.enums.ResponseBodyEnum;
import org.lkg.request.GenericCommonResp;
import org.lkg.request.InternalRequest;

import java.io.IOException;
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
        // 输出时将属性变成小写带下划线 输入时还原成javaBean格式 , 已过时要么通过spring全局配置，要么在java bean自动添加
//        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCaseStrategy.SNAKE_CASE);
        // 映射未知属性不抛出异常
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
        //
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, Boolean.TRUE);
        // 不序列化 值为null的数据
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String pattern = DateTimeUtils.YYYY_MM_DD_HH_MM_SS_SSS;

//        JavaTimeModule javaTimeModule = new JavaTimeModule(); 不同的场景序列化要求不同按需配置
//        javaTimeModule
//                .addSerializer(
//                        LocalDateTime.class,
//                        new LocalDateTimeSerializer(
//                                DateTimeFormatter.ofPattern(pattern)))
//                .addDeserializer(
//                        LocalDateTime.class,
//                        new LocalDateTimeDeserializer(
//                                DateTimeFormatter.ofPattern(pattern)));
//
//        mapper.registerModule(javaTimeModule);
//        mapper.setDateFormat(new SimpleDateFormat(pattern));
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }


    public static GenericCommonResp deserialize(String json, ResponseBodyEnum responseBodyEnum) {
        if (ObjectUtil.isEmpty(json)) {
            return null;
        }
        try {
            JsonNode jsonNode = mapper.readTree(json);

            Object code = parseNode(jsonNode.path(responseBodyEnum.getCode()));
            boolean isCodeStr = jsonNode.isInt() || jsonNode.isShort() || jsonNode.isLong();
            JsonNode path = jsonNode.path(responseBodyEnum.getData());
            boolean isArr = path.isArray();
            String msg = jsonNode.path(responseBodyEnum.getMessage()).asText();
            return new GenericCommonResp(code, path.toString(), msg, isArr, isCodeStr);

        } catch (JsonProcessingException e) {
            log.error("deserialize json exception, {}", e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static Object parseNode(JsonNode jsonNode) {
        if (jsonNode.isInt() || jsonNode.isShort()) {
            return jsonNode.asInt();
        } else if (jsonNode.isBoolean()) {
            return jsonNode.asBoolean();
        } else if (jsonNode.isTextual()) {
            return jsonNode.asText();
        } else if (jsonNode.isDouble() || jsonNode.isFloat()) {
            return jsonNode.asDouble();
        } else if (jsonNode.isLong() || jsonNode.isBigInteger()) {
            return jsonNode.asLong();
        }
        return null;
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
        if (ObjectUtil.isEmpty(json)) {
            return null;
        }
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
        if (ObjectUtil.isEmpty(json)) {
            return null;
        }
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

    public static <K, V> Map<K, V> readMap(String json, Class<K> keyClass, Class<V> valueClass) {
        if (ObjectUtil.isEmpty(json)) {
            return null;
        }
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructMapType(Map.class, keyClass, valueClass));
        } catch (IOException e) {
            log.error("readCollection convert json to object list exception, {}", e.getMessage(), e);
        }
        return null;
    }

    public static <K, V> Map<K, V> readMap(String json, Class<K> keyClass, TypeReference<V> valueClass) {
        if (ObjectUtil.isEmpty(json)) {
            return null;
        }
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructMapType(Map.class,
                    mapper.getTypeFactory().constructType(keyClass),
                    mapper.getTypeFactory().constructType(valueClass)));
        } catch (IOException e) {
            log.error("readCollection convert json to object list exception, {}", e.getMessage(), e);
        }
        return null;
    }

    public static TypeReference<Map<String, Object>> getMapReference() {
        return new TypeReference<Map<String, Object>>() {
        };
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
        if (ObjectUtil.isEmpty(str)) {
            return null;
        }

        try {
            return mapper.readValue(str, mapper.getTypeFactory().constructCollectionType(Collection.class, tClass));
        } catch (IOException e) {
            log.error("readCollection convert json to object list exception, {}", e.getMessage(), e);
        }
        return null;
    }

    @Data
    private static class TestObj {
        private String name;
        @JsonProperty("aName")
        private String aName;
        private String aaName;

        private String oldAge;
        // 以为代码是生成的setAName方法【lombok按照我们理解的方式】，
        // 但是ide生成的是【setaName有点不友好，但确实对的】所以jackson 在读取set方法时会将连续首字母大写转为小写
        // 因此实际调用setaname，因此要求属性是aname 而不是aName

        // 正常情况下以为是 setAaName ，jackson世纪调用的是 setaaName，因此解析aaName即可，最好的解决方案是@jsonProperty强制指定，要求上游这么传递不太现实

    }


    public static void main(String[] args) {
        String jsonString1 = "{\"data\":[{\"name\":\"example\",\"aName\":\"xxx\",\"oldAge\":101}],\"code\":200,\"message\":\"Success\"}";
        String jsonString2 = "{\"result\":{\"aname\":\"example\",\"aaName\":\"example2\", \"oldAge\": 3},\"code\":200,\"msg\":\"Success\"}";

        GenericCommonResp deserialize = JacksonUtil.deserialize(jsonString1, ResponseBodyEnum.DATA_CODE_MESSAGE);
        List<TestObj> testObjs = deserialize.unSafeGetList(TestObj.class);
        System.out.println(testObjs);
        System.out.println(deserialize);

        GenericCommonResp deserialize2 = JacksonUtil.deserialize(jsonString2, ResponseBodyEnum.RESULT_CODE_MSG);
        TestObj testObj = deserialize2.unSafeGet(TestObj.class);
        System.out.println(testObj);

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
        Map<Integer, List<InternalRequest>> map2 = readMap(s, Integer.class, new TypeReference<List<InternalRequest>>() {
        });
        Map<String, Object> map3 = readMap(s);
//        System.out.println(integerInternalRequestMap.values());
        System.out.println(map2.values());
        System.out.println(map3);
    }

}
