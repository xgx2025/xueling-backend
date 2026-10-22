package com.hope.xueling.common.security;

import com.hope.xueling.common.controller.AuthController;
import com.hope.xueling.common.domain.vo.Result;
import com.hope.xueling.common.service.IAuthService;
import com.hope.xueling.common.util.JwtTokenUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenTest {
    @Test
    void parallelTabsCanRefreshWithTheSameCookieUntilItsExpiry() {
        byte[] accessBytes = new byte[32];
        byte[] refreshBytes = new byte[32];
        Arrays.fill(accessBytes, (byte) 1);
        Arrays.fill(refreshBytes, (byte) 2);
        JwtTokenUtils jwt = new JwtTokenUtils(Base64.getEncoder().encodeToString(accessBytes),
                Base64.getEncoder().encodeToString(refreshBytes));
        String refresh = jwt.generateRefreshToken(Map.of("userId", 42L));
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> values = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(values);
        when(values.get("refreshToken:42")).thenReturn(refresh);
        AuthController controller = new AuthController(mock(IAuthService.class), redis, jwt);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("refreshToken", refresh));

        ResponseEntity<Result<String>> first = controller.refreshToken(request);
        ResponseEntity<Result<String>> second = controller.refreshToken(request);

        assertEquals(200, first.getStatusCode().value());
        assertEquals(200, second.getStatusCode().value());
        assertEquals(42L, jwt.parseAccessToken(first.getBody().getData()).get("userId", Long.class));
        verify(values, times(2)).get("refreshToken:42");
        verifyNoMoreInteractions(values);
    }
}
