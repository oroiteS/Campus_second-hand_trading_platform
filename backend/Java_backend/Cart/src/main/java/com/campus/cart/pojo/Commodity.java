package com.campus.cart.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Commodity {
    private String commodityId;
    private String commodityName;
    private String commodityDescription;
    private Integer categoryId;
    private BigDecimal currentPrice;
    private String commodityStatus;
    private String sellerId;
    private String mainImageUrl;
    private String imageList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer quantity;
}
