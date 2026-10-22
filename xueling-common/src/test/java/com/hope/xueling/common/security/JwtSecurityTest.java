package com.hope.xueling.common.security;

import com.hope.xueling.common.util.JwtTokenUtils;
import com.hope.xueling.common.util.ThreadLocalUtils;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class JwtSecurityTest {
    private static final String ACCESS_KEY = key((byte) 1);
    private static final String REFRESH_KEY = key((byte) 2);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
        ThreadLocalUtils.remove();
    }

    @Test
    void rejectsMissingWeakAndReusedSecrets() {
        assertThrows(IllegalArgumentException.class, () -> new JwtTokenUtils("", REFRESH_KEY));
        assertThrows(IllegalArgumentException.class, () -> new JwtTokenUtils(key(new byte[16]), REFRESH_KEY));
        assertThrows(IllegalArgumentException.class, () -> new JwtTokenUtils(ACCESS_KEY, ACCESS_KEY));
    }

    @Test
    void tokensHaveSeparateSigningKeys() {
        JwtTokenUtils jwt = new JwtTokenUtils(ACCESS_KEY, REFRESH_KEY);
        String access = jwt.generateAccessToken(Map.of("userId", 42L));
        String refresh = jwt.generateRefreshToken(Map.of("userId", 42L));
        assertEquals(42L, jwt.parseAccessToken(access).get("userId", Long.class));
        assertEquals(42L, jwt.parseRefreshToken(refresh).get("userId", Long.class));
        assertThrows(Exception.class, () -> jwt.parseAccessToken(refresh));
        assertThrows(Exception.class, () -> jwt.parseRefreshToken(access));
    }

    @Test
    void filterAuthenticatesAdminAndRejectsForgedToken() throws Exception {
        JwtTokenUtils jwt = new JwtTokenUtils(ACCESS_KEY, REFRESH_KEY);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwt, new AdminAccessPolicy("42"));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/admin/read/article");
        request.addHeader("Authorization", "Bearer " + jwt.generateAccessToken(Map.of("userId", 42L)));
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean reachedController = new AtomicBoolean();
        FilterChain chain = (req, res) -> {
            reachedController.set(true);
            assertEquals(42L, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
            assertNotNull(ThreadLocalUtils.get());
        };

        filter.doFilter(request, response, chain);
        assertTrue(reachedController.get());
        assertNull(ThreadLocalUtils.get());

        reachedController.set(false);
        MockHttpServletRequest forged = new MockHttpServletRequest("GET", "/admin/read/article");
        forged.addHeader("Authorization", "Bearer " + jwt.generateRefreshToken(Map.of("userId", 42L)));
        MockHttpServletResponse rejected = new MockHttpServletResponse();
        filter.doFilter(forged, rejected, chain);
        assertEquals(401, rejected.getStatus());
        assertFalse(reachedController.get());
    }

    private static String key(byte value) {
        byte[] bytes = new byte[32];
        java.util.Arrays.fill(bytes, value);
        return key(bytes);
    }

    private static String key(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
