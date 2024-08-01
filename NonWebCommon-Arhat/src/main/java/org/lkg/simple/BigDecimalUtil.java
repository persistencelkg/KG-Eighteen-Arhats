package org.lkg.simple;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/1 1:47 PM
 */
public class BigDecimalUtil {

    /**
     * 元 -> 分 向上取整
     *
     * @param bigDecimal
     * @return
     */
    public static long multi100(BigDecimal bigDecimal) {
        return multiN(bigDecimal, 2);
    }

    public static long multiN(BigDecimal bigDecimal, int powerOfTen) {
        if (Objects.isNull(bigDecimal)) {
            return 0;
        }
        // 相等于 x 10^N
        return bigDecimal.scaleByPowerOfTen(powerOfTen).setScale(0, RoundingMode.HALF_UP).longValue();
    }

    /**
     * 分 -> 元 保留2位向上取整
     *
     * @param fee
     * @return
     */
    public static BigDecimal divide100(Long fee) {
       return divideN(fee, 2, 2);
    }

    public static BigDecimal divideN(Long fee, int powerOfTen, int scale) {
        if (Objects.isNull(fee)) {
            return new BigDecimal(0);
        }
        // 相等于 ÷ 10^N
        return BigDecimal.valueOf(fee).scaleByPowerOfTen(-powerOfTen).setScale(scale, RoundingMode.HALF_UP);
    }


    public static void main(String[] args) {
        System.out.println(divide100(132L));
        System.out.println(multi100(BigDecimal.valueOf(2.334)));
        System.out.println(multi100(BigDecimal.valueOf(2.378)));
    }

}
