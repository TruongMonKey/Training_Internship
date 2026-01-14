package com.example.crudjob.utils;

import com.example.crudjob.dto.AESPayload;
import com.example.crudjob.exception.DecryptionException;
import com.example.crudjob.exception.EncryptionException;
import com.example.crudjob.exception.InvalidEncryptedDataException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class AESUtil {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_GCM = "AES/GCM/NoPadding";
    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_TAG_LENGTH = 128; // bits
    private static final int IV_SIZE = 12; // bytes

    /* ===================== KEY ===================== */

    /** Generate random AES key (per request / per session) */
    public static SecretKey generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGenerator.init(AES_KEY_SIZE);
            return keyGenerator.generateKey();

        } catch (GeneralSecurityException e) {

            log.error(
                    "[CRYPTO][AES][KEYGEN] Failed to generate AES key. " +
                            "Context=TX=????, Account=????, Amount=?, Time=????",
                    e);

            throw new EncryptionException("AES key generation failed", e);
        }
    }

    /** Restore AES key from raw bytes (after RSA decrypt) */
    public static SecretKey restoreKey(byte[] keyBytes) {
        try {
            return new SecretKeySpec(keyBytes, AES_ALGORITHM);

        } catch (IllegalArgumentException e) {

            log.warn(
                    "[CRYPTO][AES][KEYRESTORE] Invalid AES key format. " +
                            "Context=TX=????, Account=????, Amount=?, Time=????",
                    e);

            throw new InvalidEncryptedDataException(
                    "Invalid AES key format", e);
        }
    }

    /* ===================== ENCRYPT ===================== */

    public static AESPayload encrypt(byte[] plainData, SecretKey aesKey) {
        try {
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

        } catch (GeneralSecurityException e) {

            log.error(
                    "[CRYPTO][AES][ENCRYPT] Encryption failed. " +
                            "Context=TX=????, Account=????, Amount=?, Time=????",
                    e);

            throw new EncryptionException("AES encryption failed", e);
        }
    }

    /* ===================== DECRYPT ===================== */

    public static byte[] decrypt(
            String base64EncryptedData,
            String base64Iv,
            SecretKey aesKey) {

        try {
            byte[] encryptedData = Base64.getDecoder().decode(base64EncryptedData);
            byte[] iv = Base64.getDecoder().decode(base64Iv);

            Cipher cipher = Cipher.getInstance(AES_GCM);
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    aesKey,
                    new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            return cipher.doFinal(encryptedData);

        } catch (IllegalArgumentException e) {

            log.warn(
                    "[CRYPTO][AES][DECRYPT] Invalid encrypted data or IV format. " +
                            "Context=TX=????, Account=????, Amount=?, Time=????",
                    e);

            throw new InvalidEncryptedDataException(
                    "Invalid AES encrypted data or IV format", e);

        } catch (GeneralSecurityException e) {

            log.error(
                    "[CRYPTO][AES][DECRYPT] Decryption failed. " +
                            "Context=TX=????, Account=????, Amount=?, Time=????",
                    e);

            throw new DecryptionException("AES decryption failed", e);
        }
    }
}
