package com.campus.ordermanagement.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 根据买家ID查询订单请求DTO
 */
@Schema(description = "根据买家ID查询订单请求")
public class QueryOrdersByBuyerRequest {

    /**
     * 买家ID
     */
    @Schema(description = "买家ID", example = "buyer123")
    @NotBlank(message = "买家ID不能为空")
    @Size(max = 9, message = "买家ID长度不能超过9位")
    @JsonProperty("buyerId")
    private String buyerId;

    // 默认构造函数
    public QueryOrdersByBuyerRequest() {}

    // 带参构造函数
    public QueryOrdersByBuyerRequest(String buyerId) {
        this.buyerId = buyerId;
    }

    // Getter and Setter
    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    @Override
    public String toString() {
        return "QueryOrdersByBuyerRequest{" +
                "buyerId='" + buyerId + '\'' +
                '}';
    }
}