package com.eagleeye;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * EagleEye Finance Platform 主启动类
 * 集团企业资金管理与费用管控系统
 *
 * @author EagleEye Team
 * @version 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.eagleeye")
@EnableTransactionManagement
@EnableAsync
@MapperScan("com.eagleeye.**.mapper")
public class EagleEyeFinanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EagleEyeFinanceApplication.class, args);
        System.out.println("========================================");
        System.out.println("EagleEye Finance Platform 启动成功！");
        System.out.println("API文档地址: http://localhost:8080/doc.html");
        System.out.println("Druid监控地址: http://localhost:8080/druid");
        System.out.println("========================================");
    }
}
