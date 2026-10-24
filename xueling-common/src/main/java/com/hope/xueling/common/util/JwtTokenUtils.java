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

/**
 * JWT 令牌工具：用两套独立的运行时密钥分别签发与校验访问令牌、刷新令牌。
 * <p>密钥由环境变量 XUELING_JWT_ACCESS_SECRET_BASE64 与 XUELING_JWT_REFRESH_SECRET_BASE64 提供，
 * 必须为 Base64 编码且解码后不少于 32 字节，两者不允许相同。</p>
 * @author 谢光湘
 * @since 2026/10/24
 */
@Component
public class JwtTokenUtils {
    /**
     * 访问令牌有效期（毫秒），30 分钟
     */
    public static final long ACCESS_TOKEN_EXPIRE = 30 * 60 * 1000L;
    /**
     * 刷新令牌有效期（毫秒），7 天
     */
    public static final long REFRESH_TOKEN_EXPIRE = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 访问令牌签名密钥
     */
    private final SecretKey accessKey;
    /**
     * 刷新令牌签名密钥，与访问令牌密钥不同，避免两类令牌互相冒用
     */
    private final SecretKey refreshKey;

    /**
     * 解码并校验两套签名密钥
     * @param accessSecret 访问令牌密钥（Base64）
     * @param refreshSecret 刷新令牌密钥（Base64）
     */
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

    /**
     * 解码并校验密钥：必须非空、合法 Base64 且不少于 32 字节
     * @param value Base64 编码的密钥
     * @param name 配置项名称，用于错误提示
     * @return 密钥字节
     */
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

    /**
     * 签发访问令牌
     * @param claims 令牌载荷，至少包含 userId
     * @return JWT 字符串
     */
    public String generateAccessToken(Map<String, Object> claims) {
        return generateToken(claims, accessKey, ACCESS_TOKEN_EXPIRE);
    }

    /**
     * 签发刷新令牌
     * @param claims 令牌载荷，至少包含 userId
     * @return JWT 字符串
     */
    public String generateRefreshToken(Map<String, Object> claims) {
        return generateToken(claims, refreshKey, REFRESH_TOKEN_EXPIRE);
    }

    /**
     * 按指定密钥与有效期签发 JWT
     * @param claims 令牌载荷
     * @param key 签名密钥
     * @param lifetime 有效期（毫秒）
     * @return JWT 字符串
     */
    private String generateToken(Map<String, Object> claims, SecretKey key, long lifetime) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + lifetime))
                .signWith(key)
                .compact();
    }

    /**
     * 校验并解析访问令牌，签名或有效期非法时抛出 JwtException
     * @param token 访问令牌
     * @return 令牌载荷
     */
    public Claims parseAccessToken(String token) {
        return parseToken(token, accessKey);
    }

    /**
     * 校验并解析刷新令牌，签名或有效期非法时抛出 JwtException
     * @param token 刷新令牌
     * @return 令牌载荷
     */
    public Claims parseRefreshToken(String token) {
        return parseToken(token, refreshKey);
    }

    /**
     * 用指定密钥校验并解析 JWT
     * @param token JWT 字符串
     * @param key 签名密钥
     * @return 令牌载荷
     */
    private Claims parseToken(String token, SecretKey key) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
