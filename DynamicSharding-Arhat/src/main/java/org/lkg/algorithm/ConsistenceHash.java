package org.lkg.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/13 3:52 PM
 */
@Slf4j
public class ConsistenceHash {

    private final static String EMPTY = "";
    private final static String SPLIT = "_";


    @Getter
    private final int nodeCount;

    @Getter
    private final int virtualNodeCount;

    @Getter
    private final CommonHashAlgorithm commonHashAlgorithm;


    private final TreeMap<Long, Node> HASH_CIRCLE = new TreeMap<>();

    private int fallbackCount;


    private ConsistenceHash(CommonHashAlgorithm shardingAlgorithm, int nodeCount, int virtualNodeCount) {
        if (nodeCount < 1 || virtualNodeCount < 1) {
            throw new IllegalArgumentException("hash cycle node or virtual node count not lt 1");
        }
        this.nodeCount = nodeCount;
        this.virtualNodeCount = virtualNodeCount;
        this.commonHashAlgorithm = shardingAlgorithm;
    }


    public static ConsistenceHash getInstance(CommonHashAlgorithm shardingAlgorithm, int nodeCount, int virtualNodeCount) {
        ConsistenceHash consistenceHash = new ConsistenceHash(shardingAlgorithm, nodeCount, virtualNodeCount);
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
        if (log.isDebugEnabled()) {
            log.debug("build hash circle:{}", HASH_CIRCLE);
        }
    }

    public static String joinWithSpit(Object... obj) {
        String[] array = Arrays.stream(obj).map(ref -> ref + EMPTY).toArray(String[]::new);
        return String.join(SPLIT, array);
    }


    public String getActualNode(String key) {
        Long hash = this.commonHashAlgorithm.hash(key);
        Node node = HASH_CIRCLE.get(hash);
        if (Objects.isNull(node)) {
            // 递增向上查找
            hash = HASH_CIRCLE.ceilingKey(hash);
            if (Objects.nonNull(hash)) {
                node = HASH_CIRCLE.get(hash);
            }

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
        System.out.println(getInstance(CommonHashAlgorithm.ELF_HASH, 4, 1));
        System.out.println(getInstance(CommonHashAlgorithm.NATIVE_HASH, 4, 1));
    }
}
