package com.eagleeye.common.util;

import cn.hutool.core.lang.UUID;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

/**
 * 请求追踪工具类
 * 用于生成和追踪请求的唯一ID
 */
public class TraceIdUtil {

    private static final String TRACE_ID_KEY = "traceId";

    /**
     * 生成追踪ID
     */
    public static String generateTraceId() {
        return UUID.fastUUID().toString(true);
    }

    /**
     * 设置追踪ID到MDC
     */
    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID_KEY, traceId);
    }

    /**
     * 获取当前追踪ID
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    /**
     * 清除追踪ID
     */
    public static void clearTraceId() {
        MDC.remove(TRACE_ID_KEY);
    }

    /**
     * 从请求头中获取追踪ID
     */
    public static String getTraceIdFromRequest(HttpServletRequest request) {
        String traceId = request.getHeader("X-Trace-Id");
        return traceId != null ? traceId : generateTraceId();
    }
}
