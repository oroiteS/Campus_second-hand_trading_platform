package com.campus.product_management_seller.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 商品描述更新请求DTO
 */
public class CommodityDescriptionUpdateRequest {
    
    @NotBlank(message = "商品ID不能为空")
    private String commodityId;
    
    @NotBlank(message = "卖家ID不能为空")
    private String sellerId;
    
    private String description; // 允许为空，表示清空描述
    
    public CommodityDescriptionUpdateRequest() {}
    
    public CommodityDescriptionUpdateRequest(String commodityId, String sellerId, String description) {
        this.commodityId = commodityId;
        this.sellerId = sellerId;
        this.description = description;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "CommodityDescriptionUpdateRequest{" +
                "commodityId='" + commodityId + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}