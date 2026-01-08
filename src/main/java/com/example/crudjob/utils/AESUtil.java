package com.example.crudjob.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.example.crudjob.dto.AESPayload;

import java.security.SecureRandom;
import java.util.Base64;

public class AESUtil {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_GCM = "AES/GCM/NoPadding";
    private static final int AES_KEY_SIZE = 256; // AES-256
    private static final int GCM_TAG_LENGTH = 128; // bits
    private static final int IV_SIZE = 12; // bytes (recommended)

    /* ===================== KEY ===================== */

    /** Generate random AES key (per request / per session) */
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(AES_KEY_SIZE);
        return keyGenerator.generateKey();
    }

    /** Restore AES key from raw bytes (after RSA decrypt) */
    public static SecretKey restoreKey(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }

    /* ===================== ENCRYPT ===================== */

    public static AESPayload encrypt(byte[] plainData, SecretKey aesKey)
            throws Exception {

        byte[] iv = new byte[IV_SIZE];
        SecureRandom.getInstanceStrong().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(AES_GCM);
        cipher.init(
                Cipher.ENCRYPT_MODE,
                aesKey,
                new GCMParameterSpec(GCM_TAG_LENGTH, iv));

        byte[] encryptedData = cipher.doFinal(plainData);

        return new AESPayload(
                Base64.getEncoder().encodeToString(encryptedData),
                Base64.getEncoder().encodeToString(iv));
    }

    /* ===================== DECRYPT ===================== */

    public static byte[] decrypt(
            String base64EncryptedData,
            String base64Iv,
            SecretKey aesKey) throws Exception {

        byte[] encryptedData = Base64.getDecoder().decode(base64EncryptedData);
        byte[] iv = Base64.getDecoder().decode(base64Iv);

        Cipher cipher = Cipher.getInstance(AES_GCM);
        cipher.init(
                Cipher.DECRYPT_MODE,
                aesKey,
                new GCMParameterSpec(GCM_TAG_LENGTH, iv));

        return cipher.doFinal(encryptedData);
    }
}
