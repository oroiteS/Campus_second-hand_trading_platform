package com.campus.product_management_seller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品创建请求DTO
 */
public class CommodityCreateRequest {
    
    @NotBlank(message = "商品名称不能为空")
    private String commodityName;
    
    private String commodityDescription;
    
    @NotNull(message = "商品类别不能为空")
    private Integer categoryId;
    
    private List<Integer> tagsId;
    
    @NotNull(message = "商品价格不能为空")
    @Positive(message = "商品价格必须大于0")
    private BigDecimal currentPrice;
    
    private String mainImageUrl;
    
    private String imageList;
    
    @NotNull(message = "商品数量不能为空")
    @Positive(message = "商品数量必须大于0")
    private Integer quantity = 1;
    
    @NotBlank(message = "卖家ID不能为空")
    private String sellerId;
    
    @NotBlank(message = "商品新旧度不能为空")
    private String newness;
    
    public CommodityCreateRequest() {}
    
    public CommodityCreateRequest(String commodityName, String commodityDescription, 
                                 Integer categoryId, List<Integer> tagsId, BigDecimal currentPrice,
                                 String mainImageUrl, String imageList, Integer quantity, String sellerId, String newness) {
        this.commodityName = commodityName;
        this.commodityDescription = commodityDescription;
        this.categoryId = categoryId;
        this.tagsId = tagsId;
        this.currentPrice = currentPrice;
        this.mainImageUrl = mainImageUrl;
        this.imageList = imageList;
        this.quantity = quantity;
        this.sellerId = sellerId;
        this.newness = newness;
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
    
    public Integer getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    
    public List<Integer> getTagsId() {
        return tagsId;
    }
    
    public void setTagsId(List<Integer> tagsId) {
        this.tagsId = tagsId;
    }
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
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
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
    
    public String getNewness() {
        return newness;
    }
    
    public void setNewness(String newness) {
        this.newness = newness;
    }
    
    @Override
    public String toString() {
        return "CommodityCreateRequest{" +
                "commodityName='" + commodityName + '\'' +
                ", commodityDescription='" + commodityDescription + '\'' +
                ", categoryId=" + categoryId +
                ", tagsId='" + tagsId + '\'' +
                ", currentPrice=" + currentPrice +
                ", mainImageUrl='" + mainImageUrl + '\'' +
                ", imageList='" + imageList + '\'' +
                ", quantity=" + quantity +
                ", sellerId='" + sellerId + '\'' +
                ", newness='" + newness + '\'' +
                '}';
    }
}