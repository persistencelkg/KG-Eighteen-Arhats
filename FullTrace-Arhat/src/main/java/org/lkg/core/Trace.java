package org.lkg.core;

import com.google.common.collect.Sets;
import lombok.Data;
import org.lkg.constant.LinkKeyConst;
import org.lkg.simple.ObjectUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/23 8:58 PM
 */
@Data
public class Trace {
    private static final Set<String> COMMON_FULL_LINK =
            Sets.newHashSet(
                    LinkKeyConst.STRESS_ID,
                    LinkKeyConst.USER_ID,
                    LinkKeyConst.CITY_ID,
                    LinkKeyConst.TC_TT);

    private String traceId;

    // 在不同中间件网络传输中，有类型差异，因此统一转为str，解析根据需要再根据包装类拆箱
    private ConcurrentHashMap<String, String> extraMap;

    // 意味着可以全链路透传的key信息
    private Set<String> fullLinkKeySet;


    public Trace(String tid) {
        this.traceId = ObjectUtil.isEmpty(tid) ? newTraceId() : tid;
        this.extraMap = new ConcurrentHashMap<>();
        this.fullLinkKeySet = DynamicConfigManger.initAndRegistChangeEvent("full.link.key", DynamicConfigManger::toSet, this::reSet);

    }

    public Trace() {
        this(newTraceId());
    }

    private void reSet(Set<String> newKeyList) {
        if (ObjectUtil.isNotEmpty(fullLinkKeySet)) {
            fullLinkKeySet.clear();
        } else {
            fullLinkKeySet = new HashSet<>();
        }
        fullLinkKeySet.addAll(COMMON_FULL_LINK);
        fullLinkKeySet.addAll(newKeyList);
    }

    /**
     * 借助uuid 实现20位长的traceId
     *
     * @return
     */
    public static String newTraceId() {
        String str = UUID.randomUUID().toString();
        return str.substring(0, 8) + str.substring(24);
    }


    public String addExtra(String key, String val) {
        if (ObjectUtil.isEmpty(key) || ObjectUtil.isEmpty(val)) {
            return null;
        }
        ExtraEntryManager.addExtra(key, val);
        return extraMap.putIfAbsent(key, val);
    }

}
