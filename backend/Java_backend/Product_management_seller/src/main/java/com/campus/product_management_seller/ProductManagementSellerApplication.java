package com.campus.product_management_seller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.campus.product_management_seller.repository")
@EnableTransactionManagement
public class ProductManagementSellerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductManagementSellerApplication.class, args);
        System.out.println("=== 商品管理卖家服务启动成功 ===");
        System.out.println("API文档地址: http://localhost:8084/api-8084/commodity/health");
        System.out.println("服务端口: 8084");
    }

}
