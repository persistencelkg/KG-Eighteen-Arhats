package org.lkg.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/23 9:41 PM
 */
public class ExtraEntryManager {

    private static List<ExtraEntryInjector> injectorList = new ArrayList<>();

    public static void addExtraEntryInjector(ExtraEntryInjector extraEntryInjector) {
        injectorList.add(extraEntryInjector);
    }

    public static void addExtra(String key, String val) {
        for (ExtraEntryInjector extraEntryInjector : injectorList) {
            extraEntryInjector.inject(key, val);
        }
    }
}
