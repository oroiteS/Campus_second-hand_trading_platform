package com.campus.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WalletApplication {
    public static void main(String[] args) {
        SpringApplication.run(WalletApplication.class, args);
        System.out.println("Wallet API 文档地址: http://localhost:8081/api-8081/user/account");
    }
}
