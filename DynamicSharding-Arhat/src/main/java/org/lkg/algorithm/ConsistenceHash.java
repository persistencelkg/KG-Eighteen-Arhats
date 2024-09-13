package org.lkg.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.*;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/13 3:52 PM
 */
public class ConsistenceHash {

    private final static String EMPTY = "";
    private final static String SPLIT = "_";


    @Getter
    private final int nodeCount;

    @Getter
    private final int virtualNodeCount;

    @Getter
    private final CommonHashAlgorithm commonHashAlgorithm;

    @Getter
    private final TreeMap<Long, Node> HASH_CIRCLE = new TreeMap<>();
    private int fallbackCount;


    private ConsistenceHash(int nodeCount, int virtualNodeCount) {
        if (nodeCount < 1 || virtualNodeCount < 1) {
            throw new IllegalArgumentException("hash cycle node or virtual node count not lt 1");
        }
        this.nodeCount = nodeCount;
        this.virtualNodeCount = virtualNodeCount;
        this.commonHashAlgorithm = CommonHashAlgorithm.NATIVE_HASH;
    }


    public static ConsistenceHash getInstance(int nodeCount, int virtualNodeCount) {
        ConsistenceHash consistenceHash = new ConsistenceHash(nodeCount, virtualNodeCount);
        consistenceHash.buildCircle();
        return consistenceHash;
    }


    private void buildCircle() {
        for (int i = 0; i < nodeCount; i++) {
            Node node = new Node(i + EMPTY);
            for (int j = 0; j < virtualNodeCount; j++) {
                String join = String.join(SPLIT, node.getValue(), j + EMPTY);
                long hash = commonHashAlgorithm.hash(join);
                HASH_CIRCLE.put(hash, node);
            }
        }
    }

    public static String joinWithSpit(Object...obj) {
        String[] array = Arrays.stream(obj).map(ref -> ref + EMPTY).toArray(String[]::new);
        return String.join(SPLIT, array);
    }


    public String getActualNode(String key) {
        long hash = this.commonHashAlgorithm.hash(key);
        Node node = HASH_CIRCLE.get(hash);
        if (Objects.isNull(node)) {
            // 递增向上查找
            hash = HASH_CIRCLE.ceilingKey(hash);
            node = HASH_CIRCLE.get(hash);
        }
        if (Objects.isNull(node)) {
            node = HASH_CIRCLE.firstEntry().getValue();
            fallbackCount++;
        }
        return node.getValue();
    }

    @Data
    @AllArgsConstructor
    static class Node {
        private String value;
    }

    public static void main(String[] args) {
        System.out.println(getInstance(1, 3));
        System.out.println(getInstance(4, 10));
    }
}
