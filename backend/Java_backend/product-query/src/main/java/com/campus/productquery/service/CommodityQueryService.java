package com.campus.productquery.service;

import com.campus.productquery.dto.CategoryQueryRequest;
import com.campus.productquery.dto.PagedCommodityResponse;

/**
 * 商品分类查询服务接口
 * 专门处理前端分类查找和筛选功能
 */
public interface CommodityQueryService {

    /**
     * 按分类查询商品（支持筛选和排序）
     * 支持价格区间、新旧度筛选和多种排序方式
     * 
     * @param request 分类查询请求（包含分类ID、价格区间、新旧度、排序方式等）
     * @return 分页商品响应
     */
    PagedCommodityResponse getCommoditiesByCategory(CategoryQueryRequest request);
}