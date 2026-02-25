package com.eagleeye.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 用于生成和验证 Token
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * JWT 密钥（从配置文件读取）
     */
    @Value("${jwt.secret:eagleeye-finance-platform-secret-key-2026}")
    private String secret;

    /**
     * Token 有效期（毫秒，默认 24 小时）
     */
    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    /**
     * 获取加密密钥
     */
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return JWT Token
     */
    public String generateToken(Long userId, String username) {
        return generateToken(userId, username, null);
    }

    /**
     * 生成 Token（包含部门ID）
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param deptId   部门ID
     * @return JWT Token
     */
    public String generateToken(Long userId, String username, Long deptId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        if (deptId != null) {
            claims.put("deptId", deptId);
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 从 Token 中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            if (claims != null && claims.get("userId") != null) {
                Object userId = claims.get("userId");
                if (userId instanceof Number) {
                    return ((Number) userId).longValue();
                }
                return Long.valueOf(userId.toString());
            }
        } catch (Exception e) {
            log.warn("从Token中获取userId失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 从 Token 中获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            if (claims != null && claims.get("username") != null) {
                return claims.get("username").toString();
            }
        } catch (Exception e) {
            log.warn("从Token中获取username失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 从 Token 中获取部门ID
     *
     * @param token JWT Token
     * @return 部门ID
     */
    public Long getDeptIdFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            if (claims != null && claims.get("deptId") != null) {
                Object deptId = claims.get("deptId");
                if (deptId instanceof Number) {
                    return ((Number) deptId).longValue();
                }
                return Long.valueOf(deptId.toString());
            }
        } catch (Exception e) {
            log.warn("从Token中获取deptId失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT Token
     * @return true-有效，false-无效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token 已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的 Token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Token 格式错误: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("Token 签名验证失败: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Token 为空或非法: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 解析 Token
     *
     * @param token JWT Token
     * @return Claims
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 判断 Token 是否过期
     *
     * @param token JWT Token
     * @return true-已过期，false-未过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * 获取 Token 过期时间
     *
     * @param token JWT Token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }
}
