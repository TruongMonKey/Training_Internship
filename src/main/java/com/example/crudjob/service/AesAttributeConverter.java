package com.example.crudjob.service;

import com.example.crudjob.exception.DecryptionException;
import com.example.crudjob.exception.EncryptionException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Setter;

@Converter
public class AesAttributeConverter
        implements AttributeConverter<String, String> {

    private static EncryptionService encryptionService;

    // Inject từ Spring lúc app start
    public static void init(EncryptionService service) {
        encryptionService = service;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        return encryptionService.encrypt(attribute); // 🔐 AES
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return encryptionService.decrypt(dbData); // 🔓 AES
    }
}
