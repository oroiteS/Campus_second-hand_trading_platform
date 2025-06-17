package com.campus.view_product_information.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("commodities")
public class Commodity {

    /**
     * 商品唯一标识符（UUIDv7）
     */
    @TableId("commodity_id")
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
     * 存储标签ID数组（如[1,2,3]）
     */
    @TableField("tags_Id")
    private String tagsId;

    /**
     * 商品售价（如3500.00）
     */
    @TableField("current_price")
    private BigDecimal currentPrice;

    /**
     * 商品状态：on_sale=在售/sold=已售/off_sale=下架
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
    @TableField(value = "image_list", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private List<String> imageList;

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
     * 商品类别名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String categoryName;
}