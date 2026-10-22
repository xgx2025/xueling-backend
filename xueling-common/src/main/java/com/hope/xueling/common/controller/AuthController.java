package com.hope.xueling.common.controller;

import com.hope.xueling.common.constant.ResultCode;
import com.hope.xueling.common.domain.dto.LoginDTO;
import com.hope.xueling.common.domain.dto.RegisterDTO;
import com.hope.xueling.common.domain.vo.Result;
import com.hope.xueling.common.service.IAuthService;
import com.hope.xueling.common.util.JwtTokenUtils;
import com.hope.xueling.common.util.ThreadLocalUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器，处理登录、登出、注册和忘记密码等请求
 * @author 谢光湘
 * @since 2026/1/20
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService authService;
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtTokenUtils jwtTokenUtils;

    @Value("${xueling.security.secure-cookies:true}")
    private boolean secureCookies;

    private ResponseCookie refreshCookie(String value, long maxAgeSeconds) {
        return ResponseCookie.from("refreshToken", value)
                .httpOnly(true)
                .secure(secureCookies)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
    }

    /**
     * 用户注册
     * @param registerDTO 注册数据传输对象
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return Result.success(null, "注册成功");
    }

    /**
     * 登录
     * @param loginDTO 登录数据传输对象
     * @param request HttpServletRequest对象
     * @return 登录成功后返回访问令牌（data中）和刷新令牌（Set-Cookie中）
     */
    @PostMapping("/login")
    public ResponseEntity<Result<String>> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response) {
        log.info("登录尝试，IP: {}", request.getRemoteAddr());
        loginDTO.setIp(request.getRemoteAddr());
        Map<String,String> tokens = authService.login(loginDTO);
        response.addHeader("Set-Cookie", refreshCookie(tokens.get("refreshToken"),
                JwtTokenUtils.REFRESH_TOKEN_EXPIRE / 1000).toString());

        return ResponseEntity.ok(Result.success(tokens.get("accessToken"), "登录成功"));
    }

    /**
     * 发送邮箱验证码
     * @param email 邮箱
     * @return 发送成功的消息
     */
    @GetMapping("/sendVerificationCode/email")
    public Result<String> sendVerificationCode(@RequestParam String email, HttpServletRequest request) {
        log.info("邮箱验证码发送尝试，IP: {}", request.getRemoteAddr());
        authService.sendEmailVerificationCode(email, request.getRemoteAddr());
        return Result.success(null, "验证码已发送");
    }



    /**
     * 用户登出
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletResponse response) {
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        authService.logout(userId.toString());
        response.addHeader("Set-Cookie", refreshCookie("", 0).toString());
        return Result.success(null, "已成功退出登录");
    }



    @PostMapping("/forget")
    public String forget() {
        return "forget";
    }

    /**
     * 刷新访问令牌
     * @param request HttpServletRequest对象
     * @return 刷新成功后返回新的访问令牌（data中）
     */
    @PostMapping("/refreshToken")
    public ResponseEntity<Result<String>> refreshToken(HttpServletRequest request) {
        //从请求的HttpOnlyCookie中获取刷新令牌
        Cookie refreshCookie = WebUtils.getCookie(request, "refreshToken");
        if (refreshCookie == null){
            log.warn("刷新令牌为空");
            return ResponseEntity.status(401).body(Result.fail(ResultCode.UNAUTHORIZED,"登录已过期，请重新登录"));
        }
        String refreshToken = refreshCookie.getValue();
        try {
            Claims claims = jwtTokenUtils.parseRefreshToken(refreshToken);
            log.info("刷新令牌，userId: {}", claims.get("userId", Long.class));
            Long userId = claims.get("userId", Long.class);

            // 校验 Redis 中的 refreshToken
            String redisKey = "refreshToken:" + userId;
            String savedRefreshToken = stringRedisTemplate.opsForValue().get(redisKey);

            if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
                log.warn("刷新令牌无效或已过期，userId: {}", userId);
                return ResponseEntity.status(401).body(Result.fail(ResultCode.UNAUTHORIZED, "登录状态失效，请重新登录"));
            }

            Map<String,Object> map = new HashMap<>();
            map.put("userId",userId);
            String newAccessToken = jwtTokenUtils.generateAccessToken(map);
            // 刷新令牌在其固定的七天有效期内保持稳定，避免多个标签页并发刷新互相踢出。
            return ResponseEntity.ok(Result.success(newAccessToken));
        } catch (Exception e) {
            log.error("刷新令牌失败: {}", e.getMessage());
            return ResponseEntity.status(401).body(Result.fail(ResultCode.UNAUTHORIZED,"登录已过期，请重新登录"));
        }
    }
}
