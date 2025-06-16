package com.campus.product_management_seller.service;

import com.campus.product_management_seller.repository.CommodityRepository;
import com.campus.product_management_seller.entity.Commodity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommodityService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommodityService.class);
    
    @Autowired
    private CommodityRepository commodityRepository;
    
    /**
     * 上架商品
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @return 是否成功
     */
    public boolean putOnSale(String commodityId, String sellerId) {
        logger.info("尝试上架商品: commodityId={}, sellerId={}", commodityId, sellerId);
        
        try {
            int updatedRows = commodityRepository.updateCommodityStatus(
                commodityId, sellerId, Commodity.CommodityStatus.ON_SALE);
            
            if (updatedRows > 0) {
                logger.info("商品上架成功: commodityId={}, sellerId={}", commodityId, sellerId);
                return true;
            } else {
                logger.warn("商品上架失败，未找到对应商品: commodityId={}, sellerId={}", commodityId, sellerId);
                return false;
            }
        } catch (Exception e) {
            logger.error("商品上架异常: commodityId={}, sellerId={}, error={}", commodityId, sellerId, e.getMessage(), e);
            throw new RuntimeException("商品上架失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 下架商品
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @return 是否成功
     */
    public boolean putOffSale(String commodityId, String sellerId) {
        logger.info("尝试下架商品: commodityId={}, sellerId={}", commodityId, sellerId);
        
        try {
            int updatedRows = commodityRepository.updateCommodityStatus(
                commodityId, sellerId, Commodity.CommodityStatus.OFF_SALE);
            
            if (updatedRows > 0) {
                logger.info("商品下架成功: commodityId={}, sellerId={}", commodityId, sellerId);
                return true;
            } else {
                logger.warn("商品下架失败，未找到对应商品: commodityId={}, sellerId={}", commodityId, sellerId);
                return false;
            }
        } catch (Exception e) {
            logger.error("商品下架异常: commodityId={}, sellerId={}, error={}", commodityId, sellerId, e.getMessage(), e);
            throw new RuntimeException("商品下架失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 修改商品描述
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @param description 新描述
     * @return 是否成功
     */
    public boolean updateDescription(String commodityId, String sellerId, String description) {
        logger.info("尝试修改商品描述: commodityId={}, sellerId={}, description={}", 
                   commodityId, sellerId, description);
        
        try {
            int updatedRows = commodityRepository.updateCommodityDescription(
                commodityId, sellerId, description);
            
            if (updatedRows > 0) {
                logger.info("商品描述修改成功: commodityId={}, sellerId={}", commodityId, sellerId);
                return true;
            } else {
                logger.warn("商品描述修改失败，未找到对应商品: commodityId={}, sellerId={}", commodityId, sellerId);
                return false;
            }
        } catch (Exception e) {
            logger.error("商品描述修改异常: commodityId={}, sellerId={}, error={}", 
                        commodityId, sellerId, e.getMessage(), e);
            throw new RuntimeException("商品描述修改失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取卖家的所有商品
     * @param sellerId 卖家ID
     * @return 商品列表
     */
    @Transactional(readOnly = true)
    public List<Commodity> getCommoditiesBySeller(String sellerId) {
        logger.info("获取卖家商品列表: sellerId={}", sellerId);
        
        try {
            List<Commodity> commodities = commodityRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);
            logger.info("获取到{}个商品: sellerId={}", commodities.size(), sellerId);
            return commodities;
        } catch (Exception e) {
            logger.error("获取卖家商品列表异常: sellerId={}, error={}", sellerId, e.getMessage(), e);
            throw new RuntimeException("获取商品列表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据状态获取卖家的商品
     * @param sellerId 卖家ID
     * @param status 商品状态
     * @return 商品列表
     */
    @Transactional(readOnly = true)
    public List<Commodity> getCommoditiesBySellerAndStatus(String sellerId, Commodity.CommodityStatus status) {
        logger.info("根据状态获取卖家商品列表: sellerId={}, status={}", sellerId, status);
        
        try {
            List<Commodity> commodities = commodityRepository.findBySellerIdAndCommodityStatusOrderByCreatedAtDesc(sellerId, status);
            logger.info("获取到{}个商品: sellerId={}, status={}", commodities.size(), sellerId, status);
            return commodities;
        } catch (Exception e) {
            logger.error("根据状态获取卖家商品列表异常: sellerId={}, status={}, error={}", 
                        sellerId, status, e.getMessage(), e);
            throw new RuntimeException("获取商品列表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取商品详情
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @return 商品信息
     */
    @Transactional(readOnly = true)
    public Optional<Commodity> getCommodityDetail(String commodityId, String sellerId) {
        logger.info("获取商品详情: commodityId={}, sellerId={}", commodityId, sellerId);
        
        try {
            Optional<Commodity> commodity = commodityRepository.findByCommodityIdAndSellerId(commodityId, sellerId);
            if (commodity.isPresent()) {
                logger.info("获取商品详情成功: commodityId={}, sellerId={}", commodityId, sellerId);
            } else {
                logger.warn("未找到商品: commodityId={}, sellerId={}", commodityId, sellerId);
            }
            return commodity;
        } catch (Exception e) {
            logger.error("获取商品详情异常: commodityId={}, sellerId={}, error={}", 
                        commodityId, sellerId, e.getMessage(), e);
            throw new RuntimeException("获取商品详情失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检查商品是否属于指定卖家
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @return 是否属于
     */
    @Transactional(readOnly = true)
    public boolean isCommodityOwnedBySeller(String commodityId, String sellerId) {
        logger.debug("检查商品归属: commodityId={}, sellerId={}", commodityId, sellerId);
        
        try {
            boolean exists = commodityRepository.existsByCommodityIdAndSellerId(commodityId, sellerId);
            logger.debug("商品归属检查结果: commodityId={}, sellerId={}, exists={}", 
                        commodityId, sellerId, exists);
            return exists;
        } catch (Exception e) {
            logger.error("检查商品归属异常: commodityId={}, sellerId={}, error={}", 
                        commodityId, sellerId, e.getMessage(), e);
            throw new RuntimeException("检查商品归属失败: " + e.getMessage(), e);
        }
    }
}