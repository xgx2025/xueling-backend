package com.hope.xueling.common.security;

import com.hope.xueling.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Locale;

/** Limits password guesses per account without storing the account name in Redis keys. */
@Component
@RequiredArgsConstructor
public class LoginAttemptLimiter {
    private static final long MAX_FAILURES = 10;
    private static final Duration WINDOW = Duration.ofMinutes(15);
    private final StringRedisTemplate redis;

    public void check(String account) {
        String failures = redis.opsForValue().get(key(account));
        if (failures != null && Long.parseLong(failures) >= MAX_FAILURES) {
            throw new BusinessException("登录尝试过于频繁，请15分钟后再试");
        }
    }

    public void failed(String account) {
        String key = key(account);
        Long failures = redis.opsForValue().increment(key);
        if (failures != null && failures == 1) redis.expire(key, WINDOW);
    }

    public void succeeded(String account) {
        redis.delete(key(account));
    }

    private String key(String account) {
        String normalized = account.trim().toLowerCase(Locale.ROOT);
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(normalized.getBytes(StandardCharsets.UTF_8));
            return "auth:login:fail:" + HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
