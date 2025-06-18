package com.campus.ordermanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * Swagger配置类
 * 配置API文档生成和测试环境
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8095}")
    private String serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("校园二手交易平台 - 订单管理API")
                        .description("## 订单管理模块REST API文档\n\n" +
                                "### 功能概述\n" +
                                "- 订单创建和管理\n" +
                                "- 订单状态流转控制\n" +
                                "- 多维度订单查询\n" +
                                "- 订单统计分析\n\n" +
                                "### 订单状态流转\n" +
                                "```\n" +
                                "PENDING_PAYMENT (待付款)\n" +
                                "       ↓\n" +
                                "PENDING_TRANSACTION (待交易)\n" +
                                "       ↓\n" +
                                "COMPLETED (已完成)\n" +
                                "```\n\n" +
                                "### 测试说明\n" +
                                "1. 所有UUID参数请使用标准UUID格式\n" +
                                "2. 金额字段支持小数点后两位\n" +
                                "3. 时间格式为ISO 8601标准\n" +
                                "4. 分页参数：page从0开始，size默认为10")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("校园二手交易平台开发团队")
                                .email("dev@campus.com")
                                .url("https://github.com/campus-platform"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort + contextPath)
                                .description("本地开发环境"),
                        new Server()
                                .url("http://127.0.0.1:" + serverPort + contextPath)
                                .description("本地测试环境")
                ));
    }
}