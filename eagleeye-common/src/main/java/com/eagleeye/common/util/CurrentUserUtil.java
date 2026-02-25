package com.eagleeye.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 当前用户工具类
 * 用于获取当前登录用户的信息
 */
public class CurrentUserUtil {

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID，如果未登录返回 null
     */
    public static Long getCurrentUserId() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        Object userId = request.getAttribute("userId");
        return userId != null ? Long.valueOf(userId.toString()) : null;
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名，如果未登录返回 null
     */
    public static String getCurrentUsername() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        Object username = request.getAttribute("username");
        return username != null ? username.toString() : null;
    }

    /**
     * 获取当前 Token
     *
     * @return Token，如果未登录返回 null
     */
    public static String getCurrentToken() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        Object token = request.getAttribute("token");
        return token != null ? token.toString() : null;
    }

    /**
     * 获取当前 HTTP 请求
     *
     * @return HttpServletRequest
     */
    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest();
    }

    /**
     * 检查是否已登录
     *
     * @return true-已登录，false-未登录
     */
    public static boolean isLogin() {
        return getCurrentUserId() != null;
    }
}
