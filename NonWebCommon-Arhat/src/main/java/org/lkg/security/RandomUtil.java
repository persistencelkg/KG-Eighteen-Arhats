package org.lkg.security;

import org.apache.commons.lang3.RandomStringUtils;
import org.lkg.enums.StringEnum;
import org.lkg.simple.ObjectUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Description: 自定义随机数
 * Author: 李开广
 * Date: 2024/7/8 3:22 PM
 */
public class RandomUtil {
    // 100_MB
    private static long MAX = 1024 * 1024 * 100;

    private static final String RANGE_COUNT_MAX = System.getProperty("range_count_max", String.valueOf(MAX));

    static {
        if (!ObjectUtil.isEmpty(RANGE_COUNT_MAX)) {
            MAX = Long.parseLong(RANGE_COUNT_MAX);
        }
    }

    public static Set<String> rangeWithLetterNumber(int count, int len) {
        return rangeWithCharArr(count, len, null);
    }

    public static Set<String> rangeWithUpperLetterNumber(int count, int len) {
        char[] chars = ObjectUtil.concatMore(StringEnum.UPPER_CHAR, StringEnum.ONE_TO_NINE);
        return rangeWithCharArr(count, len, chars);
    }

    public static Set<String> rangeWithCharArr(int count, int len, char[] chars) {
        Set<String> codes = new HashSet<>(count);
        long countByte = 0;
        while (count > 0) {
            String code;
            if (!ObjectUtil.isEmpty(chars)) {
                code = RandomStringUtils.random(len, 0, chars.length, true, true, chars);
            } else {
                code = RandomStringUtils.randomAlphanumeric(len);
            }
            if (codes.add(code)) {
                count--;
            }
            countByte += code.getBytes().length;
            if (countByte >= MAX) {
                throw new UnsupportedOperationException("memory protected: range number size is too much , max only support: " + MAX + " B");
            }
        }
        return codes;
    }

    public static String rangeSingle(int len, char[] chars) {
        Set<String> strings = rangeWithCharArr(1, len, chars);
        return strings.iterator().next();
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("range_count_max"));
        System.out.println(rangeWithLetterNumber(3, 6));
        System.out.println(rangeWithUpperLetterNumber(2, 6));
    }
}
