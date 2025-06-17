package com.campus.wallet.pojo;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "提现请求体")
public class WithdrawRequest {
    
    @Schema(description = "用户ID", example = "U10000001", required = true)
    @NotNull(message = "用户ID不能为空")
    @NotBlank(message = "用户ID不能为空字符串")
    private String userId;
    
    @Schema(description = "提现金额", example = "50.00", required = true)
    @NotNull(message = "提现金额不能为空")
    private BigDecimal amount;
    
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public BigDecimal getAmount() { 
        return amount; 
    }
    
    public void setAmount(BigDecimal amount) { 
        this.amount = amount; 
    }
}
