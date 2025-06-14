package com.campus.wallet.dao;

import com.campus.wallet.pojo.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    // 根据订单ID查询订单信息
    Order findByOrder_id(String orderId);
}