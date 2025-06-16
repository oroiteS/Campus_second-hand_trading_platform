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
 */
@Data
@TableName("commodities")
public class Commodity {
    
    @TableId(value = "commodity_id", type = IdType.INPUT)
    private String commodityId;
    
    @TableField("commodity_name")
    private String commodityName;
    
    @TableField("commodity_description")
    private String commodityDescription;
    
    @TableField("commodity_price")
    private BigDecimal commodityPrice;
    
    @TableField("commodity_status")
    private String commodityStatus;
    
    @TableField("commodity_category")
    private String commodityCategory;
    
    @TableField("commodity_location")
    private String commodityLocation;
    
    @TableField("seller_id")
    private String sellerId;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
    @TableField("commodity_image_url")
    private String commodityImageUrl;
}