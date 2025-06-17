package com.campus.ordermanagement.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 确认付款请求DTO
 */
@Schema(description = "确认付款请求")
public class ConfirmPaymentRequest {

    /**
     * 订单ID
     */
    @Schema(description = "订单ID", example = "ORD123456")
    @NotBlank(message = "订单ID不能为空")
    @Size(max = 36, message = "订单ID长度不能超过36位")
    @JsonProperty("orderId")
    private String orderId;

    /**
     * 操作用户ID（可选，用于权限验证）
     */
    @Schema(description = "操作用户ID", example = "user123")
    @JsonProperty("operatorId")
    private String operatorId;

    /**
     * 备注信息（可选）
     */
    @Schema(description = "备注信息", example = "付款已确认")
    @Size(max = 500, message = "备注信息长度不能超过500位")
    @JsonProperty("remark")
    private String remark;

    // 默认构造函数
    public ConfirmPaymentRequest() {}

    // 带参构造函数
    public ConfirmPaymentRequest(String orderId) {
        this.orderId = orderId;
    }

    public ConfirmPaymentRequest(String orderId, String operatorId) {
        this.orderId = orderId;
        this.operatorId = operatorId;
    }

    // Getter and Setter
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "ConfirmPaymentRequest{" +
                "orderId='" + orderId + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}