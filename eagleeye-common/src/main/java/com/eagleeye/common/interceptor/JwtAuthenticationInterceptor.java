package com.eagleeye.common.interceptor;

import com.eagleeye.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 认证拦截器
 * 验证请求中的 Token 是否有效
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    /**
     * 不需要验证 Token 的路径
     */
    private static final String[] EXCLUDE_PATHS = {
            "/api/auth/login",
            "/api/auth/refresh",
            "/swagger-ui",
            "/v3/api-docs",
            "/doc.html",
            "/webjars",
            "/favicon.ico",
            "/druid",
            "/actuator"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();

        // 1. 检查是否是排除路径
        for (String excludePath : EXCLUDE_PATHS) {
            if (requestUri.startsWith(excludePath)) {
                return true;
            }
        }

        // 2. 获取 Token
        String token = extractToken(request);
        if (token == null) {
            log.warn("请求未携带 Token，uri={}", requestUri);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或Token无效\"}");
            return false;
        }

        // 3. 验证 Token
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token 验证失败，uri={}", requestUri);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token无效或已过期\"}");
            return false;
        }

        // 4. 将用户信息存入请求属性，供后续使用
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        Long deptId = jwtUtil.getDeptIdFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        request.setAttribute("deptId", deptId);
        request.setAttribute("token", token);

        log.debug("Token 验证成功，userId={}, username={}, uri={}", userId, username, requestUri);
        return true;
    }

    /**
     * 从请求中提取 Token
     *
     * @param request HTTP 请求
     * @return Token，如果没有则返回 null
     */
    private String extractToken(HttpServletRequest request) {
        // 1. 从 Header 中获取
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2. 从参数中获取（用于调试）
        String token = request.getParameter("token");
        if (token != null && !token.isEmpty()) {
            return token;
        }

        return null;
    }
}
