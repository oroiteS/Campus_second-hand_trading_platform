package com.campus.productquery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 商品ID查询请求DTO
 */
public class CommodityIdRequest {

    /**
     * 商品ID
     */
    @JsonProperty("commodityId")
    @Schema(description = "商品ID", example = "COMM001", required = true)
    @NotBlank(message = "商品ID不能为空")
    private String commodityId;

    public CommodityIdRequest() {}

    public CommodityIdRequest(String commodityId) {
        this.commodityId = commodityId;
    }

    public String getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    @Override
    public String toString() {
        return "CommodityIdRequest{" +
                "commodityId='" + commodityId + '\'' +
                '}';
    }
}