package com.example.crudjob.dto;

public class AESPayload {

    private final String encryptedData;
    private final String iv;

    public AESPayload(String encryptedData, String iv) {
        this.encryptedData = encryptedData;
        this.iv = iv;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public String getIv() {
        return iv;
    }
}
