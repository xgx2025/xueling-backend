package com.hope.xueling.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/** Signs and validates the two kinds of token with independent runtime secrets. */
@Component
public class JwtTokenUtils {
    public static final long ACCESS_TOKEN_EXPIRE = 30 * 60 * 1000L;
    public static final long REFRESH_TOKEN_EXPIRE = 7 * 24 * 60 * 60 * 1000L;

    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    public JwtTokenUtils(
            @Value("${XUELING_JWT_ACCESS_SECRET_BASE64:}") String accessSecret,
            @Value("${XUELING_JWT_REFRESH_SECRET_BASE64:}") String refreshSecret) {
        byte[] accessBytes = decodeKey(accessSecret, "XUELING_JWT_ACCESS_SECRET_BASE64");
        byte[] refreshBytes = decodeKey(refreshSecret, "XUELING_JWT_REFRESH_SECRET_BASE64");
        if (Arrays.equals(accessBytes, refreshBytes)) {
            throw new IllegalArgumentException("JWT access and refresh secrets must differ");
        }
        accessKey = Keys.hmacShaKeyFor(accessBytes);
        refreshKey = Keys.hmacShaKeyFor(refreshBytes);
    }

    private static byte[] decodeKey(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must be configured");
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(value);
            if (decoded.length < 32) {
                throw new IllegalArgumentException(name + " must contain at least 32 random bytes");
            }
            return decoded;
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().startsWith(name)) throw e;
            throw new IllegalArgumentException(name + " must be valid Base64", e);
        }
    }

    public String generateAccessToken(Map<String, Object> claims) {
        return generateToken(claims, accessKey, ACCESS_TOKEN_EXPIRE);
    }

    public String generateRefreshToken(Map<String, Object> claims) {
        return generateToken(claims, refreshKey, REFRESH_TOKEN_EXPIRE);
    }

    private String generateToken(Map<String, Object> claims, SecretKey key, long lifetime) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + lifetime))
                .signWith(key)
                .compact();
    }

    public Claims parseAccessToken(String token) {
        return parseToken(token, accessKey);
    }

    public Claims parseRefreshToken(String token) {
        return parseToken(token, refreshKey);
    }

    private Claims parseToken(String token, SecretKey key) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
