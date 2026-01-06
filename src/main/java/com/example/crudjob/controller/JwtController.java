package com.example.crudjob.controller;

import org.springframework.web.bind.annotation.*;

import com.example.crudjob.service.AesCryptoService;
import com.example.crudjob.service.JwtService;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;

@RestController
@RequestMapping("/api/jwt")
public class JwtController {

    private final JwtService jwtService;
    private final AesCryptoService aesService;

    public JwtController(JwtService jwtService, AesCryptoService aesService) {
        this.jwtService = jwtService;
        this.aesService = aesService;
    }

    /**
     * Tạo JWT từ payload JSON
     */
    @PostMapping("/create")
    public String createToken(@RequestBody String payload) throws Exception {

        // 1. Tạo AES key tạm thời (hoặc lấy từ Vault)
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey aesKey = keyGen.generateKey();

        // 2. Random IV
        byte[] iv = new byte[12]; // 12 bytes cho GCM
        new SecureRandom().nextBytes(iv);

        // 3. AES Encrypt payload
        String encrypted = aesService.encrypt(payload, aesKey, iv);

        // 4. Encode IV + AES key Base64 để lưu hoặc gửi kèm token (demo purposes)
        String ivBase64 = Base64.getEncoder().encodeToString(iv);
        String keyBase64 = Base64.getEncoder().encodeToString(aesKey.getEncoded());

        // 5. Tạo JWT RS256
        String token = jwtService.generateToken(encrypted, 3600_000); // 1h

        return token + "|" + ivBase64 + "|" + keyBase64;
    }

    /**
     * Giải mã JWT
     */
    @PostMapping("/decode")
    public String decodeToken(@RequestParam String token,
            @RequestParam String ivBase64,
            @RequestParam String keyBase64) throws Exception {

        byte[] iv = Base64.getDecoder().decode(ivBase64);
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        SecretKey aesKey = new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");

        // 1. Verify JWT
        String jwtToken = token;
        String encryptedPayload = jwtService.parseToken(jwtToken).get("payload", String.class);

        // 2. Decrypt AES
        return aesService.decrypt(encryptedPayload, aesKey, iv);
    }
}
