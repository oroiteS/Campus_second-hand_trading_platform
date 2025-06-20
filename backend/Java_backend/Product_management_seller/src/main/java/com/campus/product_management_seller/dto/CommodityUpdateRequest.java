package com.campus.product_management_seller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * 商品信息更新请求DTO
 */
public class CommodityUpdateRequest {
    
    @NotBlank(message = "商品ID不能为空")
    private String commodityId;
    
    @NotBlank(message = "卖家ID不能为空")
    private String sellerId;
    
    private String commodityName;
    
    private String commodityDescription;
    
    @Positive(message = "商品价格必须大于0")
    private BigDecimal currentPrice;
    
    private String newness;
    
    @Positive(message = "商品数量必须大于0")
    private Integer quantity;
    
    private String mainImageUrl;
    
    private String imageList;
    
    /**
     * 是否删除原有图片（true=删除原有图片，false=保留原有图片）
     */
    private Boolean deleteExistingImages;
    
    public CommodityUpdateRequest() {}
    
    public CommodityUpdateRequest(String commodityId, String sellerId, String commodityName, 
                                 String commodityDescription, BigDecimal currentPrice, String newness,
                                 Integer quantity, String mainImageUrl, String imageList, Boolean deleteExistingImages) {
        this.commodityId = commodityId;
        this.sellerId = sellerId;
        this.commodityName = commodityName;
        this.commodityDescription = commodityDescription;
        this.currentPrice = currentPrice;
        this.newness = newness;
        this.quantity = quantity;
        this.mainImageUrl = mainImageUrl;
        this.imageList = imageList;
        this.deleteExistingImages = deleteExistingImages;
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
    
    public String getCommodityName() {
        return commodityName;
    }
    
    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }
    
    public String getCommodityDescription() {
        return commodityDescription;
    }
    
    public void setCommodityDescription(String commodityDescription) {
        this.commodityDescription = commodityDescription;
    }
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public String getNewness() {
        return newness;
    }
    
    public void setNewness(String newness) {
        this.newness = newness;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getMainImageUrl() {
        return mainImageUrl;
    }
    
    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }
    
    public String getImageList() {
        return imageList;
    }
    
    public void setImageList(String imageList) {
        this.imageList = imageList;
    }
    
    public Boolean getDeleteExistingImages() {
        return deleteExistingImages;
    }
    
    public void setDeleteExistingImages(Boolean deleteExistingImages) {
        this.deleteExistingImages = deleteExistingImages;
    }
    
    @Override
    public String toString() {
        return "CommodityUpdateRequest{" +
                "commodityId='" + commodityId + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", commodityName='" + commodityName + '\'' +
                ", commodityDescription='" + commodityDescription + '\'' +
                ", currentPrice=" + currentPrice +
                ", newness='" + newness + '\'' +
                ", quantity=" + quantity +
                ", mainImageUrl='" + mainImageUrl + '\'' +
                ", imageList='" + imageList + '\'' +
                ", deleteExistingImages=" + deleteExistingImages +
                '}';
    }
}