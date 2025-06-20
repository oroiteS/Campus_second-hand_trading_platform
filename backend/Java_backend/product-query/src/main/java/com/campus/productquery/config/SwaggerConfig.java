package com.campus.productquery.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger配置类
 * 配置商品查询服务的API文档
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("商品查询服务 API")
                        .description("校园二手交易平台 - 商品查询服务接口文档")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@campus.com")
                                .url("https://github.com/campus-trading"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8200/product-query")
                                .description("开发环境"),
                        new Server()
                                .url("https://api.campus-trading.com/product-query")
                                .description("生产环境")
                ));
    }
}