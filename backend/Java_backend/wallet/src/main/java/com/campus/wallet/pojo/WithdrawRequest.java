package com.campus.wallet.pojo;

import java.math.BigDecimal;

public class WithdrawRequest {
    private String userId;
    private BigDecimal amount;
    // 必须提供getter和setter
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
