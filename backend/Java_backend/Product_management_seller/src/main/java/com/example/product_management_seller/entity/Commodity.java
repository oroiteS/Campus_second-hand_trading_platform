package com.example.product_management_seller.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "commodities")
public class Commodity {
    
    @Id
    @Column(name = "commodity_id", length = 36)
    private String commodityId;
    
    @NotBlank(message = "商品名称不能为空")
    @Column(name = "commodity_name", length = 100, nullable = false)
    private String commodityName;
    
    @Column(name = "commodity_description", columnDefinition = "TEXT")
    private String commodityDescription;
    
    @NotNull(message = "商品类别不能为空")
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;
    
    @Column(name = "tags", columnDefinition = "JSON")
    private String tags;
    
    @NotNull(message = "商品价格不能为空")
    @Positive(message = "商品价格必须大于0")
    @Column(name = "current_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal currentPrice;
    
    @Convert(converter = com.example.product_management_seller.converter.CommodityStatusConverter.class)
    @Column(name = "commodity_status", nullable = false)
    private CommodityStatus commodityStatus = CommodityStatus.ON_SALE;
    
    @NotBlank(message = "卖家ID不能为空")
    @Column(name = "seller_id", length = 9, nullable = false)
    private String sellerId;
    
    @Column(name = "main_image_url")
    private String mainImageUrl;
    
    @Column(name = "image_list", columnDefinition = "JSON")
    private String imageList;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 商品状态枚举
    public enum CommodityStatus {
        ON_SALE("on_sale"),
        SOLD("sold"),
        OFF_SALE("off_sale");
        
        private final String value;
        
        CommodityStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        // 根据字符串值查找枚举
        public static CommodityStatus fromValue(String value) {
            for (CommodityStatus status : CommodityStatus.values()) {
                if (status.getValue().equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown CommodityStatus value: " + value);
        }
    }
    
    // 构造函数
    public Commodity() {}
    
    public Commodity(String commodityId, String commodityName, String commodityDescription,
                    Integer categoryId, BigDecimal currentPrice, String sellerId) {
        this.commodityId = commodityId;
        this.commodityName = commodityName;
        this.commodityDescription = commodityDescription;
        this.categoryId = categoryId;
        this.currentPrice = currentPrice;
        this.sellerId = sellerId;
    }
    
    // Getter和Setter方法
    public String getCommodityId() {
        return commodityId;
    }
    
    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
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
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public CommodityStatus getCommodityStatus() {
        return commodityStatus;
    }
    
    public void setCommodityStatus(CommodityStatus commodityStatus) {
        this.commodityStatus = commodityStatus;
    }
    
    public String getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Commodity{" +
                "commodityId='" + commodityId + '\'' +
                ", commodityName='" + commodityName + '\'' +
                ", commodityDescription='" + commodityDescription + '\'' +
                ", categoryId=" + categoryId +
                ", currentPrice=" + currentPrice +
                ", commodityStatus=" + commodityStatus +
                ", sellerId='" + sellerId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}