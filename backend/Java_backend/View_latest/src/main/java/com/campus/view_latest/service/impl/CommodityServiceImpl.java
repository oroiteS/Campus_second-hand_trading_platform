package com.campus.view_latest.service.impl;

import com.campus.view_latest.entity.Commodity;
import com.campus.view_latest.mapper.CommodityMapper;
import com.campus.view_latest.service.CommodityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommodityServiceImpl implements CommodityService {
    
    private final CommodityMapper commodityMapper;
    
    @Override
    public List<Commodity> getLatestOnSaleCommodities() {
        log.info("开始获取最新6个在售商品的所有字段信息");
        try {
            List<Commodity> commodities = commodityMapper.selectLatestOnSaleCommodities();
            log.info("成功获取{}个在售商品，包含所有字段信息", commodities.size());
            
            // 记录获取到的商品详细信息
            if (!commodities.isEmpty()) {
                log.debug("获取到的商品列表：");
                commodities.forEach(commodity -> {
                    log.debug("商品ID: {}, 商品名称: {}, 价格: {}, 状态: {}, 创建时间: {}", 
                        commodity.getCommodityId(), 
                        commodity.getCommodityName(), 
                        commodity.getCurrentPrice(), 
                        commodity.getCommodityStatus(), 
                        commodity.getCreatedAt());
                });
            }
            
            return commodities;
        } catch (Exception e) {
            log.error("获取最新在售商品失败", e);
            throw new RuntimeException("获取商品信息失败: " + e.getMessage(), e);
        }
    }
}