package org.lkg;

import org.lkg.algorithm.CommonHashAlgorithm;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/18 10:03 AM
 */
public class HashTest {

    public static void main(String[] args) {
        CommonHashAlgorithm[] values = CommonHashAlgorithm.values();
        int turns = 10;
        int n = 1000_0000;
        List<Long> list = generateLongs(n);
        for (CommonHashAlgorithm value : values) {
            computeConflict(turns, n, value, list);
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception ignored) {
        }

    }

    private static List<Long> generateLongs(long n) {
        List<Long> longs = new ArrayList<>();
        for (long l = 12345; l < n + 12345; l++) {
            longs.add(l);
        }
        return longs;
    }

    private static void computeConflict(int turns, int n, CommonHashAlgorithm hashAlgorithm, List<Long> data) {
        System.out.println("---- algorithm:" + hashAlgorithm + " conflict data-----");
        AtomicLong count = new AtomicLong();
        for (int i = 0; i < turns; i++) {
            LinkedHashSet<Long> longs = new LinkedHashSet<>();
            long time = System.currentTimeMillis();
            int len = 0;// 位数
            for (int j = 0; j < n; j++) {
//                double v = ((int) (Math.random() * n));
//                long hash = hashAlgorithm.hash(String.valueOf(j));
                long hash = hashAlgorithm.hash(String.valueOf(data.get(j)));
                len += String.valueOf(hash).length();
                longs.add(hash);
            }
//            System.out.println("avg bit num:" + len * 1.0 / n);
            int repeat = n - longs.size();
            double p = repeat * 1.0d / n;
            count.addAndGet(System.currentTimeMillis() - time);
            System.out.printf("current turn:%d, repeat size:%d config Probability: %f%% cost time: %dms%n", i + 1, repeat, p, System.currentTimeMillis() - time);
        }
        System.out.printf("avg cost: %.2f ms", count.get() * 1.0f / n);
    }
}
