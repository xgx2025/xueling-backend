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

/**
 * 登录失败限流：同一账号 15 分钟内失败 10 次后拒绝继续尝试。
 * <p>Redis 键中存放账号的 SHA-256 摘要，避免明文账号落入缓存。</p>
 * @author 谢光湘
 * @since 2026/10/24
 */
@Component
@RequiredArgsConstructor
public class LoginAttemptLimiter {
    /**
     * 窗口期内允许的最大失败次数
     */
    private static final long MAX_FAILURES = 10;
    /**
     * 失败计数窗口，15 分钟
     */
    private static final Duration WINDOW = Duration.ofMinutes(15);
    /**
     * 失败计数存储，键为账号摘要
     */
    private final StringRedisTemplate redis;

    /**
     * 校验账号是否已被限流
     * @param account 账号（邮箱或手机号）
     */
    public void check(String account) {
        String failures = redis.opsForValue().get(key(account));
        if (failures != null && Long.parseLong(failures) >= MAX_FAILURES) {
            throw new BusinessException("登录尝试过于频繁，请15分钟后再试");
        }
    }

    /**
     * 记录一次登录失败，首次失败时设置窗口过期时间
     * @param account 账号（邮箱或手机号）
     */
    public void failed(String account) {
        String key = key(account);
        Long failures = redis.opsForValue().increment(key);
        if (failures != null && failures == 1) redis.expire(key, WINDOW);
    }

    /**
     * 登录成功后清除该账号的失败计数
     * @param account 账号（邮箱或手机号）
     */
    public void succeeded(String account) {
        redis.delete(key(account));
    }

    /**
     * 生成 Redis 键：账号去空白、转小写后取 SHA-256 摘要，避免明文账号落缓存
     * @param account 账号（邮箱或手机号）
     * @return Redis 键
     */
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
