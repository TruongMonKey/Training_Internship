package com.example.crudjob.service;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.example.crudjob.config.RSAKeyProvider;
import com.example.crudjob.dto.AESPayload;
import com.example.crudjob.entity.enums.ErrorCode;
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

        /* ================= ENCRYPT ================= */

        /**
         * Encrypt plain text using hybrid encryption (AES + RSA)
         * 
         * Quy trình mã hóa:
         * 1. Kiểm tra input plaintext
         * 2. Sinh AES key ngẫu nhiên
         * 3. Mã hóa plaintext bằng AES
         * 4. Mã hóa AES key bằng RSA
         * 5. Kết hợp kết quả: encryptedData::iv::encryptedAesKey
         * 
         * @param plainText Plain text cần mã hóa
         * @return Encrypted string định dạng: encryptedData::iv::encryptedAesKey
         * @throws EncryptionException nếu quá trình mã hóa thất bại
         */
        public String encrypt(String plainText) {
                if (plainText == null) {
                        throw new EncryptionException(
                                        ErrorCode.ENC_PLAINTEXT_NULL,
                                        ErrorCode.ENC_PLAINTEXT_NULL.getDefaultMessage());
                }

                if (plainText.isBlank()) {
                        throw new EncryptionException(
                                        ErrorCode.ENC_PLAINTEXT_BLANK,
                                        ErrorCode.ENC_PLAINTEXT_BLANK.getDefaultMessage());
                }

                try {
                        SecretKey aesKey = AESUtil.generateKey();

                        if (aesKey == null) {
                                throw new EncryptionException(
                                                ErrorCode.ENC_AES_KEY_FAILED,
                                                ErrorCode.ENC_AES_KEY_FAILED.getDefaultMessage());
                        }

                        byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);
                        AESPayload aesPayload = AESUtil.encrypt(plainBytes, aesKey);

                        if (aesPayload == null) {
                                throw new EncryptionException(
                                                ErrorCode.ENC_AES_ENCRYPT_NULL,
                                                ErrorCode.ENC_AES_ENCRYPT_NULL.getDefaultMessage());
                        }

                        String encryptedData = aesPayload.getEncryptedData();
                        String iv = aesPayload.getIv();

                        if (encryptedData == null || encryptedData.isBlank() ||
                                        iv == null || iv.isBlank()) {
                                throw new EncryptionException(
                                                ErrorCode.ENC_AES_ENCRYPT_NULL,
                                                ErrorCode.ENC_AES_ENCRYPT_NULL.getDefaultMessage());
                        }

                        String encryptedAesKey = RSAUtil.encrypt(
                                        aesKey.getEncoded(),
                                        rsaKeyProvider.getPublicKey());

                        if (encryptedAesKey == null || encryptedAesKey.isBlank()) {
                                throw new EncryptionException(
                                                ErrorCode.ENC_RSA_ENCRYPT_NULL,
                                                ErrorCode.ENC_RSA_ENCRYPT_NULL.getDefaultMessage());
                        }

                        String encrypted = encryptedData + "::" + iv + "::" + encryptedAesKey;

                        if (encrypted == null || encrypted.isBlank()) {
                                throw new EncryptionException(
                                                ErrorCode.ENC_RESULT_NULL,
                                                ErrorCode.ENC_RESULT_NULL.getDefaultMessage());
                        }

                        log.info("[ENCRYPT] SUCCESS");
                        return encrypted;

                } catch (EncryptionException e) {
                        log.error("[ENCRYPT] FAILED | errorCode={}", e.getErrorCodeValue());
                        throw e;

                } catch (IllegalArgumentException e) {
                        log.error("[ENCRYPT] FAILED | cause={}", e.getMessage());
                        throw new EncryptionException(
                                        ErrorCode.ENC_AES_ENCRYPT_FAILED,
                                        ErrorCode.ENC_AES_ENCRYPT_FAILED.getDefaultMessage());

                } catch (Exception e) {
                        log.error("[ENCRYPT] FAILED | exception={}", e.getClass().getSimpleName());
                        throw new EncryptionException(ENCRYPTION_ERROR_MSG, e);
                }
        }

        /* ================= DECRYPT ================= */

        /**
         * Decrypt encrypted string using hybrid decryption (AES + RSA)
         * 
         * Quy trình giải mã:
         * 1. Kiểm tra input encryptedText
         * 2. Parse và validate định dạng (3 phần: encryptedData::iv::encryptedAesKey)
         * 3. Giải mã AES key bằng RSA
         * 4. Restore AES key từ bytes
         * 5. Giải mã plaintext bằng AES
         * 
         * @param encryptedText Encrypted string định dạng:
         *                      encryptedData::iv::encryptedAesKey
         * @return Decrypted plain text
         * @throws InvalidEncryptedDataException nếu định dạng không hợp lệ
         * @throws DecryptionException           nếu quá trình giải mã thất bại
         */
        public String decrypt(String encryptedText) {
                if (encryptedText == null) {
                        throw new InvalidEncryptedDataException(
                                        ErrorCode.DEC_INPUT_NULL,
                                        ErrorCode.DEC_INPUT_NULL.getDefaultMessage());
                }

                if (encryptedText.isBlank()) {
                        throw new InvalidEncryptedDataException(
                                        ErrorCode.DEC_INPUT_BLANK,
                                        ErrorCode.DEC_INPUT_BLANK.getDefaultMessage());
                }

                try {
                        String[] parts = encryptedText.split("::", -1);
                        if (parts.length != 3) {
                                throw new InvalidEncryptedDataException(
                                                ErrorCode.DEC_PARTS_COUNT_INVALID,
                                                ErrorCode.DEC_PARTS_COUNT_INVALID.getDefaultMessage());
                        }

                        String encryptedData = parts[0];
                        String iv = parts[1];
                        String encryptedAesKey = parts[2];

                        if (encryptedData.isBlank() || iv.isBlank() || encryptedAesKey.isBlank()) {
                                throw new InvalidEncryptedDataException(
                                                ErrorCode.DEC_ENCRYPTED_DATA_BLANK,
                                                ErrorCode.DEC_ENCRYPTED_DATA_BLANK.getDefaultMessage());
                        }

                        byte[] aesKeyBytes = RSAUtil.decrypt(
                                        encryptedAesKey,
                                        rsaKeyProvider.getPrivateKey());

                        if (aesKeyBytes == null || aesKeyBytes.length == 0) {
                                throw new DecryptionException(
                                                ErrorCode.DEC_RSA_DECRYPT_NULL,
                                                ErrorCode.DEC_RSA_DECRYPT_NULL.getDefaultMessage());
                        }

                        SecretKey aesKey = AESUtil.restoreKey(aesKeyBytes);

                        if (aesKey == null) {
                                throw new DecryptionException(
                                                ErrorCode.DEC_AES_KEY_RESTORE_NULL,
                                                ErrorCode.DEC_AES_KEY_RESTORE_NULL.getDefaultMessage());
                        }

                        byte[] plainBytes = AESUtil.decrypt(encryptedData, iv, aesKey);

                        if (plainBytes == null || plainBytes.length == 0) {
                                throw new DecryptionException(
                                                ErrorCode.DEC_AES_DECRYPT_NULL,
                                                ErrorCode.DEC_AES_DECRYPT_NULL.getDefaultMessage());
                        }

                        String decrypted = new String(plainBytes, StandardCharsets.UTF_8);

                        log.info("[DECRYPT] SUCCESS");
                        return decrypted;

                } catch (InvalidEncryptedDataException e) {
                        log.error("[DECRYPT] FAILED | errorCode={}", e.getErrorCodeValue());
                        throw e;

                } catch (DecryptionException e) {
                        log.error("[DECRYPT] FAILED | errorCode={}", e.getErrorCodeValue());
                        throw e;

                } catch (IllegalArgumentException e) {
                        log.error("[DECRYPT] FAILED | cause={}", e.getMessage());
                        throw new DecryptionException(
                                        ErrorCode.DEC_AES_DECRYPT_FAILED,
                                        ErrorCode.DEC_AES_DECRYPT_FAILED.getDefaultMessage());
                } catch (RuntimeException e) {
                        log.error("[DECRYPT] FAILED | runtimeException={}", e.getClass().getSimpleName());
                        throw e;
                }

        }
}