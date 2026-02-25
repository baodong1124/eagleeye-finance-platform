package com.eagleeye.common.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Knife4j配置
 * API文档生成配置
 */
@Configuration
@EnableKnife4j
public class Knife4jConfig {

    private static final String SECURITY_SCHEME_NAME = "Authorization";

    /**
     * 配置OpenAPI基本信息和安全认证
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EagleEye Finance Platform API")
                        .version("1.0.0")
                        .description("集团企业资金管理与费用管控系统API文档")
                        .contact(new Contact()
                                .name("EagleEye Team")
                                .email("support@eagleeye.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                // 配置全局安全要求
                .security(Collections.singletonList(new SecurityRequirement().addList(SECURITY_SCHEME_NAME)))
                // 配置安全方案
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    /**
     * 分组配置
     */
    @Bean
    public GroupedOpenApi defaultApi() {
        return GroupedOpenApi.builder()
                .group("default")
                .displayName("全部接口")
                .packagesToScan("com.eagleeye.system.controller", "com.eagleeye.expense.controller")
                .pathsToMatch("/api/**")
                .build();
    }
}
