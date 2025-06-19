package com.campus.ordermanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.ordermanagement.pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单数据访问层接口
 * 继承BaseMapper提供基本的CRUD操作
 */
@Mapper
public interface OrderRepository extends BaseMapper<Order> {

    /**
     * 根据买家ID查找订单列表
     * 
     * @param buyerId 买家ID
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE Buyer_id = #{buyerId} ORDER BY Sale_time DESC")
    List<Order> findByBuyerId(@Param("buyerId") String buyerId);

    /**
     * 根据卖家ID查找订单列表
     * 
     * @param sellerId 卖家ID
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE Seller_id = #{sellerId} ORDER BY Sale_time DESC")
    List<Order> findBySellerId(@Param("sellerId") String sellerId);

    /**
     * 根据商品ID查找订单列表
     * 
     * @param commodityId 商品ID
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE commodity_id = #{commodityId} ORDER BY Sale_time DESC")
    List<Order> findByCommodityId(@Param("commodityId") String commodityId);

    /**
     * 根据订单状态查找订单列表
     * 
     * @param orderStatus 订单状态
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE order_status = #{orderStatus} ORDER BY Sale_time DESC")
    List<Order> findByOrderStatus(@Param("orderStatus") String orderStatus);

    /**
     * 根据买家ID和订单状态查找订单列表
     * 
     * @param buyerId 买家ID
     * @param orderStatus 订单状态
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE Buyer_id = #{buyerId} AND order_status = #{orderStatus} ORDER BY Sale_time DESC")
    List<Order> findByBuyerIdAndOrderStatus(@Param("buyerId") String buyerId, 
                                           @Param("orderStatus") String orderStatus);

    /**
     * 根据卖家ID和订单状态查找订单列表
     * 
     * @param sellerId 卖家ID
     * @param orderStatus 订单状态
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE Seller_id = #{sellerId} AND order_status = #{orderStatus} ORDER BY Sale_time DESC")
    List<Order> findBySellerIdAndOrderStatus(@Param("sellerId") String sellerId, 
                                            @Param("orderStatus") String orderStatus);

    /**
     * 根据用户ID查找用户参与的所有订单（作为买家或卖家）
     * 
     * @param userId 用户ID
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE Buyer_id = #{userId} OR Seller_id = #{userId} ORDER BY Sale_time DESC")
    List<Order> findByUserId(@Param("userId") String userId);

    /**
     * 根据时间范围查找订单列表
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE Sale_time BETWEEN #{startTime} AND #{endTime} ORDER BY Sale_time DESC")
    List<Order> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                               @Param("endTime") LocalDateTime endTime);

    /**
     * 分页查询所有订单
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 订单列表
     */
    @Select("SELECT * FROM orders ORDER BY Sale_time DESC LIMIT #{limit} OFFSET #{offset}")
    List<Order> findAllOrdersPaged(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计订单总数
     * 
     * @return 订单总数
     */
    @Select("SELECT COUNT(*) FROM orders")
    long countAllOrders();

    /**
     * 根据买家ID和时间范围查找订单列表
     * 
     * @param buyerId 买家ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE Buyer_id = #{buyerId} AND Sale_time BETWEEN #{startTime} AND #{endTime} ORDER BY Sale_time DESC")
    List<Order> findByBuyerIdAndTimeRange(@Param("buyerId") String buyerId,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 根据用户ID查找订单列表（买家或卖家）
     * 查询该用户作为买家或卖家的所有订单
     * 
     * @param userId 用户ID
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE Buyer_id = #{userId} OR Seller_id = #{userId} ORDER BY Sale_time DESC")
    List<Order> findByBuyerIdOrSellerId(@Param("userId") String userId);

    /**
     * 根据卖家ID和时间范围查找订单列表
     * 
     * @param sellerId 卖家ID
     * @param startTime 开始时间 
                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 根据卖家ID和时间范围查找订单列表
     * 
     * @param sellerId 卖家ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE Seller_id = #{sellerId} AND Sale_time BETWEEN #{startTime} AND #{endTime} ORDER BY Sale_time DESC")
    List<Order> findBySellerIdAndTimeRange(@Param("sellerId") String sellerId,
                                          @Param("startTime") LocalDateTime startTime, 
                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 根据金额范围查找订单列表
     * 
     * @param minMoney 最小金额
     * @param maxMoney 最大金额
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE Money BETWEEN #{minMoney} AND #{maxMoney} ORDER BY Money DESC")
    List<Order> findByMoneyRange(@Param("minMoney") BigDecimal minMoney, 
                                @Param("maxMoney") BigDecimal maxMoney);

    /**
     * 查找待付款订单
     * 
     * @return 待付款订单列表
     */
    @Select("SELECT * FROM orders WHERE order_status = 'pending_payment' ORDER BY Sale_time DESC")
    List<Order> findPendingPaymentOrders();

    /**
     * 查找待交易订单
     * 
     * @return 待交易订单列表
     */
    @Select("SELECT * FROM orders WHERE order_status = 'pending_transaction' ORDER BY Sale_time DESC")
    List<Order> findPendingTransactionOrders();

    /**
     * 查找已完成订单
     * 
     * @return 已完成订单列表
     */
    @Select("SELECT * FROM orders WHERE order_status = 'completed' ORDER BY Sale_time DESC")
    List<Order> findCompletedOrders();

    /**
     * 统计买家的订单总数
     * 
     * @param buyerId 买家ID
     * @return 订单总数
     */
    @Select("SELECT COUNT(*) FROM orders WHERE Buyer_id = #{buyerId}")
    long countByBuyerId(@Param("buyerId") String buyerId);

    /**
     * 统计卖家的订单总数
     * 
     * @param sellerId 卖家ID
     * @return 订单总数
     */
    @Select("SELECT COUNT(*) FROM orders WHERE Seller_id = #{sellerId}")
    long countBySellerId(@Param("sellerId") String sellerId);

    /**
     * 统计指定状态的订单总数
     * 
     * @param orderStatus 订单状态
     * @return 订单总数
     */
    @Select("SELECT COUNT(*) FROM orders WHERE order_status = #{orderStatus}")
    long countByOrderStatus(@Param("orderStatus") String orderStatus);

    /**
     * 计算买家的订单总金额
     * 
     * @param buyerId 买家ID
     * @return 订单总金额
     */
    @Select("SELECT COALESCE(SUM(Money), 0) FROM orders WHERE Buyer_id = #{buyerId} AND order_status = 'completed'")
    BigDecimal sumMoneyByBuyerId(@Param("buyerId") String buyerId);

    /**
     * 计算卖家的订单总金额
     * 
     * @param sellerId 卖家ID
     * @return 订单总金额
     */
    @Select("SELECT COALESCE(SUM(Money), 0) FROM orders WHERE Seller_id = #{sellerId} AND order_status = 'completed'")
    BigDecimal sumMoneyBySellerId(@Param("sellerId") String sellerId);

    /**
     * 计算指定时间范围内的订单总金额
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单总金额
     */
    @Select("SELECT COALESCE(SUM(Money), 0) FROM orders WHERE Sale_time BETWEEN #{startTime} AND #{endTime} AND order_status = 'completed'")
    BigDecimal sumMoneyByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 更新订单状态
     * 
     * @param orderId 订单ID
     * @param orderStatus 新的订单状态
     * @return 影响的行数
     */
    @Update("UPDATE orders SET order_status = #{orderStatus} WHERE order_id = #{orderId}")
    int updateOrderStatus(@Param("orderId") String orderId, 
                         @Param("orderStatus") String orderStatus);

    /**
     * 更新订单交易时间
     * 
     * @param orderId 订单ID
     * @param saleTime 交易时间
     * @return 影响的行数
     */
    @Update("UPDATE orders SET Sale_time = #{saleTime} WHERE order_id = #{orderId}")
    int updateSaleTime(@Param("orderId") String orderId, 
                      @Param("saleTime") LocalDateTime saleTime);

    /**
     * 更新订单金额
     * 
     * @param orderId 订单ID
     * @param money 新的金额
     * @return 影响的行数
     */
    @Update("UPDATE orders SET Money = #{money} WHERE order_id = #{orderId}")
    int updateMoney(@Param("orderId") String orderId, 
                   @Param("money") BigDecimal money);

    /**
     * 更新订单交易地址
     * 
     * @param orderId 订单ID
     * @param saleLocation 新的交易地址
     * @return 影响的行数
     */
    @Update("UPDATE orders SET Sale_loc = #{saleLocation} WHERE order_id = #{orderId}")
    int updateSaleLocation(@Param("orderId") String orderId, 
                          @Param("saleLocation") String saleLocation);

    /**
     * 完成订单（更新状态为已完成并设置交易时间）
     * 
     * @param orderId 订单ID
     * @param saleTime 交易时间
     * @return 影响的行数
     */
    @Update("UPDATE orders SET order_status = 'completed', Sale_time = #{saleTime} WHERE order_id = #{orderId}")
    int completeOrder(@Param("orderId") String orderId, 
                     @Param("saleTime") LocalDateTime saleTime);

    /**
     * 检查订单是否存在
     * 
     * @param orderId 订单ID
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM orders WHERE order_id = #{orderId}")
    boolean existsByOrderId(@Param("orderId") String orderId);

    /**
     * 检查买家是否有指定商品的订单
     * 
     * @param buyerId 买家ID
     * @param commodityId 商品ID
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM orders WHERE Buyer_id = #{buyerId} AND commodity_id = #{commodityId}")
    boolean existsByBuyerIdAndCommodityId(@Param("buyerId") String buyerId, 
                                         @Param("commodityId") String commodityId);
}