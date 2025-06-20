package com.campus.productquery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 卖家和状态查询请求DTO
 */
public class SellerStatusRequest {

    /**
     * 卖家ID
     */
    @JsonProperty("sellerId")
    @Schema(description = "卖家ID", example = "USER001", required = true)
    @NotBlank(message = "卖家ID不能为空")
    private String sellerId;

    /**
     * 商品状态
     */
    @JsonProperty("commodityStatus")
    @Schema(description = "商品状态", example = "available", required = true, allowableValues = {"available", "sold", "reserved"})
    @NotBlank(message = "商品状态不能为空")
    private String commodityStatus;

    public SellerStatusRequest() {}

    public SellerStatusRequest(String sellerId, String commodityStatus) {
        this.sellerId = sellerId;
        this.commodityStatus = commodityStatus;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getCommodityStatus() {
        return commodityStatus;
    }

    public void setCommodityStatus(String commodityStatus) {
        this.commodityStatus = commodityStatus;
    }

    @Override
    public String toString() {
        return "SellerStatusRequest{" +
                "sellerId='" + sellerId + '\'' +
                ", commodityStatus='" + commodityStatus + '\'' +
                '}';
    }
}