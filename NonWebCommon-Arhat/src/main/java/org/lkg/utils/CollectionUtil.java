package org.lkg.utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @date: 2026/1/24 22:46
 * @author: li kaiguang
 */
public class CollectionUtil {


    public static <T> List<T> of(T...t) {
        if (Objects.isNull(t)) {
            return Collections.emptyList();
        }
        return Arrays.stream(t).collect(Collectors.toList());
    }
}
