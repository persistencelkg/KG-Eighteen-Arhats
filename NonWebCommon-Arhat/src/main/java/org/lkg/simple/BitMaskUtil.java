package org.lkg.simple;

import lombok.AllArgsConstructor;
import lombok.Data;
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
        READ(1),
        WRITE(2),
        EXEC(3),
        ;

        private final int code;

        private final static Map<Integer, BitMaskEnum> map = new HashMap<>(BitMaskEnum.values().length);

        static  {
            for (BitMaskEnum value : BitMaskEnum.values()) {
                map.put(value.getCode(), value);
            }
        }

        public static BitMaskEnum getEnum(Integer code) {
            return map.get(code);

        }
    }


    public static long getMask(BitMaskEnum... maskEnums) {
        long base = 0L;
        for (BitMaskEnum maskEnum : maskEnums) {
            base |= (1L << maskEnum.getCode());
        }

        printBit(base);
        return base;
    }

    public static List<BitMaskEnum> listEnum(Long mask) {
        List<BitMaskEnum> list = new ArrayList<>();
        if (mask <= 0L) {
            return list;
        }

        for (int i = Long.SIZE; i >= 0; i--) {
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
            System.out.print(((mask >> i & 1) == 1) ?  "1" : "0");
        }
        System.out.println();
    }


    public static void main(String[] args) {

        long mask = getMask(BitMaskEnum.EXEC, BitMaskEnum.WRITE);
        System.out.println(listEnum(mask));

        BitMaskEnum[] values = BitMaskEnum.values();
        for (int j = 1; j < 7; j++) {
            List<BitMaskEnum> bitMaskEnums = listEnum((long) j);
            System.out.printf("num: %s has permission:%s\n", j, bitMaskEnums);
        }

    }
}
