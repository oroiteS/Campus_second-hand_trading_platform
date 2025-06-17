package com.campus.view_latest.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 * 对应数据库表：commodities
 */
@Data
@TableName("commodities")
public class Commodity {
    
    /**
     * 商品唯一标识符（UUIDv7）
     */
    @TableId(value = "commodity_id", type = IdType.INPUT)
    private String commodityId;
    
    /**
     * 商品标题（如"苹果iPhone 13"）
     */
    @TableField("commodity_name")
    private String commodityName;
    
    /**
     * 详细描述（颜色、瑕疵等）
     */
    @TableField("commodity_description")
    private String commodityDescription;
    
    /**
     * 关联类别表的外键
     */
    @TableField("category_id")
    private Integer categoryId;
    
    /**
     * 存储标签ID数组（如[1,2]）
     */
    @TableField("tags_Id")
    private String tagsId;
    
    /**
     * 商品售价（如3500.00）
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
     * 多图链接数组（可选）
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
}