package com.campus.ordermanagement.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 获取订单统计请求DTO
 */
@Schema(description = "获取订单统计请求")
public class OrderStatisticsRequest {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "user123")
    @NotBlank(message = "用户ID不能为空")
    @Size(max = 9, message = "用户ID长度不能超过9位")
    @JsonProperty("userId")
    private String userId;

    /**
     * 角色
     */
    @Schema(description = "角色", example = "buyer", allowableValues = {"buyer", "seller"})
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(buyer|seller)$", 
             message = "角色必须是：buyer 或 seller 中的一个")
    @JsonProperty("role")
    private String role;

    // 默认构造函数
    public OrderStatisticsRequest() {}

    // 带参构造函数
    public OrderStatisticsRequest(String userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    // Getter and Setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "OrderStatisticsRequest{" +
                "userId='" + userId + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}