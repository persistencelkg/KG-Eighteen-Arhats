package org.lkg.algorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/13 4:57 PM
 */
public class ConsistenceHashFactory {

    private static final Map<String, ConsistenceHash> factory = new HashMap<>();


    public static ConsistenceHash buildConsistenceHash(int nodeCount, int virtualCount) {
        String key = ConsistenceHash.joinWithSpit(nodeCount, virtualCount);
        if (factory.containsKey(key)) {
            return factory.get(key);
        }
        ConsistenceHash instance = ConsistenceHash.getInstance(nodeCount, virtualCount);
        factory.put(key, instance);
        return instance;
    }

    public static void main(String[] args) {
        System.out.println(buildConsistenceHash(2, 3).getHASH_CIRCLE());
        System.out.println(factory);
        ConsistenceHash consistenceHash = buildConsistenceHash(2, 3);
        System.out.println(consistenceHash);
        HashMap<Long, Long> map = new HashMap<>();
        for (int i = 65535; i < 10_0000; i++) {
            String actualNode = consistenceHash.getActualNode(i + "");
            long hash = consistenceHash.getCommonHashAlgorithm().hash(i + "");
            map.put(hash, map.getOrDefault(hash, 0L) + 1);
        }
        System.out.println(map);
    }
}
