package com.campus.product_management_seller.entity;

import com.campus.product_management_seller.converter.CommodityStatusConverter;
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
    
    @Column(name = "tags_Id", columnDefinition = "JSON")
    private String tagsId;
    
    @NotNull(message = "商品价格不能为空")
    @Positive(message = "商品价格必须大于0")
    @Column(name = "current_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal currentPrice;
    
    @Convert(converter = CommodityStatusConverter.class)
    @Column(name = "commodity_status", nullable = false)
    private CommodityStatus commodityStatus = CommodityStatus.TO_SALE;
    
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
    
    @NotNull(message = "商品数量不能为空")
    @Positive(message = "商品数量必须大于0")
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
    
    @NotBlank(message = "商品新旧度不能为空")
    @Column(name = "newness", length = 50, nullable = false)
    private String newness;
    
    // 商品状态枚举
    public enum CommodityStatus {
        TO_SALE("to_sale"),
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
                    Integer categoryId, BigDecimal currentPrice, String sellerId, String newness) {
        this.commodityId = commodityId;
        this.commodityName = commodityName;
        this.commodityDescription = commodityDescription;
        this.categoryId = categoryId;
        this.currentPrice = currentPrice;
        this.sellerId = sellerId;
        this.newness = newness;
        this.quantity = 1;
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
    
    public String getTagsId() {
        return tagsId;
    }
    
    public void setTagsId(String tagsId) {
        this.tagsId = tagsId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
    
    public String getNewness() {
        return newness;
    }
    
    public void setNewness(String newness) {
        this.newness = newness;
    }
    
    @Override
    public String toString() {
        return "Commodity{" +
                "commodityId='" + commodityId + '\'' +
                ", commodityName='" + commodityName + '\'' +
                ", commodityDescription='" + commodityDescription + '\'' +
                ", categoryId=" + categoryId +
                ", tagsId='" + tagsId + '\'' +
                ", currentPrice=" + currentPrice +
                ", commodityStatus=" + commodityStatus +
                ", sellerId='" + sellerId + '\'' +
                ", mainImageUrl='" + mainImageUrl + '\'' +
                ", imageList='" + imageList + '\'' +
                ", quantity=" + quantity +
                ", newness='" + newness + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}