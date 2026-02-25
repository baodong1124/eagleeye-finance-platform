package com.eagleeye.common.config;

import com.eagleeye.common.interceptor.JwtAuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 注册拦截器等
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthenticationInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除登录相关接口和静态资源
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/v3/api-docs",
                        "/swagger-config",
                        "/doc.html",
                        "/webjars/**",
                        "/knife4j/**",
                        "/favicon.ico",
                        "/druid/**",
                        "/actuator/**",
                        "/error",
                        "/",
                        "/**/*.html",
                        "/**/*.js",
                        "/**/*.css",
                        "/**/*.png",
                        "/**/*.ico",
                        "/.well-known/**"
                );
    }
}
