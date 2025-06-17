package com.campus.ordermanagement.service.impl;

import com.campus.ordermanagement.dao.OrderRepository;
import com.campus.ordermanagement.dto.CreateOrderRequest;
import com.campus.ordermanagement.dto.OrderResponse;
import com.campus.ordermanagement.dto.UpdateOrderStatusRequest;
import com.campus.ordermanagement.pojo.Order;
import com.campus.ordermanagement.service.OrderService;
import com.campus.ordermanagement.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        // 参数验证
        validateCreateOrderRequest(request);
        
        // 检查买家和卖家不能是同一人
        if (request.getBuyerId().equals(request.getSellerId())) {
            throw new IllegalArgumentException("买家和卖家不能是同一人");
        }
        
        // 创建订单实体
        Order order = new Order();
        order.setOrderId(UUIDGenerator.generateUUIDv7());
        order.setCommodityId(request.getCommodityId());
        order.setBuyerId(request.getBuyerId());
        order.setSellerId(request.getSellerId());
        order.setOrderStatus(Order.OrderStatus.PENDING_PAYMENT);
        order.setMoney(request.getMoney());
        order.setSaleLocation(request.getSaleLocation());
        
        // 保存订单
        int result = orderRepository.insert(order);
        if (result <= 0) {
            throw new RuntimeException("创建订单失败");
        }
        
        return new OrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderById(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return Optional.empty();
        }
        
        Order order = orderRepository.selectById(orderId.trim());
        return order != null ? Optional.of(new OrderResponse(order)) : Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByBuyerId(String buyerId) {
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("买家ID不能为空");
        }
        
        List<Order> orders = orderRepository.findByBuyerId(buyerId.trim());
        return orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersBySellerId(String sellerId) {
        if (sellerId == null || sellerId.trim().isEmpty()) {
            throw new IllegalArgumentException("卖家ID不能为空");
        }
        
        List<Order> orders = orderRepository.findBySellerId(sellerId.trim());
        return orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCommodityId(String commodityId) {
        if (commodityId == null || commodityId.trim().isEmpty()) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        
        List<Order> orders = orderRepository.findByCommodityId(commodityId.trim());
        return orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(Order.OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("订单状态不能为空");
        }
        
        List<Order> orders = orderRepository.findByOrderStatus(status.getCode());
        return orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        List<Order> orders = orderRepository.findByUserId(userId.trim());
        return orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse updateOrderStatus(UpdateOrderStatusRequest request) {
        // 参数验证
        if (request.getOrderId() == null || request.getOrderId().trim().isEmpty()) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        if (request.getOrderStatus() == null || request.getOrderStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("订单状态不能为空");
        }
        
        // 查询订单
        Order order = orderRepository.selectById(request.getOrderId().trim());
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        // 验证状态转换的合法性
        Order.OrderStatus newStatus = Order.OrderStatus.fromCode(request.getOrderStatus());
        validateStatusTransition(order.getOrderStatus(), newStatus);
        
        // 更新订单状态
        order.setOrderStatus(newStatus);
        if (newStatus == Order.OrderStatus.COMPLETED) {
            order.setSaleTime(LocalDateTime.now());
        }
        
        // 保存更新
        int result = orderRepository.updateById(order);
        if (result <= 0) {
            throw new RuntimeException("更新订单状态失败");
        }
        
        return new OrderResponse(order);
    }

    @Override
    public OrderResponse confirmPayment(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        
        Order order = orderRepository.selectById(orderId.trim());
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        // 确认付款
        order.confirmPayment();
        
        // 保存更新
        int result = orderRepository.updateById(order);
        if (result <= 0) {
            throw new RuntimeException("确认付款失败");
        }
        
        return new OrderResponse(order);
    }

    @Override
    public OrderResponse completeOrder(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        
        Order order = orderRepository.selectById(orderId.trim());
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        // 完成订单
        order.completeOrder();
        
        // 保存更新
        int result = orderRepository.updateById(order);
        if (result <= 0) {
            throw new RuntimeException("完成订单失败");
        }
        
        return new OrderResponse(order);
    }

    @Override
    public boolean cancelOrder(String orderId) {
            if (orderId == null || orderId.trim().isEmpty()) {
                throw new IllegalArgumentException("订单ID不能为空");
            }
            
            Order order = orderRepository.selectById(orderId.trim());
            if (order == null) {
                return false;
            }
            
            // 只有待付款状态的订单才能取消
            if (!order.isPendingPayment()) {
                throw new IllegalStateException("只有待付款状态的订单才能取消");
            }
            
            // 删除订单
            int result = orderRepository.deleteById(orderId.trim());
            return result > 0;

    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return false;
        }
        
        Order order = orderRepository.selectById(orderId.trim());
        return order != null;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderStatistics getOrderStatistics(String userId, boolean isBuyer) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        List<Order> orders = isBuyer ? 
            orderRepository.findByBuyerId(userId.trim()) : 
            orderRepository.findBySellerId(userId.trim());
        
        long totalOrders = orders.size();
        long pendingPaymentOrders = orders.stream()
            .mapToLong(order -> order.isPendingPayment() ? 1 : 0)
            .sum();
        long pendingTransactionOrders = orders.stream()
            .mapToLong(order -> order.isPendingTransaction() ? 1 : 0)
            .sum();
        long completedOrders = orders.stream()
            .mapToLong(order -> order.isCompleted() ? 1 : 0)
            .sum();
        
        return new OrderStatistics(totalOrders, pendingPaymentOrders, 
                                 pendingTransactionOrders, completedOrders);
    }

    /**
     * 验证创建订单请求参数
     */
    private void validateCreateOrderRequest(CreateOrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("创建订单请求不能为空");
        }
        if (request.getCommodityId() == null || request.getCommodityId().trim().isEmpty()) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        if (request.getBuyerId() == null || request.getBuyerId().trim().isEmpty()) {
            throw new IllegalArgumentException("买家ID不能为空");
        }
        if (request.getSellerId() == null || request.getSellerId().trim().isEmpty()) {
            throw new IllegalArgumentException("卖家ID不能为空");
        }
        if (request.getMoney() == null || request.getMoney().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("交易金额必须大于0");
        }
    }

    /**
     * 验证订单状态转换的合法性
     */
    private void validateStatusTransition(Order.OrderStatus currentStatus, Order.OrderStatus newStatus) {
        if (currentStatus == newStatus) {
            return; // 状态相同，无需转换
        }
        
        switch (currentStatus) {
            case PENDING_PAYMENT:
                if (newStatus != Order.OrderStatus.PENDING_TRANSACTION) {
                    throw new IllegalStateException("待付款状态只能转换为待交易状态");
                }
                break;
            case PENDING_TRANSACTION:
                if (newStatus != Order.OrderStatus.COMPLETED) {
                    throw new IllegalStateException("待交易状态只能转换为已完成状态");
                }
                break;
            case COMPLETED:
                throw new IllegalStateException("已完成的订单不能再次修改状态");
            default:
                throw new IllegalStateException("未知的订单状态: " + currentStatus);
        }
    }
}