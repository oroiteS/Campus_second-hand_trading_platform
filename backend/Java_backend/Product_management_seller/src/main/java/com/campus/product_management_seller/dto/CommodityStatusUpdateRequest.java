package com.campus.product_management_seller.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 商品状态更新请求DTO
 * 简化版本，只包含商品ID和卖家ID
 */
public class CommodityStatusUpdateRequest {
    
    @NotBlank(message = "商品ID不能为空")
    private String commodityId;
    
    @NotBlank(message = "卖家ID不能为空")
    private String sellerId;
    
    public CommodityStatusUpdateRequest() {}
    
    public CommodityStatusUpdateRequest(String commodityId, String sellerId) {
        this.commodityId = commodityId;
        this.sellerId = sellerId;
    }
    
    public String getCommodityId() {
        return commodityId;
    }
    
    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }
    
    public String getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
    
    @Override
    public String toString() {
        return "CommodityStatusUpdateRequest{" +
                "commodityId='" + commodityId + '\'' +
                ", sellerId='" + sellerId + '\'' +
                '}';
    }
}