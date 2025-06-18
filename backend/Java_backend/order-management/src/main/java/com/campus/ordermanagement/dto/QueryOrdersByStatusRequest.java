package com.campus.ordermanagement.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 根据订单状态查询请求DTO
 */
@Schema(description = "根据订单状态查询请求")
public class QueryOrdersByStatusRequest {

    /**
     * 订单状态
     */
    @Schema(description = "订单状态", example = "pending_payment", allowableValues = {"pending_payment", "pending_transaction", "completed"})
    @NotBlank(message = "订单状态不能为空")
    @Pattern(regexp = "^(pending_payment|pending_transaction|completed)$", 
             message = "订单状态必须是：pending_payment, pending_transaction, completed 中的一个")
    @JsonProperty("status")
    private String status;

    // 默认构造函数
    public QueryOrdersByStatusRequest() {}

    // 带参构造函数
    public QueryOrdersByStatusRequest(String status) {
        this.status = status;
    }

    // Getter and Setter
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "QueryOrdersByStatusRequest{" +
                "status='" + status + '\'' +
                '}';
    }
}