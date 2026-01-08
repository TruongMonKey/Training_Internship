package com.example.crudjob.config;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class RSAKeyProvider {

    @Value("${security.rsa.keystore}")
    private Resource keystore;

    @Value("${security.rsa.password}")
    private String password;

    @Value("${security.rsa.alias}")
    private String alias;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() {

        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(keystore.getInputStream(), password.toCharArray());

            if (!keyStore.containsAlias(alias)) {
                throw new IllegalStateException(
                        "RSA alias not found in keystore: " + alias);
            }

            Key key = keyStore.getKey(alias, password.toCharArray());

            if (!(key instanceof PrivateKey)) {
                throw new IllegalStateException(
                        "Key is not a PrivateKey: " + alias);
            }

            this.privateKey = (PrivateKey) key;
            this.publicKey = keyStore.getCertificate(alias).getPublicKey();

            // Validate RSA key size (>= 2048)
            int keySize = ((RSAPrivateKey) privateKey).getModulus().bitLength();

            if (keySize < 2048) {
                throw new IllegalStateException(
                        "RSA key size must be >= 2048 bits");
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to load RSA keys from PKCS12", e);
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
}
