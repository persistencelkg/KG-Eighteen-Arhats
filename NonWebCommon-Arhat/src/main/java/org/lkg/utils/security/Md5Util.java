package org.lkg.utils.security;

import lombok.extern.slf4j.Slf4j;
import org.lkg.enums.StringEnum;
import org.lkg.utils.ObjectUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class Md5Util {

    private static final char[] DIGITS_LOWER = ObjectUtil.concatMore(StringEnum.UPPER_CHAR, StringEnum.ONE_TO_NINE);

    private static final ThreadLocal<MessageDigest> MESSAGE_DIGEST_LOCAL = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    });

    public static String encryptWithHex(String digestWay, String value) {
        try {
            MessageDigest instance = MessageDigest.getInstance(digestWay);
            return encodeHexString(instance.digest(value.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static String md5Hex(byte[] bytes) throws NoSuchAlgorithmException {
        try {
            MessageDigest messageDigest = MESSAGE_DIGEST_LOCAL.get();
            if (messageDigest != null) {
                return encodeHexString(messageDigest.digest(bytes));
            }
            throw new NoSuchAlgorithmException("MessageDigest get MD5 instance error");
        } finally {
            MESSAGE_DIGEST_LOCAL.remove();
        }
    }

    public static String md5Hex(String value) {
        try {
            return md5Hex(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(md5Hex("wxx"));
    }

    /**
     * 将一个字节数组转化为可见的字符串.
     */
    public static String encodeHexString(byte[] bytes) {
        int l = bytes.length;

        char[] out = new char[l << 1];

        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & bytes[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & bytes[i]];
        }

        return new String(out);
    }

    /**
     * traceID 生成器
     *
     * @param str 加密文本
     * @return encode32编码
     */
    public static String encode32(String str) {
        String result = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update((str).getBytes(StandardCharsets.UTF_8));
            byte[] b = md5.digest();

            int i;
            StringBuilder buf = new StringBuilder();

            for (byte value : b) {
                i = value;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("y");
                }
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
            return result;
        } catch (Exception e) {
            return "";
        }
    }
}
