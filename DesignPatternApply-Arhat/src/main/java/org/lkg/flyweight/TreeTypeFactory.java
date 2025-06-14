package org.lkg.flyweight;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @date: 2025/6/8 09:53
 * @author: li kaiguang
 */
public class TreeTypeFactory {

    private static final Map<String, TreeType> factory = new HashMap<>();


    public static TreeType getTreeType(String name, String color) {
        StringJoiner stringJoiner = new StringJoiner("_");
        stringJoiner.add(name);
        stringJoiner.add(color);

        return factory.computeIfAbsent(stringJoiner.toString(), ref -> new TreeType(name, color));
    }

    public static int getSize() {
        return factory.size();
    }

    public static void main(String[] args) {
        getTreeType("哈哈", "测试");
    }
}
