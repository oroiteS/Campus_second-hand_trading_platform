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
        log.info("获取最新6个在售商品");
        try {
            List<Commodity> commodities = commodityMapper.selectLatestOnSaleCommodities();
            log.info("成功获取{}个在售商品", commodities.size());
            return commodities;
        } catch (Exception e) {
            log.error("获取最新在售商品失败", e);
            throw new RuntimeException("获取商品信息失败", e);
        }
    }
}