package org.lkg.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lkg.simple.ObjectUtil;
import org.springframework.util.Base64Utils;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

/**
 * 生产加密算法
 * Description:
 * Author: 李开广
 * Date: 2024/5/9 3:52 PM
 *
 * TODO 将sun实现的Base64 替换成
 *  <dependency>
 *   <groupId>commons-codec</groupId>
 *   <artifactId>commons-codec</artifactId>
 *   <version>1.8</version>
 *  </dependency>
 */
@Slf4j
public class AesUtil {


    private static final String KEY = "314498A1444512EC";
    private static final String INIT_VECTOR = "A9347374F7ED334C";

    @AllArgsConstructor
    @Getter
    public enum EncryptAlgorithm {
        AES_ECB_Padding("AES/ECB/PKCS5Padding"),
        AES_CBC_Padding("AES/CBC/PKCS5Padding"),
        AES_CBC_No_Padding("AES/CBC/NoPadding");
        private String name;

        public String getAlgorithmName() {
            return name.substring(0, name.indexOf("/"));
        }
    }

    @AllArgsConstructor
    @Getter
    public enum Mode {
        /**
         * 1.以秘钥长度将明文位数划分为等长加密块长度，
         * 2.安全系数较低
         * 3.要求明文必须使用填充策略以达到固定位数
         * 根据IV计算每个块对应密文1、密文2、密文3...
         */
        ECB("ECB"),
        /**
         * 1. 引入初始化向量init vector（IV)，长度必须和秘钥长度相等
         * 2. 明文可以不用补全
         * 3. 加密无法并行，解密可以
         * 先根据IV计算每个块对应密文1、密文2、密文3...
         * ((IV^ 密文1）^ （密文2) ) ^ (密文3) ...
         */
        CBC("CBC"),
        /**
         * 1. 引入初始化向量, 明文无需补全
         * 2. 加密无法并行，解密可以并行
         * 3.CFB1 传入的length 单位是bit，CFB8、CFB128 传入的length是byte，从1->128 加密效率越来越高
         * 先计算根据IV计算每个块对应密文1、密文2、密文3
         * (IV+KEY) ^ 第一块密文   (第一块密文+KEY) ^ 第二块密文   (第一块密文+KEY) ^ 第二块密文...
         */
        CFB("CFB");
        private String mode;
    }

    @AllArgsConstructor
    @Getter
    public enum PaddingStrategy {
        NoPadding("NoPadding"),
        /**
         * 默认填充策略,填充5字节填充，如果填充前，明文的长度正好是16字节，那么就会添加一个额外的16字节作为填充， 增加带宽
         */
        PKCS5Padding("PKCS5Padding"),
        PKCS7Padding("PKCS7Padding");

        private final String padding;
    }

    /**
     * 如果是中文 ： 考虑换算成16字节
     * 如果不等于16字节
     *
     * @param str
     * @param salt16b
     * @param encryptAlgorithm
     * @return 加密结果
     */
    public static String encrypt(String str, String salt16b, EncryptAlgorithm encryptAlgorithm) {
        try {
            Cipher cipher = getCipher(encryptAlgorithm, true, salt16b);
            byte[] srcBytes = str.getBytes(StandardCharsets.UTF_8);
            if (!encryptAlgorithm.getName().contains(Mode.ECB.getMode())) {
                return ivEncrypt(srcBytes, encryptAlgorithm, salt16b);
            }
            byte[] encryptedBytes = cipher.doFinal(srcBytes);
            return new String(Base64Utils.encode(encryptedBytes), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static String dataAlignment(String str, int len) {
        if (!ObjectUtil.isEmpty(str) && str.length() == len) {
            return str;
        }
        return new String(dataAlignment(str.getBytes(), len));
    }

    public static byte[] dataAlignment(byte[] bytes, int len) {
        int length = bytes.length;
        if (length % len != 0) {
            length += len - (length % len);
        }
        return  Arrays.copyOf(bytes, length);
    }

    public static String decrypt(String encryptStr, String salt16b, EncryptAlgorithm encryptAlgorithm) {
        try {
            byte[] decodeBuffer = Base64Utils.decode(encryptStr.getBytes(StandardCharsets.UTF_8));
            if (!encryptAlgorithm.getName().contains(Mode.ECB.getMode())) {
                return ivDecrypt(decodeBuffer, encryptAlgorithm, salt16b);
            }
            Cipher cipher = getCipher(encryptAlgorithm, false, salt16b);
            byte[] decryptedBytes = cipher.doFinal(decodeBuffer);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException | NoSuchProviderException | IOException | InvalidAlgorithmParameterException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private static Cipher getCipher(EncryptAlgorithm encryptAlgorithm, boolean encrypt, String salt) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(encryptAlgorithm.getName());
        salt = ObjectUtil.isEmpty(salt) ? KEY : salt;
        salt = dataAlignment(salt, cipher.getBlockSize());
        byte[] keyBytes = salt.getBytes(StandardCharsets.UTF_8);
        cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, encryptAlgorithm.getAlgorithmName()));
        return cipher;
    }

    private static String ivEncrypt(byte[] srcBytes, EncryptAlgorithm encryptAlgorithm, String salt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(encryptAlgorithm.getName());
        salt = ObjectUtil.isEmpty(salt) ? KEY : salt;
        salt = dataAlignment(salt, cipher.getBlockSize());
        byte[] keyBytes = salt.getBytes(StandardCharsets.UTF_8);
        // AES/ECB/PKCS5Padding， 默认只有PKCS5Padding ，PKCS1Padding PKCS7Padding 内置不存在，需要三方加密库：BouncyCastleProvider
        // 16字节
        int blockSize = cipher.getBlockSize();
        byte[] plainBytes = dataAlignment(srcBytes, blockSize);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, encryptAlgorithm.getAlgorithmName()), ivParameterSpec);
        byte[] bytes = cipher.doFinal(plainBytes);
        return new BASE64Encoder().encode(bytes).trim();
    }

    private static String ivDecrypt(byte[] srcBytes, EncryptAlgorithm encryptAlgorithm, String salt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, BadPaddingException, IOException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(encryptAlgorithm.getName());
        salt = ObjectUtil.isEmpty(salt) ? KEY : salt;
        salt = dataAlignment(salt, cipher.getBlockSize());
        byte[] keyBytes = salt.getBytes(StandardCharsets.UTF_8);
        // AES/ECB/PKCS5Padding， 默认只有PKCS5Padding ，PKCS1Padding PKCS7Padding 内置不存在，需要三方加密库：BouncyCastleProvider

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, encryptAlgorithm.getAlgorithmName());
        IvParameterSpec ivParameterSpec = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] bytes = cipher.doFinal(srcBytes);
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }


    public static void main(String[] args) {
        String encrypt = encrypt("SS6L51", "lkg", EncryptAlgorithm.AES_CBC_Padding);
        System.out.println(encrypt);
        System.out.println(decrypt(encrypt, "lkg", EncryptAlgorithm.AES_CBC_Padding));


        long a = 0x90001;
        System.out.println(Arrays.toString(String.valueOf(a).getBytes(StandardCharsets.UTF_8)));

        System.out.println(EncryptAlgorithm.AES_CBC_Padding.getAlgorithmName());
    }
}

