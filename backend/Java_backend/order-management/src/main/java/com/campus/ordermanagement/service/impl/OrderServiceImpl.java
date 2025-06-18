package com.campus.ordermanagement.service.impl;

import com.campus.ordermanagement.dao.OrderRepository;
import com.campus.ordermanagement.dao.CommodityRepository;
import com.campus.ordermanagement.pojo.Commodity;
import com.campus.ordermanagement.dto.CreateOrderRequest;
import com.campus.ordermanagement.dto.OrderResponse;
import com.campus.ordermanagement.dto.UpdateOrderStatusRequest;
import com.campus.ordermanagement.pojo.Order;
import com.campus.ordermanagement.service.OrderService;
import com.campus.ordermanagement.util.UUIDGenerator;
// 移除重复的商品相关导入
// import com.campus.product_management_seller.entity.Commodity;
// import com.campus.product_management_seller.repository.CommodityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
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
    
    @Autowired
    private CommodityRepository commodityRepository;

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "ordersByBuyer", key = "#request.buyerId"),
        @CacheEvict(value = "ordersBySeller", key = "#request.sellerId"),
        @CacheEvict(value = "ordersByCommodity", key = "#request.commodityId"),
        @CacheEvict(value = "orderStatistics", key = "#request.buyerId + '_buyer'"),
        @CacheEvict(value = "orderStatistics", key = "#request.sellerId + '_seller'")
    })
    public OrderResponse createOrder(CreateOrderRequest request) {
        // 参数验证
        validateCreateOrderRequest(request);
        
        // 检查买家和卖家不能是同一人
        if (request.getBuyerId().equals(request.getSellerId())) {
            throw new IllegalArgumentException("买家和卖家不能是同一人");
        }
        
        // 检查商品是否存在
        Commodity commodity = commodityRepository.selectById(request.getCommodityId());
        if (commodity == null) {
            throw new IllegalArgumentException("商品不存在");
        }
        
        // 检查商品状态是否为在售
        if (!Commodity.STATUS_ON_SALE.equals(commodity.getCommodityStatus())) {
            String statusDesc = getStatusDescription(commodity.getCommodityStatus());
            throw new IllegalArgumentException("商品当前不可购买，商品状态：" + statusDesc);
        }
        
        // 检查购买数量是否超过库存
        if (request.getBuyQuantity() > commodity.getQuantity()) {
            throw new IllegalArgumentException(
                String.format("购买数量超过商品库存。当前库存：%d，请求数量：%d", 
                    commodity.getQuantity(), request.getBuyQuantity())
            );
        }
        
        // 使用原子操作减少库存并在必要时更新状态
        int updateResult = commodityRepository.decreaseQuantityAndUpdateStatus(
            request.getCommodityId(), 
            request.getBuyQuantity()
        );
        
        if (updateResult <= 0) {
            throw new RuntimeException("库存更新失败，可能是库存不足或商品已被其他用户购买");
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
        order.setBuyQuantity(request.getBuyQuantity());
        
        // 保存订单
        int result = orderRepository.insert(order);
        if (result <= 0) {
            throw new RuntimeException("创建订单失败");
        }
        
        return new OrderResponse(order);
    }
    
    /**
     * 获取商品状态的中文描述
     */
    private String getStatusDescription(String status) {
        switch (status) {
            case Commodity.STATUS_ON_SALE:
                return "在售";
            case Commodity.STATUS_SOLD:
                return "已售出";
            case Commodity.STATUS_OFF_SALE:
                return "已下架";
            default:
                return "未知状态";
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "orders", key = "#orderId", unless = "#result == null")
    public Optional<OrderResponse> getOrderById(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return Optional.empty();
        }
        
        Order order = orderRepository.selectById(orderId.trim());
        return order != null ? Optional.of(new OrderResponse(order)) : Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "ordersByBuyer", key = "#buyerId")
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
    @Cacheable(value = "ordersBySeller", key = "#sellerId")
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
    @Cacheable(value = "ordersByCommodity", key = "#commodityId")
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
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
        @CacheEvict(value = "orders", key = "#orderId"),
        @CacheEvict(value = "ordersByBuyer", allEntries = true),
        @CacheEvict(value = "ordersBySeller", allEntries = true),
        @CacheEvict(value = "orderStatistics", allEntries = true)
    })
    public boolean cancelOrder(String orderId) {
        try {
            // 参数验证和清理
            if (orderId == null || orderId.trim().isEmpty()) {
                throw new IllegalArgumentException("订单ID不能为空");
            }
            
            String trimmedOrderId = sanitizeOrderId(orderId.trim());
            
            if (trimmedOrderId.isEmpty()) {
                throw new IllegalArgumentException("订单ID格式无效");
            }
            
            // 查询订单
            Order order = null;
            try {
                order = orderRepository.selectById(trimmedOrderId);
            } catch (Exception e) {
                throw new RuntimeException("查询订单失败", e);
            }
            
            // 检查订单是否存在
            if (order == null) {
                return false;
            }
            
            // 检查订单状态
            if (order.getOrderStatus() == null) {
                throw new IllegalStateException("订单状态异常，无法取消");
            }
            
            if (!order.isPendingPayment()) {
                throw new IllegalStateException("只有待付款状态的订单才能取消");
            }
            
            // 执行删除操作
            int deleteResult = 0;
            try {
                deleteResult = orderRepository.deleteById(trimmedOrderId);
            } catch (Exception e) {
                throw new RuntimeException("删除订单失败", e);
            }
            
            // 检查删除结果
            if (deleteResult <= 0) {
                throw new RuntimeException("订单删除失败，可能已被其他操作删除");
            }
            
            return true;
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            // 重新抛出业务异常，让控制器处理
            throw e;
        } catch (Exception e) {
            // 包装其他异常，确保错误信息是安全的
            throw new RuntimeException("取消订单操作失败", e);
        }
    }
    
    /**
     * 清理订单ID，确保只包含安全字符
     */
    private String sanitizeOrderId(String orderId) {
        if (orderId == null) {
            return "";
        }
        
        // 只保留字母、数字、连字符和下划线
        return orderId.replaceAll("[^a-zA-Z0-9\\-_]", "");
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
            throw new IllegalArgumentException("请求参数不能为空");
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
        if (request.getBuyQuantity() == null || request.getBuyQuantity() <= 0) {
            throw new IllegalArgumentException("购买数量必须大于0");
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