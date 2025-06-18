package com.campus.wallet.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "卖家退款请求体")
public class SellerRefundRequest {
    
    @Schema(description = "用户ID", example = "U10000001", required = true)
    @NotNull(message = "用户ID不能为空")
    @NotBlank(message = "用户ID不能为空字符串")
    private String userId;
    
    @Schema(description = "订单ID", example = "ORDER123456", required = true)
    @NotNull(message = "订单ID不能为空")
    @NotBlank(message = "订单ID不能为空字符串")
    private String orderID;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
}