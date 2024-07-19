package org.lkg.simple;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.lkg.enums.StringEnum;
import org.lkg.security.RandomUtil;
import org.springframework.lang.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Stream;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/2/28 9:09 PM
 */
public class ObjectUtil {


    public static char[] concatMore(char[]... chars) {
        return concatMore(false, chars);
    }


    public static char[] concatMore(boolean isRandom, char[]... chars) {
        char[] firstArr = new char[chars[0].length];
        // 先拷贝的临时数组中
        System.arraycopy(chars[0], 0, firstArr, 0, chars[0].length);
        for (int i = 1; i < chars.length; i++) {
            // 将临时 和 下一个数组都合并到新数组中
            char[] newArr = new char[firstArr.length + chars[i].length];
            System.arraycopy(firstArr, 0, newArr, 0, firstArr.length);
            System.arraycopy(chars[i], 0, newArr, firstArr.length, chars[i].length);
            firstArr = newArr;
        }
        if (isRandom) {
            return RandomUtil.rangeSingle(firstArr.length, firstArr).toCharArray();
        }
        return firstArr;
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static boolean isEmpty(@Nullable Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof Optional) {
            return !((Optional) obj).isPresent();
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        }

        // else
        return false;
    }

    public static String firstLetterUpper(String str) {
        return str.substring(0, 1).toUpperCase(Locale.ENGLISH) + str.substring(1);
    }


    public static String underlineToCamel(String param) {
        if (isEmpty(param)) {
            return null;
        }
        String temp = param.toLowerCase();
        int len = temp.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = temp.charAt(i);
            if (c == StringEnum.UNDERSCORE.charAt(0)) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(temp.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String camelToUnderline(String param) {
        if (isEmpty(param)) {
            return "null";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(StringEnum.UNDERSCORE.charAt(0));
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        int classz = 3;
        System.out.println(Stream.of(float.class, Number.class).anyMatch(Integer.class::isAssignableFrom));
        System.out.println(camelToUnderline("OrderTale"));

        ObjectNode jsonNodes = new ObjectNode(JsonNodeFactory.instance);
        jsonNodes.put("type", "lkg");
        System.out.println(jsonNodes.toString());


        char[] array1 = {'H', 'e', 'l', 'l', 'o'};
        char[] array2 = {' ', 'W', 'o', 'r', 'l', 'd'};
        char[] result = concatMore(array1, array2);
        System.out.println(Arrays.toString(result));

        System.out.println(firstLetterUpper("testdd"));
    }
}
