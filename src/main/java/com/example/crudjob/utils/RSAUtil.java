package com.example.crudjob.utils;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

public class RSAUtil {

    private static final String RSA_OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private static final String SIGNATURE_ALGO = "SHA256withRSA";

    /* ===================== ENCRYPT ===================== */

    /** Encrypt small data (AES key, token, secret) */
    public static String encrypt(byte[] data, PublicKey publicKey)
            throws Exception {

        Cipher cipher = Cipher.getInstance(RSA_OAEP);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encrypted = cipher.doFinal(data);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /* ===================== DECRYPT ===================== */

    /** Decrypt RSA encrypted data */
    public static byte[] decrypt(String base64EncryptedData, PrivateKey privateKey)
            throws Exception {

        byte[] encrypted = Base64.getDecoder().decode(base64EncryptedData);

        Cipher cipher = Cipher.getInstance(RSA_OAEP);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(encrypted);
    }

    /* ===================== SIGN ===================== */

    /** Digital signature */
    public static String sign(byte[] data, PrivateKey privateKey)
            throws Exception {

        Signature signature = Signature.getInstance(SIGNATURE_ALGO);
        signature.initSign(privateKey);
        signature.update(data);

        byte[] signed = signature.sign();
        return Base64.getEncoder().encodeToString(signed);
    }

    /* ===================== VERIFY ===================== */

    /** Verify digital signature */
    public static boolean verify(
            byte[] data,
            String base64Signature,
            PublicKey publicKey) throws Exception {

        byte[] signatureBytes = Base64.getDecoder().decode(base64Signature);

        Signature signature = Signature.getInstance(SIGNATURE_ALGO);

        signature.initVerify(publicKey);
        signature.update(data);

        return signature.verify(signatureBytes);
    }
}
