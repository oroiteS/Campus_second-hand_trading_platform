package com.campus.wallet.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BalanceRequest {
    @NotNull(message = "用户ID不能为空")
    @NotBlank(message = "用户ID不能为空字符串")
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "BalanceRequest{" +
                "userId='" + userId + '\'' +
                '}';
    }
}