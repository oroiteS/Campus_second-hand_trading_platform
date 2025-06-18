package com.campus.ordermanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.ordermanagement.pojo.Commodity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

/**
 * 商品数据访问层
 */
@Mapper
public interface CommodityRepository extends BaseMapper<Commodity> {

    /**
     * 更新商品库存
     * @param commodityId 商品ID
     * @param quantity 新的库存数量
     * @return 更新的行数
     */
    @Update("UPDATE commodities SET quantity = #{quantity}, updated_at = CURRENT_TIMESTAMP WHERE commodity_id = #{commodityId}")
    int updateQuantity(@Param("commodityId") String commodityId, @Param("quantity") Integer quantity);

    /**
     * 更新商品状态和库存
     * @param commodityId 商品ID
     * @param quantity 新的库存数量
     * @param status 新的商品状态
     * @return 更新的行数
     */
    @Update("UPDATE commodities SET quantity = #{quantity}, commodity_status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE commodity_id = #{commodityId}")
    int updateQuantityAndStatus(@Param("commodityId") String commodityId, 
                               @Param("quantity") Integer quantity, 
                               @Param("status") String status);

    /**
     * 减少商品库存（原子操作）
     * @param commodityId 商品ID
     * @param decreaseAmount 减少的数量
     * @return 更新的行数
     */
    @Update("UPDATE commodities SET quantity = quantity - #{decreaseAmount}, updated_at = CURRENT_TIMESTAMP WHERE commodity_id = #{commodityId} AND quantity >= #{decreaseAmount}")
    int decreaseQuantity(@Param("commodityId") String commodityId, @Param("decreaseAmount") Integer decreaseAmount);

    /**
     * 减少库存并在库存为0时更新状态为已售出
     * @param commodityId 商品ID
     * @param decreaseAmount 减少的数量
     * @return 更新的行数
     */
    @Update("UPDATE commodities SET quantity = quantity - #{decreaseAmount}, commodity_status = CASE WHEN (quantity - #{decreaseAmount}) = 0 THEN 'sold' ELSE commodity_status END, updated_at = CURRENT_TIMESTAMP WHERE commodity_id = #{commodityId} AND quantity >= #{decreaseAmount}")
    int decreaseQuantityAndUpdateStatus(@Param("commodityId") String commodityId, @Param("decreaseAmount") Integer decreaseAmount);
}