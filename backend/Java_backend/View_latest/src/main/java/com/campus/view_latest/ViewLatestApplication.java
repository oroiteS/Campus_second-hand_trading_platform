package com.campus.view_latest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * View Latest 服务启动类
 * 功能：提供最新商品查询和用户信息查询服务
 * 端口：8087
 * API文档：http://localhost:8087/swagger-ui.html
 * 跨域：允许所有来源
 */
@SpringBootApplication
@MapperScan("com.campus.view_latest.mapper")
public class ViewLatestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ViewLatestApplication.class, args);
        System.out.println("\n=== View Latest Service Started Successfully ===");
        System.out.println("Port: 8087");
        System.out.println("API Documentation: http://localhost:8087/swagger-ui.html");
        System.out.println("Latest Commodities API: http://localhost:8087/api/commodities/latest");
        System.out.println("All Users API: http://localhost:8087/api/users/all");
        System.out.println("================================================\n");
    }

}
