package com.campus.productquery.dto;

import com.campus.productquery.pojo.Commodity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品响应DTO
 */
public class CommodityResponse {

    /**
     * 商品唯一标识符
     */
    @JsonProperty("commodityId")
    private String commodityId;

    /**
     * 商品标题
     */
    @JsonProperty("commodityName")
    private String commodityName;

    /**
     * 详细描述
     */
    @JsonProperty("commodityDescription")
    private String commodityDescription;

    /**
     * 关联类别表的外键
     */
    @JsonProperty("categoryId")
    private Integer categoryId;

    /**
     * 存储标签ID数组
     */
    @JsonProperty("tagsId")
    private String tagsId;

    /**
     * 商品售价
     */
    @JsonProperty("currentPrice")
    private BigDecimal currentPrice;

    /**
     * 商品状态
     */
    @JsonProperty("commodityStatus")
    private String commodityStatus;

    /**
     * 商品状态描述
     */
    @JsonProperty("commodityStatusDescription")
    private String commodityStatusDescription;

    /**
     * 关联用户表的外键
     */
    @JsonProperty("sellerId")
    private String sellerId;

    /**
     * 商品主图链接
     */
    @JsonProperty("mainImageUrl")
    private String mainImageUrl;

    /**
     * 多图链接数组
     */
    @JsonProperty("imageList")
    private String imageList;

    /**
     * 商品发布时间
     */
    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 信息更新时间
     */
    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 商品数量
     */
    @JsonProperty("quantity")
    private Integer quantity;

    /**
     * 商品新旧度
     */
    @JsonProperty("newness")
    private String newness;

    // 构造函数
    public CommodityResponse() {}

    public CommodityResponse(Commodity commodity) {
        this.commodityId = commodity.getCommodityId();
        this.commodityName = commodity.getCommodityName();
        this.commodityDescription = commodity.getCommodityDescription();
        this.categoryId = commodity.getCategoryId();
        this.tagsId = commodity.getTagsId();
        this.currentPrice = commodity.getCurrentPrice();
        this.commodityStatus = commodity.getCommodityStatus();
        this.commodityStatusDescription = getStatusDescription(commodity.getCommodityStatus());
        this.sellerId = commodity.getSellerId();
        this.mainImageUrl = commodity.getMainImageUrl();
        this.imageList = commodity.getImageList();
        this.createdAt = commodity.getCreatedAt();
        this.updatedAt = commodity.getUpdatedAt();
        this.quantity = commodity.getQuantity();
        this.newness = commodity.getNewness();
    }

    /**
     * 获取状态描述
     */
    private String getStatusDescription(String status) {
        if (status == null) {
            return "未知状态";
        }
        switch (status) {
            case Commodity.STATUS_ON_SALE:
                return "在售";
            case Commodity.STATUS_SOLD:
                return "已售";
            case Commodity.STATUS_OFF_SALE:
                return "下架";
            default:
                return "未知状态";
        }
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

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getCommodityStatus() {
        return commodityStatus;
    }

    public void setCommodityStatus(String commodityStatus) {
        this.commodityStatus = commodityStatus;
    }

    public String getCommodityStatusDescription() {
        return commodityStatusDescription;
    }

    public void setCommodityStatusDescription(String commodityStatusDescription) {
        this.commodityStatusDescription = commodityStatusDescription;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getNewness() {
        return newness;
    }

    public void setNewness(String newness) {
        this.newness = newness;
    }

    @Override
    public String toString() {
        return "CommodityResponse{" +
                "commodityId='" + commodityId + '\'' +
                ", commodityName='" + commodityName + '\'' +
                ", commodityDescription='" + commodityDescription + '\'' +
                ", categoryId=" + categoryId +
                ", tagsId='" + tagsId + '\'' +
                ", currentPrice=" + currentPrice +
                ", commodityStatus='" + commodityStatus + '\'' +
                ", commodityStatusDescription='" + commodityStatusDescription + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", mainImageUrl='" + mainImageUrl + '\'' +
                ", imageList='" + imageList + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", quantity=" + quantity +
                ", newness='" + newness + '\'' +
                '}';
    }
}