package org.lkg.utils;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.lkg.enums.ResponseBodyEnum;
import org.lkg.exception.CommonException;
import org.lkg.exception.enums.CommonExceptionEnum;
import org.lkg.request.GenericCommonResp;
import org.lkg.request.InternalRequest;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
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
//        @JsonProperty("aName")
        private String aName;
        private String aaName;

        private String oldAge;
        // 由于aName 是1个小写字母 + Name，根据lombok编译规则后 变成了setAName
        // jackson 在反序列化时，会将连续首字母大写转为小写 也就是实际jackson在读取key的时候期望读取 aname 但是实际只有aName;
        // 导致最终获取对象结果为空

        // 最好的解决方案是@jsonProperty强制指定要读取的字段key，要求上游这么传递不太现实

    }

    @JsonFilter("writeValueWithExclusionFilter")
    public static class MinInTest {
        @JsonIgnore
        private Integer code;
    }

    @JsonFilter("v2Filter")
    public static class MinTestv2 {
        @JsonIgnore
        private String msg;
    }

    public static String writeValueWithExclusion(Object obj, @NotNull Set<String> exclusionKeySet, @Nullable Class<?> mixInClassz, String... filterName) {
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAllExcept(exclusionKeySet);
        SimpleFilterProvider simpleFilterProvider = new SimpleFilterProvider();
        simpleFilterProvider.addFilter("writeValueWithExclusionFilter", simpleBeanPropertyFilter);
        Optional.ofNullable(filterName).ifPresent(ref ->  {
            for (String key : ref) {
                simpleFilterProvider.addFilter(key, SimpleBeanPropertyFilter.serializeAll());
            }
        });
        // 动态根据maxIn混合模式去排除对象，即通过其他@JsonFilter修饰的类去排除, 避免在原对象上去修改

        Optional.ofNullable(mixInClassz).ifPresent(ref -> getMapper().addMixIn(obj.getClass(), mixInClassz));

        try {
            return getMapper().writer(simpleFilterProvider).writeValueAsString(obj);
        } catch (Throwable th) {
            KgLogUtil.printSysError("JacksonUtil.writeValueWithExclusion fail, exclusions:{}, mixInClass:{}", exclusionKeySet, mixInClassz, th);
            throw CommonException.fail(CommonExceptionEnum.JACKSON_EXCLUSION_UNKNOWN_ERROR, th);
        }
    }



    public static void main(String[] args) {
        String jsonString1 = "{\"data\":[{\"name\":\"example\",\"aName\":\"xxx\",\"oldAge\":101}],\"code\":200,\"message\":\"Success\"}";
        String jsonString2 = "{\"result\":{\"aName\":\"example\",\"aaName\":\"example2\", \"oldAge\": 3},\"code\":200,\"msg\":\"Success\"}";

        GenericCommonResp deserialize = JacksonUtil.deserialize(jsonString1, ResponseBodyEnum.DATA_CODE_MESSAGE);
        List<TestObj> testObjs = deserialize.unSafeGetList(TestObj.class);
        System.out.println(testObjs);
        System.out.println("-----");
        HashSet<String> objects = new HashSet<>();
        objects.add("data");
        System.out.println(writeValueWithExclusion(deserialize, objects, MinInTest.class, "v2Filter"));
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
