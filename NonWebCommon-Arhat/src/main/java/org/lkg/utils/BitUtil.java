package org.lkg.utils;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/1 9:19 PM
 */
public class BitUtil {

    /**
     * 是否是2次 正数次幂
     *
     * @param i
     * @return
     */
    public static boolean isPowerOfTwo(int i) {
        return i > 0 && Integer.bitCount(i) == 1;
    }


    /**
     * 获取位数对应的最大值
     *
     * @param bitLen
     * @return
     */
    public static long getBitMaxValue(int bitLen) {
        return ~(-1L << bitLen);
    }

    public static void main(String[] args) {
        System.out.println(getBitMaxValue(3));
        System.out.println(getBitMaxValue(13));

        System.out.println(isPowerOfTwo(13));
        System.out.println(isPowerOfTwo(16));
        System.out.println(isPowerOfTwo(-13));
        System.out.println(isPowerOfTwo(-16));
    }
}
