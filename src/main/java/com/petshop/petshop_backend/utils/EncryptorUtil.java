package com.petshop.petshop_backend.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

//简单加密算法
public class EncryptorUtil {
    private static final String ALGORITHM = "AES";
    private final SecretKeySpec secretKey;

    public EncryptorUtil(String secretKey) {
        // 确保密钥长度为16、24或32字节（AES-128/192/256）
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        byte[] adjustedKey = new byte[16]; // 使用AES-128

        // 将密钥调整为16字节
        System.arraycopy(keyBytes, 0, adjustedKey, 0, Math.min(keyBytes.length, 16));
        this.secretKey = new SecretKeySpec(adjustedKey, ALGORITHM);
    }

    /**
     * 加密字符串（返回Base64编码结果）
     */
    public String encrypt(String input) throws Exception {
        if (input == null || input.isEmpty()) {
            return input;
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
        // 使用Base64 URL安全编码（无填充）
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
    }

    public String decrypt(String encrypted) throws Exception {
        if (encrypted == null || encrypted.isEmpty()) {
            return encrypted;
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        // 使用Base64 URL安全解码
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encrypted);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
