package com.example.crudjob.service;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.example.crudjob.config.RSAKeyProvider;
import com.example.crudjob.dto.AESPayload;
import com.example.crudjob.exception.DecryptionException;
import com.example.crudjob.exception.EncryptionException;
import com.example.crudjob.exception.InvalidEncryptedDataException;
import com.example.crudjob.utils.AESUtil;
import com.example.crudjob.utils.RSAUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EncryptionService {

    private final RSAKeyProvider rsaKeyProvider;

    private static final String ENCRYPTION_ERROR_MSG = "Failed to encrypt data";
    private static final String DECRYPTION_ERROR_MSG = "Failed to decrypt data";
    private static final String INVALID_FORMAT_MSG = "Invalid encrypted data format. Expected format: encryptedData::iv::encryptedAesKey";

    /* ================= ENCRYPT ================= */

    /**
     * Encrypt plain text using hybrid encryption (AES + RSA)
     * 
     * @param plainText Plain text to encrypt
     * @return Encrypted string in format: encryptedData::iv::encryptedAesKey
     * @throws EncryptionException if encryption fails
     */
    public String encrypt(String plainText) {
        if (plainText == null) {
            log.warn("Attempted to encrypt null value");
            throw new EncryptionException("Cannot encrypt null value");
        }

        try {
            log.debug("Encrypting data (length: {})", plainText.length());

            // 1. Generate AES key
            SecretKey aesKey = AESUtil.generateKey();

            // 2. Encrypt data with AES
            AESPayload aesPayload = AESUtil.encrypt(plainText.getBytes(), aesKey);

            // 3. Encrypt AES key with RSA
            String encryptedAesKey = RSAUtil.encrypt(
                    aesKey.getEncoded(),
                    rsaKeyProvider.getPublicKey());

            // 4. Combine (format: encryptedData::iv::encryptedAesKey)
            String encrypted = aesPayload.getEncryptedData()
                    + "::" + aesPayload.getIv()
                    + "::" + encryptedAesKey;

            log.debug("Encryption successful (encrypted length: {})", encrypted.length());
            return encrypted;

        } catch (EncryptionException e) {
            // Re-throw custom exceptions
            throw e;
        } catch (Exception e) {
            log.error("Encryption failed: {}", e.getMessage(), e);
            throw new EncryptionException(ENCRYPTION_ERROR_MSG, e);
        }
    }

    /* ================= DECRYPT ================= */

    /**
     * Decrypt encrypted string using hybrid decryption (AES + RSA)
     * 
     * @param encryptedText Encrypted string in format:
     *                      encryptedData::iv::encryptedAesKey
     * @return Decrypted plain text
     * @throws DecryptionException           if decryption fails
     * @throws InvalidEncryptedDataException if encrypted data format is invalid
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isBlank()) {
            log.warn("Attempted to decrypt null or blank value");
            throw new InvalidEncryptedDataException("Cannot decrypt null or blank value");
        }

        try {
            log.debug("Decrypting data (length: {})", encryptedText.length());

            // Validate format
            String[] parts = encryptedText.split("::");
            if (parts.length != 3) {
                log.error("Invalid encrypted format. Expected 3 parts, got: {}", parts.length);
                throw new InvalidEncryptedDataException(INVALID_FORMAT_MSG);
            }

            String encryptedData = parts[0];
            String iv = parts[1];
            String encryptedAesKey = parts[2];

            // Validate parts are not empty
            if (encryptedData.isBlank() || iv.isBlank() || encryptedAesKey.isBlank()) {
                log.error("Invalid encrypted format. One or more parts are blank");
                throw new InvalidEncryptedDataException(INVALID_FORMAT_MSG);
            }

            // 1. Decrypt AES key with RSA
            byte[] aesKeyBytes = RSAUtil.decrypt(
                    encryptedAesKey,
                    rsaKeyProvider.getPrivateKey());

            SecretKey aesKey = AESUtil.restoreKey(aesKeyBytes);

            // 2. Decrypt data
            byte[] plainBytes = AESUtil.decrypt(encryptedData, iv, aesKey);

            String decrypted = new String(plainBytes);
            log.debug("Decryption successful (decrypted length: {})", decrypted.length());
            return decrypted;

        } catch (InvalidEncryptedDataException e) {
            // Re-throw format validation exceptions
            throw e;
        } catch (DecryptionException e) {
            // Re-throw custom decryption exceptions
            throw e;
        } catch (Exception e) {
            log.error("Decryption failed: {}", e.getMessage(), e);
            throw new DecryptionException(DECRYPTION_ERROR_MSG, e);
        }
    }
}