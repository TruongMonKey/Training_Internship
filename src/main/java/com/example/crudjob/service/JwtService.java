package com.example.crudjob.service;

import java.security.KeyPair;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {

    private final KeyPair keyPair;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration; // Default: 24 hours

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiration; // Default: 7 days

    public JwtService(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    /**
     * Tạo Access Token với user info, roles và permissions
     */
    public String generateAccessToken(Long userId, String username, Collection<String> roles,
            Collection<String> permissions) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }

    /**
     * Tạo Refresh Token
     */
    public String generateRefreshToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }

    /**
     * Verify JWT và lấy claims
     */
    public Claims parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Lấy username từ token
     */
    public String getUsernameFromToken(String token) {
        try {
            return parseToken(token).getSubject();
        } catch (JwtException e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Lấy userId từ token
     */
    public Long getUserIdFromToken(String token) {
        try {
            return parseToken(token).get("userId", Long.class);
        } catch (JwtException e) {
            log.error("Error extracting userId from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Kiểm tra token có hợp lệ không
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            log.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra token có hết hạn không
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    /**
     * Tạo JWT RS256 (phương thức cũ, dùng cho compatibility)
     */
    public String generateToken(String encryptedPayload, long expirationMillis) {
        return Jwts.builder()
                .setSubject("user-id")
                .claim("payload", encryptedPayload)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }
}
