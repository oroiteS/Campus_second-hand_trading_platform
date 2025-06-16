package com.campus.view_latest.service;

import com.campus.view_latest.entity.Commodity;

import java.util.List;

/**
 * 商品服务接口
 */
public interface CommodityService {
    
    /**
     * 获取最新6个在售商品
     * @return 商品列表
     */
    List<Commodity> getLatestOnSaleCommodities();
}