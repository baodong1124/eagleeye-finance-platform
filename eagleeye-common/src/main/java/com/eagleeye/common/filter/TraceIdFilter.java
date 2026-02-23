package com.eagleeye.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

/**
 * 全局请求追踪过滤器
 * 为每个请求生成唯一的traceId，并贯穿日志
 */
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "traceIdFilter")
public class TraceIdFilter implements Filter {

    private static final String TRACE_ID = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 从请求头获取traceId，如果没有则生成新的
        String traceId = httpRequest.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }

        // 将traceId放入MDC，以便在日志中使用
        MDC.put(TRACE_ID, traceId);

        try {
            // 将traceId添加到响应头
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");

            chain.doFilter(request, response);
        } finally {
            // 清除MDC
            MDC.remove(TRACE_ID);
        }
    }
}
