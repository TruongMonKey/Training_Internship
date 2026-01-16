package com.example.crudjob.config;

import com.example.crudjob.service.AesAttributeConverter;
import com.example.crudjob.service.EncryptionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ConverterConfig {

    private final EncryptionService encryptionService;

    @PostConstruct
    public void init() {
        AesAttributeConverter.init(encryptionService);
    }
}
