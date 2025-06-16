package com.campus.product_management_seller.repository;

import com.campus.product_management_seller.entity.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommodityRepository extends JpaRepository<Commodity, String> {
    
    /**
     * 根据卖家ID查找商品列表
     * @param sellerId 卖家ID
     * @return 商品列表
     */
    List<Commodity> findBySellerIdOrderByCreatedAtDesc(String sellerId);
    
    /**
     * 根据卖家ID和商品状态查找商品列表
     * @param sellerId 卖家ID
     * @param status 商品状态
     * @return 商品列表
     */
    List<Commodity> findBySellerIdAndCommodityStatusOrderByCreatedAtDesc(String sellerId, Commodity.CommodityStatus status);
    
    /**
     * 根据商品ID和卖家ID查找商品（确保只能操作自己的商品）
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @return 商品信息
     */
    Optional<Commodity> findByCommodityIdAndSellerId(String commodityId, String sellerId);
    
    /**
     * 更新商品状态
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @param status 新状态
     * @return 更新的行数
     */
    @Modifying
    @Transactional
    @Query("UPDATE Commodity c SET c.commodityStatus = :status WHERE c.commodityId = :commodityId AND c.sellerId = :sellerId")
    int updateCommodityStatus(@Param("commodityId") String commodityId, 
                             @Param("sellerId") String sellerId, 
                             @Param("status") Commodity.CommodityStatus status);
    
    /**
     * 更新商品描述
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @param description 新描述
     * @return 更新的行数
     */
    @Modifying
    @Transactional
    @Query("UPDATE Commodity c SET c.commodityDescription = :description WHERE c.commodityId = :commodityId AND c.sellerId = :sellerId")
    int updateCommodityDescription(@Param("commodityId") String commodityId, 
                                  @Param("sellerId") String sellerId, 
                                  @Param("description") String description);
    
    /**
     * 检查商品是否属于指定卖家
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @return 是否存在
     */
    boolean existsByCommodityIdAndSellerId(String commodityId, String sellerId);
}