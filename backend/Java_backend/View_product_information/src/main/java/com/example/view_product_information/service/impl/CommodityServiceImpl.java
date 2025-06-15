package com.example.view_product_information.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.view_product_information.dto.CommodityDetailDTO;
import com.example.view_product_information.entity.Commodity;
import com.example.view_product_information.mapper.CommodityMapper;
import com.example.view_product_information.service.CommodityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 商品服务实现类
 */
@Slf4j
@Service
public class CommodityServiceImpl extends ServiceImpl<CommodityMapper, Commodity> implements CommodityService {

    @Override
    public CommodityDetailDTO getCommodityDetail(String commodityId) {
        log.info("查询商品详情，商品ID: {}", commodityId);
        
        if (commodityId == null || commodityId.trim().isEmpty()) {
            log.warn("商品ID为空");
            return null;
        }
        
        CommodityDetailDTO commodityDetail = baseMapper.selectCommodityDetailById(commodityId);
        
        if (commodityDetail == null) {
            log.warn("未找到商品，商品ID: {}", commodityId);
            return null;
        }
        
        log.info("成功查询到商品详情，商品名称: {}", commodityDetail.getCommodityName());
        return commodityDetail;
    }
}