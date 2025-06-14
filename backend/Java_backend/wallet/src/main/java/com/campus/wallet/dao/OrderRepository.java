package com.campus.wallet.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.wallet.pojo.Order;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@Repository
public interface OrderRepository extends BaseMapper<Order> {
    // 根据订单ID查询订单信息
    @Select("SELECT * FROM `order` WHERE order_id = #{orderId}")
    Order selectByOrderId(@Param("orderId") String orderId);
}