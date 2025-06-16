package com.campus.ordermanagement.controller;

import com.campus.ordermanagement.dao.OrderRepository;
import com.campus.ordermanagement.pojo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 * 处理订单相关的REST API请求
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * 根据买家ID查询订单列表
     * 前端传入JSON格式：{"userId": "U10000001"}
     * 
     * @param request 包含userId的请求体
     * @return 订单列表
     */
    @PostMapping("/buyer")
    public ResponseEntity<?> getOrdersByBuyerId(@Valid @RequestBody UserIdRequest request) {
        try {
            String userId = request.getUserId();
            
            // 参数验证
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "用户ID不能为空",
                    "code", 400
                ));
            }
            
            // 查询订单
            List<Order> orders = orderRepository.findByBuyerId(userId.trim());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "查询成功",
                "code", 200,
                "data", orders,
                "total", orders.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "服务器内部错误: " + e.getMessage(),
                "code", 500
            ));
        }
    }

    /**
     * 根据卖家ID查询订单列表
     * 前端传入JSON格式：{"userId": "U10000001"}
     * 
     * @param request 包含userId的请求体
     * @return 订单列表
     */
    @PostMapping("/seller")
    public ResponseEntity<?> getOrdersBySellerId(@Valid @RequestBody UserIdRequest request) {
        try {
            String userId = request.getUserId();
            
            // 参数验证
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "用户ID不能为空",
                    "code", 400
                ));
            }
            
            // 查询订单
            List<Order> orders = orderRepository.findBySellerId(userId.trim());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "查询成功",
                "code", 200,
                "data", orders,
                "total", orders.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "服务器内部错误: " + e.getMessage(),
                "code", 500
            ));
        }
    }

    /**
     * 根据买家ID和订单状态查询订单列表
     * 前端传入JSON格式：{"userId": "U10000001", "orderStatus": "pending_payment"}
     * 
     * @param request 包含userId和orderStatus的请求体
     * @return 订单列表
     */
    @PostMapping("/buyer/status")
    public ResponseEntity<?> getOrdersByBuyerIdAndStatus(@Valid @RequestBody UserIdStatusRequest request) {
        try {
            String userId = request.getUserId();
            String orderStatus = request.getOrderStatus();
            
            // 参数验证
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "用户ID不能为空",
                    "code", 400
                ));
            }
            
            if (orderStatus == null || orderStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "订单状态不能为空",
                    "code", 400
                ));
            }
            
            // 验证订单状态是否有效
            if (!isValidOrderStatus(orderStatus)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "无效的订单状态，支持的状态：pending_payment, pending_transaction, completed",
                    "code", 400
                ));
            }
            
            // 查询订单
            List<Order> orders = orderRepository.findByBuyerIdAndOrderStatus(userId.trim(), orderStatus.trim());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "查询成功",
                "code", 200,
                "data", orders,
                "total", orders.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "服务器内部错误: " + e.getMessage(),
                "code", 500
            ));
        }
    }

    /**
     * 根据用户ID查询订单列表（买家或卖家）
     * 前端传入JSON格式：{"userId": "U10000001"}
     * 查询该用户作为买家或卖家的所有订单
     * 
     * @param request 包含userId的请求体
     * @return 订单列表
     */
    @PostMapping("/user")
    public ResponseEntity<?> getOrdersByUserId(@Valid @RequestBody UserIdRequest request) {
        try {
            String userId = request.getUserId();
            
            // 参数验证
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "用户ID不能为空",
                    "code", 400
                ));
            }
            
            // 查询订单
            List<Order> orders = orderRepository.findByBuyerIdOrSellerId(userId.trim());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "查询成功",
                "code", 200,
                "data", orders,
                "total", orders.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "服务器内部错误: " + e.getMessage(),
                "code", 500
            ));
        }
    }

    /**
     * 根据订单ID查询单个订单
     * 
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {
        try {
            // 参数验证
            if (orderId == null || orderId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "订单ID不能为空",
                    "code", 400
                ));
            }
            
            // 查询订单
            Order order = orderRepository.selectById(orderId.trim());
            
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "查询成功",
                "code", 200,
                "data", order
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "服务器内部错误: " + e.getMessage(),
                "code", 500
            ));
        }
    }

    /**
     * 验证订单状态是否有效
     * 
     * @param orderStatus 订单状态
     * @return 是否有效
     */
    private boolean isValidOrderStatus(String orderStatus) {
        return "pending_payment".equals(orderStatus) || 
               "pending_transaction".equals(orderStatus) || 
               "completed".equals(orderStatus);
    }

    /**
     * 用户ID请求体类
     */
    public static class UserIdRequest {
        @NotBlank(message = "用户ID不能为空")
        private String userId;

        public UserIdRequest() {}

        public UserIdRequest(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    /**
     * 用户ID和状态请求体类
     */
    public static class UserIdStatusRequest {
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        @NotBlank(message = "订单状态不能为空")
        private String orderStatus;

        public UserIdStatusRequest() {}

        public UserIdStatusRequest(String userId, String orderStatus) {
            this.userId = userId;
            this.orderStatus = orderStatus;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }
    }
}