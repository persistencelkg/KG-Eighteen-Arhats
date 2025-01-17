package org.lkg.utils;

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
    public static BigDecimal multi100(BigDecimal bigDecimal) {
        return multiN(bigDecimal, 2);
    }

    public static BigDecimal multiN(BigDecimal bigDecimal, int powerOfTen) {
        // 相等于 x 10^N
        return bigDecimal.scaleByPowerOfTen(powerOfTen).setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 分 -> 元 保留2位向上取整
     *
     * @param fee
     * @return
     */
    public static BigDecimal divide100(Integer fee) {
        return divideN(fee, 2, 2);
    }

    public static BigDecimal divideN(Integer fee, int powerOfTen, int scale) {
        // 相等于 ÷ 10^N
        return BigDecimal.valueOf(fee).scaleByPowerOfTen(-powerOfTen).setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal convertBigDecimal(Integer num, int scale, RoundingMode roundingMode) {
        return new BigDecimal(num).setScale(scale, roundingMode);
    }

    public static BigDecimal convertBigDecimal(Integer num) {
        return convertBigDecimal(num, 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal fenMultiFen(Integer fenA, Integer fenB, int scale) {
        return new BigDecimal(fenA).multiply(new BigDecimal(fenB)).setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal fenDivideFen(Integer fenA, Integer fenB, int scale) {
        return divide100(fenA).divide(divide100(fenB), scale, RoundingMode.HALF_UP);
    }


    /**
     * @param fenA 分
     * @param discountB  折扣
     * @return 分 ，业界普遍公认的折扣金额计算结果
     */
    public static BigDecimal fenMultiDiscount(Integer fenA, Integer discountB) {
        return discountCompute(fenA, divide100(discountB));
    }

    public static BigDecimal discountCompute(Integer fen, BigDecimal discount) {
        if (discount.compareTo(BigDecimal.ZERO) <= 0 || discount.compareTo(new BigDecimal(100)) >= 0) {
            throw new IllegalArgumentException("invalid discount:" + discount);
        }
        return discount.multiply(new BigDecimal(fen));
    }

    // 对分和折扣计算的结果，保留0位小数 再取整获取1次
    public static BigDecimal fenMultiDiscountResultRoundUp(Integer fenA, Integer discountB) {
        return fenToYuan(fenMultiDiscount(fenA, discountB), true);
    }

    public static BigDecimal fenToYuan(BigDecimal fen) {
        return fenToYuan(fen, false);
    }
    public static BigDecimal fenToYuan(BigDecimal fen, boolean withRound) {
        return fen.divide(new BigDecimal(100), withRound ? 0 : 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal yuanToFen(BigDecimal yuan) {
        return multi100(yuan);
    }

    public static void main(String[] args) {
        BigDecimal A = new BigDecimal(88).divide(new BigDecimal(100));
        System.out.println(yuanToFen(A));
        BigDecimal B = new BigDecimal(36000).multiply(A).divide(new BigDecimal(1), 0, RoundingMode.HALF_UP);
        System.out.println(fenMultiDiscount(36000, 88));
        System.out.println(fenToYuan(fenMultiDiscount(36000, 88), true));
        System.out.println(fenMultiDiscountResultRoundUp(36000, 88));
        System.out.println(multi100(BigDecimal.valueOf(2.334)));
        System.out.println(multi100(BigDecimal.valueOf(2.378)));
    }

}
