package com.hope.xueling.common.security;

import com.hope.xueling.common.config.SecurityConfig;
import com.hope.xueling.common.util.JwtTokenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SecurityRoutesTest.Routes.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, AdminAccessPolicy.class,
        JwtTokenUtils.class, SecurityRoutesTest.Routes.class})
class SecurityRoutesTest {
    @Autowired private MockMvc mvc;
    @Autowired private JwtTokenUtils jwt;

    @DynamicPropertySource
    static void secrets(DynamicPropertyRegistry registry) {
        registry.add("XUELING_JWT_ACCESS_SECRET_BASE64", () -> key((byte) 1));
        registry.add("XUELING_JWT_REFRESH_SECRET_BASE64", () -> key((byte) 2));
        registry.add("xueling.admin.user-ids", () -> "42");
    }

    @Test
    void publicAndPrivateRoutesEnforceAuthentication() throws Exception {
        mvc.perform(get("/auth/login")).andExpect(status().isOk());
        mvc.perform(get("/private/resource")).andExpect(status().isUnauthorized());
        mvc.perform(get("/private/resource").header("Authorization", bearer(7L)))
                .andExpect(status().isOk());
    }

    @Test
    void onlyAdminCanCallAdminRoutes() throws Exception {
        mvc.perform(get("/admin/resource")).andExpect(status().isUnauthorized());
        mvc.perform(get("/admin/resource").header("Authorization", bearer(7L)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/admin/resource").header("Authorization", bearer(42L)))
                .andExpect(status().isOk());
        mvc.perform(get("/druid/index.html").header("Authorization", bearer(42L)))
                .andExpect(status().isForbidden());
    }

    private String bearer(long userId) {
        return "Bearer " + jwt.generateAccessToken(Map.of("userId", userId));
    }

    private static String key(byte value) {
        byte[] bytes = new byte[32];
        Arrays.fill(bytes, value);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class TestApplication {}

    @RestController
    static class Routes {
        @GetMapping({"/auth/login", "/private/resource", "/admin/resource"})
        ResponseEntity<Void> ok() {
            return ResponseEntity.ok().build();
        }
    }
}
