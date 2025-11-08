package org.lkg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.Test;
import org.lkg.structable_pattern.flyweight.Forest;
import org.lkg.structable_pattern.flyweight.TreeTypeFactory;

import java.util.*;
import java.util.function.Function;

/**
 * @date: 2025/6/8 09:58
 * @author: li kaiguang
 */
public class FlyWeightTest {

    @Test
    public void testCreateForest() {
        Random random = new Random();
        String[] name = new String[]{"桃树", "白桦树", "梧桐树", "苹果树"};
        String[] color = new String[]{"红色", "绿色", "黄色"};
        for (int i = 0; i < 10000 ; i++) {
            int x = random.nextInt(1000);
            int y = random.nextInt(1000);
            int randColor = random.nextInt(color.length);
            int randName = random.nextInt(name.length);
            Forest.addTree(x, y , name[randName], color[randColor]);
        }

        Forest.render();
        System.out.println("树类型个数:" + TreeTypeFactory.getSize());
    }

    @Data
    static class Base {
        private String name;
        private int age;
    }


    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    static  class A extends Base {
        private String address;
    }

    public static void main(String[] args) {
        Base base = new A("test");
        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure()
        try {
            String s = objectMapper.writeValueAsString(base);
            System.out.println(s);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private final static Map<String, Function<Object, Object>> map = new HashMap<>();
    static {
        register("insure", "PICC_CBPL", (obj) -> apply(obj));
        register("report", "PICC_CBPL", (obj) -> apply(obj));
        register("insure", "PICC_SHENZHEN_CBPL", (obj) -> apply(obj));
    }

    public static void register(String str, String name, Function<Object, Object> functions) {
        String key = new StringJoiner("_", str, name).toString();
        map.put(key, functions);
    }

    public static Object apply(Object req) {
        return new Object();
    }
}
