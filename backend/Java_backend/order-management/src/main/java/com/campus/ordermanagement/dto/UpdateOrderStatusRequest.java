package com.campus.ordermanagement.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 更新订单状态请求DTO
 */
public class UpdateOrderStatusRequest {

    /**
     * 订单ID
     */
    @NotBlank(message = "订单ID不能为空")
    @Size(max = 36, message = "订单ID长度不能超过36位")
    @JsonProperty("orderId")
    private String orderId;

    /**
     * 新的订单状态
     * 可选值：pending_payment, pending_transaction, completed
     */
    @NotBlank(message = "订单状态不能为空")
    @Pattern(regexp = "^(pending_payment|pending_transaction|completed)$", 
             message = "订单状态必须是：pending_payment, pending_transaction, completed 中的一个")
    @JsonProperty("orderStatus")
    private String orderStatus;

    /**
     * 操作用户ID（可选，用于权限验证）
     */
    @JsonProperty("operatorId")
    private String operatorId;

    /**
     * 备注信息（可选）
     */
    @Size(max = 500, message = "备注信息长度不能超过500位")
    @JsonProperty("remark")
    private String remark;

    // 默认构造函数
    public UpdateOrderStatusRequest() {}

    // 带参构造函数
    public UpdateOrderStatusRequest(String orderId, String orderStatus) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }

    // 带参构造函数（包含操作者和备注）
    public UpdateOrderStatusRequest(String orderId, String orderStatus, String operatorId, String remark) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.operatorId = operatorId;
        this.remark = remark;
    }

    // Getter和Setter方法
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
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
        return "UpdateOrderStatusRequest{" +
                "orderId='" + orderId + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}