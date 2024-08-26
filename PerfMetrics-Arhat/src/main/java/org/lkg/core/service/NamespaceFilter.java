package org.lkg.core.service;

import org.lkg.core.DynamicConfigManger;
import org.lkg.core.config.LongHongConst;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/15 11:18 AM
 */
public class NamespaceFilter {

    private static Set<String> DISABLE_LIST = DynamicConfigManger.initAndRegistChangeEvent(LongHongConst.DISABLE_METER_KEY, ref -> DynamicConfigManger.toSet(ref, String.class), NamespaceFilter::init);

    public static boolean disable(String nameSpace) {
        return DISABLE_LIST.contains(nameSpace);
    }

    public static void init(Set<String> hashSet) {
        DISABLE_LIST = hashSet;
    }
}
