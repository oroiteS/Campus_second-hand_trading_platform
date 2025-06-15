package com.example.view_product_information.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品详情DTO
 */
@Data
@Accessors(chain = true)
public class CommodityDetailDTO {

    /**
     * 商品唯一标识符
     */
    private String commodityId;

    /**
     * 商品标题
     */
    private String commodityName;

    /**
     * 详细描述
     */
    private String commodityDescription;

    /**
     * 类别ID
     */
    private Integer categoryId;

    /**
     * 类别名称
     */
    private String categoryName;

    /**
     * 商品标签
     */
    private String tags;

    /**
     * 商品价格
     */
    private BigDecimal currentPrice;

    /**
     * 商品状态
     */
    private String commodityStatus;

    /**
     * 卖家ID
     */
    private String sellerId;

    /**
     * 商品图片链接
     */
    private String mainImageUrl;

    /**
     * 多图链接数组
     */
    private List<String> imageList;

    /**
     * 商品发布时间
     */
    private LocalDateTime createdAt;

    /**
     * 信息更新时间
     */
    private LocalDateTime updatedAt;
}