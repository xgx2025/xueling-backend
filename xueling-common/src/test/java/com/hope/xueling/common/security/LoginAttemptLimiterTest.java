package com.hope.xueling.common.security;

import com.hope.xueling.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LoginAttemptLimiterTest {
    @Test
    void blocksRepeatedFailuresAndExpiresTheCounter() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> values = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(values);
        LoginAttemptLimiter limiter = new LoginAttemptLimiter(redis);

        when(values.increment(anyString())).thenReturn(1L);
        limiter.failed("PERSON@example.com");
        verify(redis).expire(anyString(), eq(Duration.ofMinutes(15)));

        when(values.get(anyString())).thenReturn("10");
        assertThrows(BusinessException.class, () -> limiter.check("person@example.com"));
        verify(values).get(argThat(key -> key instanceof String value
                && value.startsWith("auth:login:fail:") && !value.contains("person@example.com")));
    }
}
