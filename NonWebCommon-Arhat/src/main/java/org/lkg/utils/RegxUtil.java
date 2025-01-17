package org.lkg.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/2/28 8:23 PM
 */
public class RegxUtil {
    public static boolean isValidPhoneNumber(String phoneNumber) {
        //String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[0,1,3,5,6,7,8])|(18[0-9])|166|198|199)\\d{8}$";
        String regex = "^1\\d{10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }


    /**
     * 将驼峰命名法转换为下划线命名法
     *
     * @param camelCaseStr 驼峰格式的字符串
     * @return 下划线格式的字符串
     */
    public static String camelToSnake(String camelCaseStr) {
        return camelToTargetCh(camelCaseStr, '_');
    }

    public static String camelToMinus(String camelCaseStr) {
        return camelToTargetCh(camelCaseStr, '-');
    }

    public static String camelToTargetCh(String camelCaseStr, char ch) {
        if (camelCaseStr == null || camelCaseStr.isEmpty()) {
            return "";
        }
        return camelCaseStr
                .replaceAll("([a-z])([A-Z]+)", "$1" + ch + "$2")
                .toLowerCase();
    }


    /**
     * 将下划线命名法转换为驼峰命名法
     *
     * @param snakeCaseStr 下划线格式的字符串
     * @return 驼峰格式的字符串
     */
    public static String snakeToCamel(String snakeCaseStr) {
        if (snakeCaseStr == null || snakeCaseStr.isEmpty()) {
            return "";
        }
        String[] parts = snakeCaseStr.split("_");
        StringBuilder camelCaseStr = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            camelCaseStr.append(parts[i].substring(0, 1).toUpperCase()).append(parts[i].substring(1).toLowerCase());
        }
        return camelCaseStr.toString();
    }

    public static void main(String[] args) {
        System.out.println(camelToMinus("primKafa"));
    }


}
