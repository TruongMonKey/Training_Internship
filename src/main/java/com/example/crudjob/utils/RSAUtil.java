package com.example.crudjob.utils;

import com.example.crudjob.exception.DecryptionException;
import com.example.crudjob.exception.EncryptionException;
import com.example.crudjob.exception.InvalidEncryptedDataException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

@Slf4j
public class RSAUtil {

    private static final String RSA_OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private static final String SIGNATURE_ALGO = "SHA256withRSA";

    /* ===================== ENCRYPT ===================== */

    public static String encrypt(byte[] data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_OAEP);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encrypted = cipher.doFinal(data);
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (GeneralSecurityException e) {

            log.error(
                    "[CRYPTO][RSA][ENCRYPT] Encryption failed. Context=TX=????, Account=????, Amount=?, Time=????",
                    e);

            throw new EncryptionException("RSA encryption failed", e);
        }
    }

    /* ===================== DECRYPT ===================== */

    public static byte[] decrypt(
            String base64EncryptedData,
            PrivateKey privateKey) {

        try {
            byte[] encrypted = Base64.getDecoder().decode(base64EncryptedData);

            Cipher cipher = Cipher.getInstance(RSA_OAEP);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            return cipher.doFinal(encrypted);

        } catch (IllegalArgumentException e) {

            log.warn(
                    "[CRYPTO][RSA][DECRYPT] Invalid encrypted data format. Context=TX=????, Account=????, Amount=?, Time=????",
                    e);

            throw new InvalidEncryptedDataException(
                    "Invalid RSA encrypted data format", e);

        } catch (GeneralSecurityException e) {

            log.error(
                    "[CRYPTO][RSA][DECRYPT] Decryption failed. Context=TX=????, Account=????, Amount=?, Time=????",
                    e);

            throw new DecryptionException("RSA decryption failed", e);
        }
    }

    /* ===================== SIGN ===================== */

    public static String sign(byte[] data, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGO);

            signature.initSign(privateKey);
            signature.update(data);

            byte[] signed = signature.sign();
            return Base64.getEncoder().encodeToString(signed);

        } catch (GeneralSecurityException e) {

            log.error(
                    "[CRYPTO][RSA][SIGN] Signing failed. Context=TX=????, Account=????, Amount=?, Time=????",
                    e);

            throw new EncryptionException("RSA sign failed", e);
        }
    }

    /* ===================== VERIFY ===================== */

    public static boolean verify(
            byte[] data,
            String base64Signature,
            PublicKey publicKey) {

        try {
            byte[] signatureBytes = Base64.getDecoder().decode(base64Signature);

            Signature signature = Signature.getInstance(SIGNATURE_ALGO);

            signature.initVerify(publicKey);
            signature.update(data);

            return signature.verify(signatureBytes);

        } catch (IllegalArgumentException e) {

            log.warn(
                    "[CRYPTO][RSA][VERIFY] Invalid signature format. Context=TX=????, Account=????, Amount=?, Time=????",
                    e);

            throw new InvalidEncryptedDataException(
                    "Invalid RSA signature format", e);

        } catch (GeneralSecurityException e) {

            log.error(
                    "[CRYPTO][RSA][VERIFY] Signature verification failed. Context=TX=????, Account=????, Amount=?, Time=????",
                    e);

            throw new DecryptionException(
                    "RSA signature verification failed", e);
        }
    }
}
