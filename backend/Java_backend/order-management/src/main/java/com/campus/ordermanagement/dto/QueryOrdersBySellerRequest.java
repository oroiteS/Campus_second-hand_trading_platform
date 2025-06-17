package com.campus.ordermanagement.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 根据卖家ID查询订单请求DTO
 */
@Schema(description = "根据卖家ID查询订单请求")
public class QueryOrdersBySellerRequest {

    /**
     * 卖家ID
     */
    @Schema(description = "卖家ID", example = "seller123")
    @NotBlank(message = "卖家ID不能为空")
    @Size(max = 9, message = "卖家ID长度不能超过9位")
    @JsonProperty("sellerId")
    private String sellerId;

    // 默认构造函数
    public QueryOrdersBySellerRequest() {}

    // 带参构造函数
    public QueryOrdersBySellerRequest(String sellerId) {
        this.sellerId = sellerId;
    }

    // Getter and Setter
    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    @Override
    public String toString() {
        return "QueryOrdersBySellerRequest{" +
                "sellerId='" + sellerId + '\'' +
                '}';
    }
}