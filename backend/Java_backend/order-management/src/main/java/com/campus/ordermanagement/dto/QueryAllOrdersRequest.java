package com.campus.ordermanagement.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 分页查询所有订单请求DTO
 */
@Schema(description = "分页查询所有订单请求")
public class QueryAllOrdersRequest {

    /**
     * 页码（从1开始）
     */
    @Schema(description = "页码（从1开始）", example = "1")
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    @JsonProperty("pageNum")
    private Integer pageNum;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    @NotNull(message = "每页大小不能为空")
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    @JsonProperty("pageSize")
    private Integer pageSize;

    // 默认构造函数
    public QueryAllOrdersRequest() {}

    // 带参构造函数
    public QueryAllOrdersRequest(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    // Getter and Setter
    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "QueryAllOrdersRequest{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }
}