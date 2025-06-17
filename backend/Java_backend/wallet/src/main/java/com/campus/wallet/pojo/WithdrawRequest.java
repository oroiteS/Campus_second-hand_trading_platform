package com.campus.wallet.pojo;

import java.math.BigDecimal;

public class WithdrawRequest {
    private BigDecimal amount;
    // 必须提供getter和setter
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
