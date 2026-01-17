package org.lkg.dynamic_json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lkg.exception.CommonException;
import org.lkg.exception.enums.CommonExceptionEnum;
import org.lkg.exception.enums.MonitorStatus;
import org.lkg.exception.enums.MonitorType;
import org.lkg.utils.JacksonUtil;
import org.lkg.utils.KgLogUtil;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @date: 2026/1/17 15:57
 * @author: li kaiguang
 */
@Getter
@AllArgsConstructor
public enum PdtContentEnum {

    DEMO(User.class)


    ;



    private final Class<?> pdtContentClass;


    public static PdtContentEnum getByName(String name) {
        PdtContentEnum[] values = PdtContentEnum.values();
        for (PdtContentEnum value : values) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        throw CommonException.fail(CommonExceptionEnum.PDT_CONTENT_NAME_NOT_EXIST_BIZ_ERROR, String.format("产品内容Name：%s 不存在", name));
    }

    public <T> T parseContent(String content,  String pdtTemplate) {
        long start = System.currentTimeMillis();
        try {
            List<PdtContentTemplate> pdtContentTemplates = JacksonUtil.readList(pdtTemplate, PdtContentTemplate.class);
            Map<String, Object> map = JacksonUtil.readMap(content);
            return (T)  createObjectFromMap(map, pdtContentTemplates, pdtContentClass);
        } catch (Throwable th) {
            KgLogUtil.monitor(MonitorType.INNER, MonitorStatus.FAIL, CommonExceptionEnum.PDT_CONTENT_PARSE_BIZ_ERROR, "PdtContentEnum.parseContent", start);
           // KgLogUtil.printBizError("产品内容解析为空:{}", pdtContentClass);
            throw CommonException.fail(CommonExceptionEnum.PDT_CONTENT_PARSE_BIZ_ERROR);
        }
    }


    public static Object createObjectFromMap(Map<String, Object> map, List<PdtContentTemplate> templates, Class<?> targetClass)
            throws Exception {

        List<Object> params = new ArrayList<>();

        for (PdtContentTemplate template : templates) {
            String key = template.getAttrKey();
            Class<?> clazz = template.getClazz();
            Object value = map.get(key);

            // 处理嵌套对象
            if (value instanceof Map && clazz.getName().contains("$")) {
                // 递归创建嵌套对象
                value =  createNestedObject((Map<String, Object>) value, clazz);
            }

            params.add(value);
        }

        // 获取参数类型数组
        Class<?>[] paramTypes = templates.stream()
                .map(PdtContentTemplate::getClazz)
                .toArray(Class<?>[]::new);

        // 获取并调用构造函数
        Constructor<?> constructor = targetClass.getConstructor(paramTypes);
        return constructor.newInstance(params.toArray());
    }

    private static Object createNestedObject(Map<String, Object> nestedMap, Class<?> clazz)
            throws Exception {

        // 这里需要根据Address类的实际构造函 数来调整
        // 假设Address有一个接受两个String参数的构造函数
        Constructor<?> constructor = clazz.getConstructor(String.class, String.class);

        return constructor.newInstance(
                nestedMap.get("cityName"),
                nestedMap.get("provinceName")
        );
    }
}
