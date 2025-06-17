package com.campus.ordermanagement.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 根据商品ID查询订单请求DTO
 */
@Schema(description = "根据商品ID查询订单请求")
public class QueryOrdersByCommodityRequest {

    /**
     * 商品ID
     */
    @Schema(description = "商品ID", example = "commodity123")
    @NotBlank(message = "商品ID不能为空")
    @Size(max = 36, message = "商品ID长度不能超过36位")
    @JsonProperty("commodityId")
    private String commodityId;

    // 默认构造函数
    public QueryOrdersByCommodityRequest() {}

    // 带参构造函数
    public QueryOrdersByCommodityRequest(String commodityId) {
        this.commodityId = commodityId;
    }

    // Getter and Setter
    public String getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    @Override
    public String toString() {
        return "QueryOrdersByCommodityRequest{" +
                "commodityId='" + commodityId + '\'' +
                '}';
    }
}