package com.campus.productquery;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.campus.productquery.dao")
public class ProductQueryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductQueryApplication.class, args);
    }

}
