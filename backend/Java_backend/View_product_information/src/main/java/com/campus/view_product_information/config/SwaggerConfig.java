package com.campus.view_product_information.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置类
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("校园二手交易平台 - 商品信息查看模块 API")
                        .version("1.0.0")
                        .description("提供商品详情查看功能的REST API接口")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@campus-trading.com")));
    }
}