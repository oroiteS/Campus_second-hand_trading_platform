package com.example.product_management_seller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 商品状态更新请求DTO
 */
public class CommodityStatusUpdateRequest {
    
    @NotBlank(message = "商品ID不能为空")
    private String commodityId;
    
    @NotBlank(message = "卖家ID不能为空")
    private String sellerId;
    
    @NotNull(message = "商品状态不能为空")
    private String status; // "on_sale" 或 "off_sale"
    
    public CommodityStatusUpdateRequest() {}
    
    public CommodityStatusUpdateRequest(String commodityId, String sellerId, String status) {
        this.commodityId = commodityId;
        this.sellerId = sellerId;
        this.status = status;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "CommodityStatusUpdateRequest{" +
                "commodityId='" + commodityId + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}