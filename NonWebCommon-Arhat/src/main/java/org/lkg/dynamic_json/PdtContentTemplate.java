package org.lkg.dynamic_json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lkg.utils.JacksonUtil;

import java.util.Collections;

/**
 * @date: 2026/1/11 23:27
 * @author: li kaiguang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdtContentTemplate {

    private String attrName;
    private String attrKey;
    private Class<?> clazz;


    public static void main(String[] args) throws Exception {
        String str = "{\n" +
                "  \"attrName\" : \"姓名\",\n" +
                "  \"attrKey\" : \"name\",\n" +
                "  \"clazz\" : \"java.lang.String\"\n" +
                "}";
        PdtContentTemplate pdtContentTemplate = JacksonUtil.readValue(str, PdtContentTemplate.class);

        System.out.println(JacksonUtil.writeValue(Collections.singletonList(pdtContentTemplate)));

        String jsonArr ="[{\"attrName\":\"姓名\",\"attrKey\":\"name\",\"clazz\":\"java.lang.String\"},{\"attrName\":\"年龄\",\"attrKey\":\"age\",\"clazz\":\"java.lang.Integer\"},{\"attrName\":\"地址\",\"attrKey\":\"address\",\"clazz\":\"org.lkg.dynamic_json.User$Address\"}]";
        //根据定义的pdtContent 去得到一个动态DTO的元数据， 然后根据动态DTO元数据反序列化出具体的对象可以配置具体要的属性


        String userStr = "{\"name\":\"若渴\",\"age\":23,\"address\":{\"cityName\":\"北京市\",\"provinceName\":\"北京---\"}}";
        Object o = PdtContentEnum.getByName("DEMO").parseContent(userStr,  jsonArr);

        System.out.println(o);
    }


}

