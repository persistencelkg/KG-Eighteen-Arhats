package org.lkg.algorithm;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

/**
 * 使用建议:在一般散列分表、分布式节点中碰撞率最低 && 性能最好的【千万次计算耗时3s以内】的top3
 * hashcode(最强为0) > ELF_HASH > RS_HASH [冲突概率<0.04%]
 * 第二梯队
 * FNV1_64_HASH [冲突概率<0.05%, 千万次耗时3s]
 * FNV1A_64_HASH [冲突概率<0.05%, 千万次耗时5.5s]
 * FNV1_32_HASH [冲突概率<0.1%, 千万次耗时3s]
 * FNV1A_32_HASH [冲突概率<0.1%, 千万次耗时5.5s]
 * KETAMA_HASH   [冲突概率<0.1%， 千万次计算耗时14s], 随着位数越长，性能下降越明显
 * MurMurHash  [冲突概率<0.1%， 千万次计算耗时7.5s]
 * LUA_HASH、ONE_AT_A_TIME  [冲突概率<0.2%， 千万次计算耗时7s]
 * MYSQL_HASH [冲突概率<0.2%， 千万次计算耗时5.8s]
 *
 * CRC32 不属于hash算法，他的碰撞是100%的 多用于整合长字符串的hash值固定映射为4字节32位长度
 * Description: 通用hash算法能力
 * Author: 李开广
 * Date: 2024/9/13 2:38 PM
 */
public enum CommonHashAlgorithm {
    /**
     * Native hash (String.hashCode()).
     */
    NATIVE_HASH,
    /**
     *
     */
    CRC32_HASH,
    /**
     * 先乘后异或
     */
    FNV1_64_HASH,
    /**
     * 先异或后乘 64位
     */
    FNV1A_64_HASH,
    /**
     * 32-bit FNV1.
     */
    FNV1_32_HASH,
    /**
     * 32-bit FNV1a.
     */
    FNV1A_32_HASH,
    /**
     * 基于md5散列
     */
    KETAMA_HASH,

    /**
     * mysql内置的hash算法
     */
    MYSQL_HASH,

    ELF_HASH,

    /**
     * 递增场景下，性能之王
     */
    RS_HASH,

    /**
     * From lua source,it is used for long key
     */
    LUA_HASH,
    /**
     * MurMurHash算法，比hashcode、RS_HASH 差点比其他都ok
     * http://murmurhash.googlepages.com/
     */
    MurMurHash,
    /**
     * The Jenkins One-at-a-time hash ,please see
     * http://www.burtleburtle.net/bob/hash/doobs.html
     */
    ONE_AT_A_TIME;

    private static final long FNV_64_INIT = 0xcbf29ce484222325L;
    private static final long FNV_64_PRIME = 0x100000001b3L;

    private static final long FNV_32_INIT = 2166136261L;
    private static final long FNV_32_PRIME = 16777619;

    /**
     * Compute the hash for the given key.
     *
     * @return a positive integer hash
     */
    public long hash(final String k) {
        long rv = 0;
        switch (this) {
            case NATIVE_HASH:
                rv = k.hashCode();
                break;
            case CRC32_HASH:
                // return (crc32(shift) >> 16) & 0x7fff;
                CRC32 crc32 = new CRC32();
                crc32.update(k.getBytes(StandardCharsets.UTF_8));
                rv = crc32.getValue() >> 16 & 0x7fff;
                break;
            case FNV1_64_HASH: {
                // Thanks to pierre@demartines.com for the pointer
                rv = FNV_64_INIT;
                int len = k.length();
                for (int i = 0; i < len; i++) {
                    rv *= FNV_64_PRIME;
                    rv ^= k.charAt(i);
                }
            }
            break;
            case MurMurHash:
                ByteBuffer buf = ByteBuffer.wrap(k.getBytes());
                int seed = 0x1234ABCD;

                ByteOrder byteOrder = buf.order();
                buf.order(ByteOrder.LITTLE_ENDIAN);

                long m = 0xc6a4a7935bd1e995L;
                int r = 47;

                rv = seed ^ (buf.remaining() * m);

                long ky;
                while (buf.remaining() >= 8) {
                    ky = buf.getLong();

                    ky *= m;
                    ky ^= ky >>> r;
                    ky *= m;

                    rv ^= ky;
                    rv *= m;
                }

                if (buf.remaining() > 0) {
                    ByteBuffer finish = ByteBuffer.allocate(8).order(
                            ByteOrder.LITTLE_ENDIAN);
                    // for big-endian version, do this first:
                    // finish.position(8-buf.remaining());
                    finish.put(buf).rewind();
                    rv ^= finish.getLong();
                    rv *= m;
                }

                rv ^= rv >>> r;
                rv *= m;
                rv ^= rv >>> r;
                buf.order(byteOrder);
                break;
            case FNV1A_64_HASH: {
                rv = FNV_64_INIT;
                int len = k.length();
                for (int i = 0; i < len; i++) {
                    rv ^= k.charAt(i);
                    rv *= FNV_64_PRIME;
                }
            }
            break;
            case FNV1_32_HASH: {
                rv = FNV_32_INIT;
                int len = k.length();
                for (int i = 0; i < len; i++) {
                    rv *= FNV_32_PRIME;
                    rv ^= k.charAt(i);
                }
            }
            break;
            case FNV1A_32_HASH: {
                rv = FNV_32_INIT;
                int len = k.length();
                for (int i = 0; i < len; i++) {
                    rv ^= k.charAt(i);
                    rv *= FNV_32_PRIME;
                }
            }
            break;
            case KETAMA_HASH:
                byte[] bKey = computeMd5(k);
                rv = (long) (bKey[3] & 0xFF) << 24 | (long) (bKey[2] & 0xFF) << 16
                        | (long) (bKey[1] & 0xFF) << 8 | bKey[0] & 0xFF;
                break;

            case MYSQL_HASH:
                int nr2 = 4;
                for (int i = 0; i < k.length(); i++) {
                    rv ^= ((rv & 63) + nr2) * k.charAt(i) + (rv << 8);
                    nr2 += 3;
                }
                break;
            case ELF_HASH:
                long x = 0;
                for (int i = 0; i < k.length(); i++) {
                    rv = (rv << 4) + k.charAt(i);
                    if ((x = rv & 0xF0000000L) != 0) {
                        rv ^= x >> 24;
                        rv &= ~x;
                    }
                }
                rv = rv & 0x7FFFFFFF;
                break;
            case RS_HASH:
                long b = 378551;
                long a = 63689;
                for (int i = 0; i < k.length(); i++) {
                    rv = rv * a + k.charAt(i);
                    a *= b;
                }
                rv = rv & 0x7FFFFFFF;
                break;
            case LUA_HASH:
                int step = (k.length() >> 5) + 1;
                rv = k.length();
                for (int len = k.length(); len >= step; len -= step) {
                    rv = rv ^ (rv << 5) + (rv >> 2) + k.charAt(len - 1);
                }
            case ONE_AT_A_TIME:
                try {
                    int hash = 0;
                    for (byte bt : k.getBytes("utf-8")) {
                        hash += (bt & 0xFF);
                        hash += (hash << 10);
                        hash ^= (hash >>> 6);
                    }
                    hash += (hash << 3);
                    hash ^= (hash >>> 11);
                    hash += (hash << 15);
                    return hash;
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException("Hash function error", e);
                }
            default:
                assert false;
        }
        /* Truncate to 32-bits */
        return rv & 0xffffffffL;
    }

    /**
     * Get the md5 of the given key.
     */
    public static byte[] computeMd5(String k) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        md5.reset();
        md5.update(k.getBytes(StandardCharsets.UTF_8));
        return md5.digest();
    }
}
