package com.campus.productquery;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.campus.productquery.dao")
public class ProductQueryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductQueryApplication.class, args);
        
        System.out.println("API文档地址: http://localhost:8096/api-8096/v1/commodities");
    }

}
