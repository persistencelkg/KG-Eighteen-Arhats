package org.lkg.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lkg.simple.ObjectUtil;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/5/9 3:52 PM
 */
@Slf4j
public class AesUtil {


    private static final String SEC = "U2FsdGVkX1/ur/JQ0V707DL9MKDXRO34nr5LKzmy9X1Xjo2tC2XVrSHuWsVBzM0s";

    @AllArgsConstructor
    @Getter
    public enum EncryptAlgorithm {
        AES("AES"),
        DES("DES"),
        MD5("MD5");
        private String name;
    }


    public static String encrypt(String str, String salt, EncryptAlgorithm encryptAlgorithm) {

        try {
            byte[] keyEncoded = getKeyBytes(encryptAlgorithm, salt);
            SecretKeySpec skeySpec = new SecretKeySpec(keyEncoded, encryptAlgorithm.getName());
            Cipher cipher = Cipher.getInstance(encryptAlgorithm.getName());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encryptedBytes = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
            return new String(encryptedBytes, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException | NoSuchProviderException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private static byte[] getKeyBytes(EncryptAlgorithm encryptAlgorithm, String salt) throws NoSuchAlgorithmException, NoSuchProviderException {
        salt = ObjectUtil.isEmpty(salt) ? SEC : salt;
        KeyGenerator kgen = KeyGenerator.getInstance(encryptAlgorithm.getName());
        kgen.init(128);
        return kgen.generateKey().getEncoded();
    }


    public static String decrypt(String encryptStr, String salt, EncryptAlgorithm encryptAlgorithm) {

        try {
            Cipher cipher = Cipher.getInstance(encryptAlgorithm.getName());
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(SEC.getBytes(StandardCharsets.UTF_8), encryptAlgorithm.getName()));
            byte[] decryptedBytes = cipher.doFinal(encryptStr.getBytes(StandardCharsets.UTF_8));
            return new String(decryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static void main(String[] args) {
        String encrypt = encrypt("lkg", "love", EncryptAlgorithm.AES);
        System.out.println(encrypt);

        System.out.println(decrypt(encrypt, "love", EncryptAlgorithm.AES));
    }
}

