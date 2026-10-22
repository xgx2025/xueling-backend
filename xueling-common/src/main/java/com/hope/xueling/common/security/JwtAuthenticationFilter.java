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

/** Authenticates bearer tokens before Spring Security evaluates route permissions. */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final AdminAccessPolicy adminAccessPolicy;

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
