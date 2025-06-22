package com.campus.ordermanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.ordermanagement.pojo.Commodity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;
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

    // 移除updateQuantityAndStatus方法，状态更新现在由数据库触发器处理

    /**
     * 减少商品库存（原子操作）
     * @param commodityId 商品ID
     * @param decreaseAmount 减少的数量
     * @return 更新的行数
     */
    @Update("UPDATE commodities SET quantity = quantity - #{decreaseAmount}, updated_at = CURRENT_TIMESTAMP WHERE commodity_id = #{commodityId} AND quantity >= #{decreaseAmount}")
    int decreaseQuantity(@Param("commodityId") String commodityId, @Param("decreaseAmount") Integer decreaseAmount);

    // 移除decreaseQuantityAndUpdateStatus方法，状态更新现在由数据库触发器处理

    /**
     * 根据商品ID查询商品名称和主图URL
     * @param commodityId 商品ID
     * @return 商品信息（只包含名称和主图URL）
     */
    @Select("SELECT commodity_name, main_image_url FROM commodities WHERE commodity_id = #{commodityId}")
    Commodity selectNameAndImageById(@Param("commodityId") String commodityId);
}