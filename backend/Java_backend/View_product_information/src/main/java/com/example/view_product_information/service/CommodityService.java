package com.example.view_product_information.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.view_product_information.dto.CommodityDetailDTO;
import com.example.view_product_information.entity.Commodity;

/**
 * 商品服务接口
 */
public interface CommodityService extends IService<Commodity> {

    /**
     * 根据商品ID获取商品详情
     *
     * @param commodityId 商品ID
     * @return 商品详情
     */
    CommodityDetailDTO getCommodityDetail(String commodityId);
}