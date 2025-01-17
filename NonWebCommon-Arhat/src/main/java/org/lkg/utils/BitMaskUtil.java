package org.lkg.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/30 4:48 PM
 */
public class BitMaskUtil {

    @Getter
    @AllArgsConstructor
    public enum BitMaskEnum {
        READ(1),// 1
        WRITE(2),// 10
        EXEC(4),//100
        ;
        // 可以扩展任意类型 如订单分类：普通单、搭售单、支付分搭售....
        private final int code;

        private final static Map<Integer, BitMaskEnum> map = new HashMap<>(BitMaskEnum.values().length);

        static {
            for (BitMaskEnum value : BitMaskEnum.values()) {
                int num = value.getCode();
                for (int i = 31; i >= 0; i--) {
                    if ((num >> i & 1) == 1){
                        map.put(i, value);
                        break;
                    }
                }
            }
//            System.out.println(map);
        }

        public static BitMaskEnum getEnum(Integer code) {
            return map.get(code);

        }
    }


    public static long getMask(BitMaskEnum... maskEnums) {
        if (ObjectUtil.isEmpty(maskEnums)) {
            return 0;
        }
        long base = 0L;
        for (BitMaskEnum maskEnum : maskEnums) {
            int code = maskEnum.getCode();
            base += code;
        }
        printBit(base);
        return base;
    }

    public static List<BitMaskEnum> listEnum(Long mask) {
        List<BitMaskEnum> list = new ArrayList<>();
        if (mask <= 0L) {
            return list;
        }
        // 获取每个枚举对应1的位置
        for (int i = Long.SIZE; i >= 0; i--) {
            // 计算每个位置的1的结果,
            if ((mask >> i & 1) == 1) {
                BitMaskEnum anEnum = BitMaskEnum.getEnum(i);
                if (Objects.nonNull(anEnum)) {
                    list.add(anEnum);
                }
            }
        }
        return list;
    }


    public static void printBit(long mask) {
        for (int i = Long.SIZE; i > -0; i--) {
            System.out.print(((mask >> i & 1) == 1) ? "1" : "0");
        }
        System.out.println();
    }


    public static void main(String[] args) {

        long mask = getMask(BitMaskEnum.EXEC, BitMaskEnum.WRITE);
        System.out.println(listEnum(mask));

        for (int j = 1; j <= 7; j++) {
            List<BitMaskEnum> bitMaskEnums = listEnum((long) j);
            System.out.printf("num: %s has permission:%s\n", j, bitMaskEnums);
        }

    }
}
