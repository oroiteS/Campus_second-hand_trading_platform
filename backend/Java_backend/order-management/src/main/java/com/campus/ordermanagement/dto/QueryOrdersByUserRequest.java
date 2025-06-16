package com.campus.ordermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 根据用户ID查询用户参与的所有订单请求DTO
 * 查询用户作为买家或卖家参与的所有订单
 */
@Schema(description = "根据用户ID查询用户参与的所有订单请求")
public class QueryOrdersByUserRequest {

    @Schema(description = "用户ID", example = "user123", required = true)
    @NotBlank(message = "用户ID不能为空")
    @Size(max = 50, message = "用户ID长度不能超过50个字符")
    @JsonProperty("user_id")
    private String userId;

    /**
     * 默认构造函数
     */
    public QueryOrdersByUserRequest() {
    }

    /**
     * 带参数的构造函数
     * 
     * @param userId 用户ID
     */
    public QueryOrdersByUserRequest(String userId) {
        this.userId = userId;
    }

    /**
     * 获取用户ID
     * 
     * @return 用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     * 
     * @param userId 用户ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "QueryOrdersByUserRequest{" +
                "userId='" + userId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryOrdersByUserRequest that = (QueryOrdersByUserRequest) o;
        return userId != null ? userId.equals(that.userId) : that.userId == null;
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }
}