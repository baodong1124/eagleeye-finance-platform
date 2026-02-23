package com.eagleeye.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置
 * API文档生成配置
 */
@Configuration
public class Knife4jConfig {

    /**
     * 配置OpenAPI基本信息
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
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }

    /**
     * 系统管理模块API分组
     */
    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
                .group("system")
                .pathsToMatch("/system/**")
                .displayName("系统管理")
                .build();
    }

    /**
     * 账户管理模块API分组
     */
    @Bean
    public GroupedOpenApi accountApi() {
        return GroupedOpenApi.builder()
                .group("account")
                .pathsToMatch("/account/**")
                .displayName("账户管理")
                .build();
    }

    /**
     * 费用管理模块API分组
     */
    @Bean
    public GroupedOpenApi expenseApi() {
        return GroupedOpenApi.builder()
                .group("expense")
                .pathsToMatch("/expense/**")
                .displayName("费用管理")
                .build();
    }

    /**
     * 支付管理模块API分组
     */
    @Bean
    public GroupedOpenApi paymentApi() {
        return GroupedOpenApi.builder()
                .group("payment")
                .pathsToMatch("/payment/**")
                .displayName("支付管理")
                .build();
    }

    /**
     * 数据分析模块API分组
     */
    @Bean
    public GroupedOpenApi analysisApi() {
        return GroupedOpenApi.builder()
                .group("analysis")
                .pathsToMatch("/analysis/**")
                .displayName("数据分析")
                .build();
    }
}
