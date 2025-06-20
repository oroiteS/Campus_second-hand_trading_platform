package com.campus.admin_login.service;
/*
*@belongsPackage: com.campus.admin_login.service
*@author: syn
*@createTime: 2025-06-20
*@description: 管理员登录服务类
*@version: 1.0
*/
import com.campus.admin_login.dto.LoginRequest;
import com.campus.admin_login.entity.Root;
import com.campus.admin_login.repository.RootRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class AdminLoginService {

    @Autowired
    private RootRepository rootRepository;

    public boolean login(LoginRequest loginRequest) {
        Optional<Root> rootOpt = rootRepository.findByRootId(loginRequest.getRootId());

        if (rootOpt.isEmpty()) {
            return false;
        }

        Root root = rootOpt.get();
        String hashedPassword = hashPassword(loginRequest.getPassword());

        return root.getPassword().equals(hashedPassword);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256算法不可用", e);
        }
    }
}
