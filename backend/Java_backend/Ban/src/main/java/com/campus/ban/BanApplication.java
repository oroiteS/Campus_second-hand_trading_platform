package com.campus.ban;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户封号管理模块启动类
 * 端口: 8082
 * 功能: 用户封号与解封管理
 */
@SpringBootApplication
@MapperScan("com.campus.ban.mapper")
public class BanApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BanApplication.class, args);
        System.out.println("\n==================================");
        System.out.println("用户封号管理模块启动成功！");
        System.out.println("端口: 8082");
        System.out.println("API文档: http://localhost:8082/api-8082/ban");
        System.out.println("CORS限制: 仅允许9418端口调用");
        System.out.println("==================================");
    }
}
