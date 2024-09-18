package org.lkg.core.expression;

import org.apache.commons.jexl3.JexlArithmetic;

import java.lang.reflect.Array;
import java.util.Objects;

/**
 * Description: 适配只有long类型无法被正确解析的问题
 * Author: 李开广
 * Date: 2024/8/15 5:20 PM
 */
public class LongAdaptJexlArithmetic extends JexlArithmetic {
    public LongAdaptJexlArithmetic(boolean astrict) {
        super(astrict);
    }

    @Override
    public Boolean contains(Object container, Object value) {
        if (Objects.isNull(container) || !container.getClass().isArray()) {
            return super.contains(container, value);
        }
        int length = Array.getLength(container);
        for (int i = 0; i < length; i++) {
            Object o = Array.get(container, i);
            if (o instanceof Number && value instanceof Number) {
                if (Objects.equals(((Number) o).doubleValue(), ((Number) value).doubleValue())) {
                    return true;
                }
            }
        }
        return false;

    }

    public static void main(String[] args) {
        Object a = 3;
        Long a3 = 3L;
        System.out.println(Objects.equals(a3, a));
    }
}
