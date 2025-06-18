package com.campus.ordermanagement.pojo;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类（用于订单模块）
 * 对应数据库中的commodities表
 */
@TableName("commodities")
public class Commodity {

    /**
     * 商品唯一标识符（UUIDv7）
     */
    @TableId(value = "commodity_id", type = IdType.INPUT)
    private String commodityId;

    /**
     * 商品标题
     */
    @TableField("commodity_name")
    private String commodityName;

    /**
     * 详细描述
     */
    @TableField("commodity_description")
    private String commodityDescription;

    /**
     * 关联类别表的外键
     */
    @TableField("category_id")
    private Integer categoryId;

    /**
     * 存储标签ID数组
     */
    @TableField("tags_Id")
    private String tagsId;

    /**
     * 商品售价
     */
    @TableField("current_price")
    private BigDecimal currentPrice;

    /**
     * 商品状态：在售/已售/下架
     */
    @TableField("commodity_status")
    private String commodityStatus;

    /**
     * 关联用户表的外键
     */
    @TableField("seller_id")
    private String sellerId;

    /**
     * 商品主图链接
     */
    @TableField("main_image_url")
    private String mainImageUrl;

    /**
     * 多图链接数组
     */
    @TableField("image_list")
    private String imageList;

    /**
     * 商品发布时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 信息更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 商品数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 商品新旧度
     */
    @TableField("newness")
    private String newness;

    // 商品状态常量
    public static final String STATUS_ON_SALE = "on_sale";
    public static final String STATUS_SOLD = "sold";
    public static final String STATUS_OFF_SALE = "off_sale";

    // 构造函数
    public Commodity() {}

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

    /**
     * 检查商品是否在售
     */
    public boolean isOnSale() {
        return STATUS_ON_SALE.equals(this.commodityStatus);
    }

    /**
     * 检查商品是否已售出
     */
    public boolean isSold() {
        return STATUS_SOLD.equals(this.commodityStatus);
    }

    /**
     * 检查商品是否下架
     */
    public boolean isOffSale() {
        return STATUS_OFF_SALE.equals(this.commodityStatus);
    }
}