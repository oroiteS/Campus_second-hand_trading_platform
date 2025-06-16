package com.campus.ordermanagement.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 根据订单ID查询请求DTO
 */
@Schema(description = "根据订单ID查询请求")
public class QueryOrderByIdRequest {

    /**
     * 订单ID
     */
    @Schema(description = "订单ID", example = "ORD123456")
    @NotBlank(message = "订单ID不能为空")
    @Size(max = 36, message = "订单ID长度不能超过36位")
    @JsonProperty("orderId")
    private String orderId;

    // 默认构造函数
    public QueryOrderByIdRequest() {}

    // 带参构造函数
    public QueryOrderByIdRequest(String orderId) {
        this.orderId = orderId;
    }

    // Getter and Setter
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "QueryOrderByIdRequest{" +
                "orderId='" + orderId + '\'' +
                '}';
    }
}