package com.example.view_product_information;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.view_product_information.mapper")
public class ViewProductInformationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ViewProductInformationApplication.class, args);
    }

}
