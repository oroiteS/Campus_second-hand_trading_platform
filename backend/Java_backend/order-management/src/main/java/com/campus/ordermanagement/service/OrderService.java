package com.campus.ordermanagement.service;

import com.campus.ordermanagement.pojo.Order;
import com.campus.ordermanagement.dto.CreateOrderRequest;
import com.campus.ordermanagement.dto.OrderResponse;
import com.campus.ordermanagement.dto.UpdateOrderStatusRequest;
import com.campus.ordermanagement.dto.QueryAllOrdersRequest;
import com.campus.ordermanagement.dto.PagedOrderResponse;

import java.util.List;
import java.util.Optional;

/**
 * 订单服务接口
 * 定义订单相关的业务逻辑方法
 */
public interface OrderService {

    /**
     * 创建新订单
     * 
     * @param request 创建订单请求
     * @return 创建的订单信息
     */
    OrderResponse createOrder(CreateOrderRequest request);

    /**
     * 根据订单ID查询订单
     * 
     * @param orderId 订单ID
     * @return 订单信息
     */
    Optional<OrderResponse> getOrderById(String orderId);

    /**
     * 根据买家ID查询订单列表
     * 
     * @param buyerId 买家ID
     * @return 订单列表
     */
    List<OrderResponse> getOrdersByBuyerId(String buyerId);

    /**
     * 根据卖家ID查询订单列表
     * 
     * @param sellerId 卖家ID
     * @return 订单列表
     */
    List<OrderResponse> getOrdersBySellerId(String sellerId);

    /**
     * 根据商品ID查询订单列表
     * 
     * @param commodityId 商品ID
     * @return 订单列表
     */
    List<OrderResponse> getOrdersByCommodityId(String commodityId);

    /**
     * 根据订单状态查询订单列表
     * 
     * @param status 订单状态
     * @return 订单列表
     */
    List<OrderResponse> getOrdersByStatus(Order.OrderStatus status);

    /**
     * 根据用户ID查询用户参与的所有订单
     * 查询用户作为买家或卖家参与的所有订单
     * 
     * @param userId 用户ID
     * @return 订单列表
     */
    List<OrderResponse> getOrdersByUserId(String userId);

    /**
     * 分页查询所有订单
     * 
     * @param request 分页查询请求
     * @return 分页查询结果
     */
    PagedOrderResponse getAllOrdersPaged(QueryAllOrdersRequest request);

    /**
     * 更新订单状态
     * 
     * @param request 更新订单状态请求
     * @return 更新后的订单信息
     */
    OrderResponse updateOrderStatus(UpdateOrderStatusRequest request);

    /**
     * 确认付款
     * 
     * @param orderId 订单ID
     * @return 更新后的订单信息
     */
    OrderResponse confirmPayment(String orderId);

    /**
     * 完成订单
     * 
     * @param orderId 订单ID
     * @return 更新后的订单信息
     */
    OrderResponse completeOrder(String orderId);

    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     * @return 是否取消成功
     */
    boolean cancelOrder(String orderId);

    /**
     * 检查订单是否存在
     * 
     * @param orderId 订单ID
     * @return 是否存在
     */
    boolean existsById(String orderId);

    /**
     * 获取用户的订单统计信息
     * 
     * @param userId 用户ID
     * @param isBuyer 是否为买家（true为买家，false为卖家）
     * @return 订单统计信息
     */
    OrderStatistics getOrderStatistics(String userId, boolean isBuyer);

    /**
     * 订单统计信息内部类
     */
    class OrderStatistics {
        private long totalOrders;
        private long pendingPaymentOrders;
        private long pendingTransactionOrders;
        private long completedOrders;

        public OrderStatistics(long totalOrders, long pendingPaymentOrders, 
                             long pendingTransactionOrders, long completedOrders) {
            this.totalOrders = totalOrders;
            this.pendingPaymentOrders = pendingPaymentOrders;
            this.pendingTransactionOrders = pendingTransactionOrders;
            this.completedOrders = completedOrders;
        }

        // Getters
        public long getTotalOrders() { return totalOrders; }
        public long getPendingPaymentOrders() { return pendingPaymentOrders; }
        public long getPendingTransactionOrders() { return pendingTransactionOrders; }
        public long getCompletedOrders() { return completedOrders; }
    }
}