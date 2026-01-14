package com.example.crudjob.config;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class RSAKeyProvider {
    private static final Logger logger = LoggerFactory.getLogger(RSAKeyProvider.class);

    // Config properties - không hardcode
    @Value("${security.rsa.keystore}")
    private Resource keystore;

    @Value("${security.rsa.password}")
    private String password;

    @Value("${security.rsa.alias}")
    private String alias;

    @Value("${security.rsa.keystore-type:PKCS12}")
    private String keystoreType;

    @Value("${security.rsa.min-key-size:2048}")
    private int minKeySize;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        logger.info("Initializing RSA key provider with alias: {}", alias);

        try {
            validateConfigProperties();
            loadKeystoreFile();
            loadPrivateKey();
            loadPublicKey();
            validateKeySize();

            logger.info("RSA keys loaded successfully. Key size: {} bits",
                    ((RSAPrivateKey) privateKey).getModulus().bitLength());

        } catch (KeystoreLoadException | KeystoreValidationException | KeyValidationException e) {
            logger.error("Failed to initialize RSA key provider", e);
            throw new IllegalStateException("RSA key initialization failed: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error during RSA key initialization", e);
            throw new IllegalStateException("Unexpected error during RSA key initialization", e);
        }
    }

    private void validateConfigProperties() throws KeystoreValidationException {
        if (keystore == null || !keystore.exists()) {
            String msg = "Keystore file not found or not accessible";
            logger.error(msg);
            throw new KeystoreValidationException(msg);
        }

        if (password == null || password.trim().isEmpty()) {
            String msg = "Keystore password is empty";
            logger.warn(msg);
            throw new KeystoreValidationException(msg);
        }

        if (alias == null || alias.trim().isEmpty()) {
            String msg = "Keystore alias is empty";
            logger.warn(msg);
            throw new KeystoreValidationException(msg);
        }

        if (minKeySize < 1024) {
            String msg = String.format("Min key size is too small: %d (minimum 1024)", minKeySize);
            logger.warn(msg);
            throw new KeystoreValidationException(msg);
        }

        logger.debug("Config properties validated - keystoreType: {}, minKeySize: {}",
                keystoreType, minKeySize);
    }

    private void loadKeystoreFile() throws KeystoreLoadException {
        try {
            logger.debug("Loading keystore from: {}", getKeystorePath());
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(keystore.getInputStream(), password.toCharArray());
            logger.debug("Keystore loaded successfully");

        } catch (Exception e) {
            String msg = String.format("Failed to load keystore (type: %s, path: %s)",
                    keystoreType, getKeystorePath());
            logger.error(msg, e);
            throw new KeystoreLoadException(msg, e);
        }
    }

    private void loadPrivateKey() throws KeystoreValidationException, KeyValidationException {
        try {
            logger.debug("Loading private key with alias: {}", alias);
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(keystore.getInputStream(), password.toCharArray());

            if (!keyStore.containsAlias(alias)) {
                String msg = String.format("Alias '%s' not found in keystore", alias);
                logger.error(msg);
                throw new KeystoreValidationException(msg);
            }

            Key key = keyStore.getKey(alias, password.toCharArray());

            if (key == null) {
                String msg = String.format("Key for alias '%s' is null", alias);
                logger.error(msg);
                throw new KeyValidationException(msg);
            }

            if (!(key instanceof PrivateKey)) {
                String msg = String.format("Key for alias '%s' is not a PrivateKey, type: %s",
                        alias, key.getClass().getSimpleName());
                logger.error(msg);
                throw new KeyValidationException(msg);
            }

            this.privateKey = (PrivateKey) key;
            logger.debug("Private key loaded successfully");

        } catch (KeystoreValidationException | KeyValidationException e) {
            throw e;
        } catch (Exception e) {
            String msg = String.format("Failed to load private key for alias '%s'", alias);
            logger.error(msg, e);
            throw new KeyValidationException(msg, e);
        }
    }

    private void loadPublicKey() throws KeystoreValidationException, KeyValidationException {
        try {
            logger.debug("Loading public key certificate for alias: {}", alias);
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(keystore.getInputStream(), password.toCharArray());

            Certificate certificate = keyStore.getCertificate(alias);

            if (certificate == null) {
                String msg = String.format("Certificate for alias '%s' not found", alias);
                logger.error(msg);
                throw new KeystoreValidationException(msg);
            }

            PublicKey loadedPublicKey = certificate.getPublicKey();

            if (loadedPublicKey == null) {
                String msg = String.format("Public key is null for alias '%s'", alias);
                logger.error(msg);
                throw new KeyValidationException(msg);
            }

            this.publicKey = loadedPublicKey;
            logger.debug("Public key loaded successfully");

        } catch (KeystoreValidationException | KeyValidationException e) {
            throw e;
        } catch (Exception e) {
            String msg = String.format("Failed to load public key certificate for alias '%s'", alias);
            logger.error(msg, e);
            throw new KeyValidationException(msg, e);
        }
    }

    private void validateKeySize() throws KeyValidationException {
        try {
            if (!(privateKey instanceof RSAPrivateKey)) {
                String msg = "Private key is not RSA key";
                logger.error(msg);
                throw new KeyValidationException(msg);
            }

            int keySize = ((RSAPrivateKey) privateKey).getModulus().bitLength();
            logger.info("RSA key size: {} bits", keySize);

            if (keySize < minKeySize) {
                String msg = String.format(
                        "RSA key size (%d bits) is below minimum required (%d bits)",
                        keySize, minKeySize);
                logger.error(msg);
                throw new KeyValidationException(msg);
            }

            logger.debug("Key size validation passed");

        } catch (KeyValidationException e) {
            throw e;
        } catch (Exception e) {
            String msg = "Failed to validate RSA key size";
            logger.error(msg, e);
            throw new KeyValidationException(msg, e);
        }
    }

    @Bean
    public KeyPair keyPair() {
        return new KeyPair(publicKey, privateKey);
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    private String getKeystorePath() {
        try {
            return keystore.getFile().getAbsolutePath();
        } catch (Exception e) {
            return keystore.getDescription();
        }
    }

    // Custom exceptions - bắt lỗi chi tiết
    public static class KeystoreLoadException extends Exception {
        public KeystoreLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class KeystoreValidationException extends Exception {
        public KeystoreValidationException(String message) {
            super(message);
        }

        public KeystoreValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class KeyValidationException extends Exception {
        public KeyValidationException(String message) {
            super(message);
        }

        public KeyValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}