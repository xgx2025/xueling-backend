package com.hope.xueling.common.security;

import com.hope.xueling.common.util.JwtTokenUtils;
import com.hope.xueling.common.util.ThreadLocalUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 鉴权过滤器：在 Spring Security 判定路由权限之前解析 Bearer 访问令牌，
 * 把用户 ID 写入认证上下文、把令牌载荷写入线程上下文，并按白名单授予 ROLE_ADMIN。
 * @author 谢光湘
 * @since 2026/10/24
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final AdminAccessPolicy adminAccessPolicy;

    /**
     * 解析并校验访问令牌；未携带 Authorization 头时按匿名请求放行，由路由规则决定是否拒绝
     * @param request 当前请求
     * @param response 当前响应
     * @param chain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            String authorization = request.getHeader("Authorization");
            if (authorization != null) {
                if (!authorization.startsWith("Bearer ")) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                Claims claims;
                try {
                    claims = jwtTokenUtils.parseAccessToken(authorization.substring(7).trim());
                    Number userIdClaim = claims.get("userId", Number.class);
                    if (userIdClaim == null || userIdClaim.longValue() <= 0) {
                        throw new IllegalArgumentException("Invalid user ID");
                    }
                    long userId = userIdClaim.longValue();
                    List<SimpleGrantedAuthority> authorities = adminAccessPolicy.isAdmin(userId)
                            ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN")) : List.of();
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(userId, null, authorities));
                    ThreadLocalUtils.set(claims);
                } catch (JwtException | IllegalArgumentException e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
            chain.doFilter(request, response);
        } finally {
            ThreadLocalUtils.remove();
            SecurityContextHolder.clearContext();
        }
    }
}
