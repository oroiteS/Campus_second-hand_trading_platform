package com.campus.ordermanagement.controller;

import com.campus.ordermanagement.dto.*;
import com.campus.ordermanagement.dto.CreateOrderRequest;
import com.campus.ordermanagement.dto.OrderResponse;
import com.campus.ordermanagement.dto.UpdateOrderStatusRequest;
import com.campus.ordermanagement.dto.QueryOrderByIdRequest;
import com.campus.ordermanagement.dto.QueryOrdersByBuyerRequest;
import com.campus.ordermanagement.dto.QueryOrdersBySellerRequest;
import com.campus.ordermanagement.dto.QueryOrdersByCommodityRequest;
import com.campus.ordermanagement.dto.QueryOrdersByStatusRequest;

import com.campus.ordermanagement.dto.CancelOrderRequest;
import com.campus.ordermanagement.dto.OrderStatisticsRequest;
import com.campus.ordermanagement.dto.QueryOrdersByUserRequest;
import com.campus.ordermanagement.pojo.Order;
import com.campus.ordermanagement.service.OrderService;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * 订单API控制器
 * 提供独立的订单管理REST API，每个事务都有独立的接口
 * 前端通过JSON格式传递参数，后端解析并构造请求体
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderApiController {

    @Autowired
    private OrderService orderService;


    // ==================== 统一响应格式 ====================
    
    private ResponseEntity<?> successResponse(String message, Object data) {
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", message,
            "code", 200,
            "data", data
        ));
    }
    
    private ResponseEntity<?> successResponse(String message, Object data, int total) {
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", message,
            "code", 200,
            "data", data,
            "total", total
        ));
    }
    
    private ResponseEntity<?> createdResponse(String message, Object data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "success", true,
            "message", message,
            "code", 201,
            "data", data
        ));
    }
    
    private ResponseEntity<?> errorResponse(String message, int code) {
        return ResponseEntity.status(code).body(Map.of(
            "success", false,
            "message", message,
            "code", code
        ));
    }

    private ResponseEntity<?> handleException(Exception e) {
        String errorMessage;
        
        if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
            errorMessage = e.getMessage() != null ? e.getMessage() : "请求参数错误";
            return errorResponse(errorMessage, 400);
        } else if (e instanceof RuntimeException) {
            errorMessage = e.getMessage() != null ? e.getMessage() : "服务器处理异常";
            // 记录详细错误日志
            System.err.println("服务异常: " + errorMessage);
            e.printStackTrace();
            return errorResponse("服务暂时不可用，请稍后重试", 500);
        } else {
            errorMessage = e.getMessage() != null ? e.getMessage() : "系统内部错误";
            // 记录详细错误日志
            System.err.println("系统异常: " + errorMessage);
            e.printStackTrace();
            return errorResponse("系统异常，请联系管理员", 500);
        }
    }

    // ==================== API接口定义 ====================
    
    /**
     * 创建新订单
     */
    @PostMapping("/create")
    @Operation(
        summary = "创建订单", 
        description = "创建一个新的订单，订单初始状态为PENDING_PAYMENT（待付款）"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "订单创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<?> createOrder(
        @Parameter(description = "创建订单请求体，包含商品ID、买家ID、卖家ID、金额和交易地点")
        @Valid @RequestBody CreateOrderRequest request) {
        try {
            OrderResponse orderResponse = orderService.createOrder(request);
            return createdResponse("订单创建成功", orderResponse);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * 根据订单ID查询订单
     */
    @PostMapping("/query/by-id")
    @Operation(summary = "查询订单详情", description = "根据订单ID查询订单详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "订单不存在"),
        @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<?> queryOrderById(
        @Parameter(description = "查询订单请求体，包含订单ID")
        @Valid @RequestBody QueryOrderByIdRequest request) {
        try {
            Optional<OrderResponse> orderResponse = orderService.getOrderById(request.getOrderId());
            
            if (orderResponse.isPresent()) {
                return successResponse("查询成功", orderResponse.get());
            } else {
                return errorResponse("订单不存在", 404);
            }
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * 根据买家ID查询订单列表
     */
    @PostMapping("/query/by-buyer")
    @Operation(summary = "查询买家订单", description = "根据买家ID查询订单列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<?> queryOrdersByBuyer(
        @Parameter(description = "查询买家订单请求体，包含买家ID")
        @Valid @RequestBody QueryOrdersByBuyerRequest request) {
        try {
            List<OrderResponse> orders = orderService.getOrdersByBuyerId(request.getBuyerId());
            return successResponse("查询成功", orders, orders.size());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * 根据卖家ID查询订单列表
     */
    @PostMapping("/query/by-seller")
    @Operation(summary = "查询卖家订单", description = "根据卖家ID查询订单列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<?> queryOrdersBySeller(
        @Parameter(description = "查询卖家订单请求体，包含卖家ID")
        @Valid @RequestBody QueryOrdersBySellerRequest request) {
        try {
            List<OrderResponse> orders = orderService.getOrdersBySellerId(request.getSellerId());
            return successResponse("查询成功", orders, orders.size());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * 根据商品ID查询订单列表
     */
    @PostMapping("/query/by-commodity")
    @Operation(summary = "查询商品订单", description = "根据商品ID查询订单列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<?> queryOrdersByCommodity(
        @Parameter(description = "查询商品订单请求体，包含商品ID")
        @Valid @RequestBody QueryOrdersByCommodityRequest request) {
        try {
            List<OrderResponse> orders = orderService.getOrdersByCommodityId(request.getCommodityId());
            return successResponse("查询成功", orders, orders.size());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * 根据订单状态查询订单列表
     */
    @PostMapping("/query/by-status")
    @Operation(summary = "按状态查询订单", description = "根据订单状态查询订单列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<?> queryOrdersByStatus(
        @Parameter(description = "查询订单状态请求体，包含订单状态")
        @Valid @RequestBody QueryOrdersByStatusRequest request) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.fromCode(request.getStatus());
            List<OrderResponse> orders = orderService.getOrdersByStatus(orderStatus);
            return successResponse("查询成功", orders, orders.size());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * 根据用户ID查询用户参与的所有订单
     */
    @PostMapping("/query/by-user")
    @Operation(summary = "查询用户参与的订单", description = "根据用户ID查询用户作为买家或卖家参与的所有订单")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<?> queryOrdersByUser(
        @Parameter(description = "查询用户订单请求体，包含用户ID")
        @Valid @RequestBody QueryOrdersByUserRequest request) {
        try {
            List<OrderResponse> orders = orderService.getOrdersByUserId(request.getUserId());
            return successResponse("查询成功", orders, orders.size());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * 更新订单状态
     */
    @PostMapping("/update-status")
    @Operation(summary = "更新订单状态", description = "更新订单的状态")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "状态更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<?> updateOrderStatus(
        @Parameter(description = "更新订单状态请求体")
        @Valid @RequestBody UpdateOrderStatusRequest request) {
        try {
            OrderResponse orderResponse = orderService.updateOrderStatus(request);
            return successResponse("订单状态更新成功", orderResponse);
        } catch (Exception e) {
            return handleException(e);
        }
    }



    /**
     * 取消订单
     */
    @PostMapping("/cancel")
    @Operation(
        summary = "取消订单", 
        description = "取消指定的订单，只有待付款状态的订单才能取消"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "订单取消成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误或订单状态不允许取消"),
        @ApiResponse(responseCode = "404", description = "订单不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<?> cancelOrder(
        @Parameter(description = "取消订单请求体，包含订单ID")
        @Valid @RequestBody CancelOrderRequest request,
        HttpServletRequest httpRequest) throws UnsupportedEncodingException {
        
        // 设置响应编码
        httpRequest.setCharacterEncoding("UTF-8");
        
        // 参数验证和编码清理
        if (request == null || request.getOrderId() == null || request.getOrderId().trim().isEmpty()) {
            return createSafeErrorResponse("订单ID不能为空", 400);
        }
        
        try {
            // 清理和验证订单ID，确保只包含安全字符
            String orderId = sanitizeString(request.getOrderId().trim());
            
            if (orderId.isEmpty()) {
                return createSafeErrorResponse("订单ID格式无效", 400);
            }
            
            boolean result = orderService.cancelOrder(orderId);
            
            if (result) {
                return createSafeSuccessResponse("订单取消成功", Map.of("orderId", orderId, "status", "cancelled"));
            } else {
                return createSafeErrorResponse("订单不存在或无法取消", 404);
            }
            
        } catch (IllegalArgumentException e) {
            String message = sanitizeString(e.getMessage() != null ? e.getMessage() : "参数错误");
            return createSafeErrorResponse(message, 400);
        } catch (IllegalStateException e) {
            String message = sanitizeString(e.getMessage() != null ? e.getMessage() : "订单状态不允许取消");
            return createSafeErrorResponse(message, 400);
        } catch (Exception e) {
            // 记录详细错误日志，但返回安全的错误信息
            System.err.println("取消订单时发生异常: " + e.getClass().getSimpleName());
            if (e.getMessage() != null) {
                System.err.println("错误详情: " + e.getMessage());
            }
            e.printStackTrace();
            return createSafeErrorResponse("订单取消失败，请稍后重试", 500);
        }
    }
    
    /**
     * 创建安全的成功响应，确保所有字符串都是UTF-8编码
     */
    private ResponseEntity<?> createSafeSuccessResponse(String message, Object data) {
        try {
            Map<String, Object> response = Map.of(
                "success", true,
                "message", sanitizeString(message),
                "code", 200,
                "data", sanitizeData(data)
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createSafeErrorResponse("响应生成失败", 500);
        }
    }
    
    /**
     * 创建安全的错误响应，确保所有字符串都是UTF-8编码
     */
    private ResponseEntity<?> createSafeErrorResponse(String message, int code) {
        try {
            Map<String, Object> response = Map.of(
                "success", false,
                "message", sanitizeString(message),
                "code", code
            );
            return ResponseEntity.status(code).body(response);
        } catch (Exception e) {
            // 最后的安全网，返回最基本的错误响应
            Map<String, Object> fallbackResponse = Map.of(
                "success", false,
                "message", "Internal Server Error",
                "code", 500
            );
            return ResponseEntity.status(500).body(fallbackResponse);
        }
    }
    
    /**
     * 清理字符串，移除可能导致编码问题的字符
     */
    private String sanitizeString(String input) {
        if (input == null) {
            return "";
        }
        
        try {
            // 转换为UTF-8字节数组再转回字符串，过滤无效字符
            byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
            String result = new String(bytes, StandardCharsets.UTF_8);
            
            // 移除控制字符（除了常见的空白字符）
            result = result.replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]", "");
            
            return result;
        } catch (Exception e) {
            return "编码转换失败";
        }
    }
    
    /**
     * 清理数据对象，确保其中的字符串都是安全的
     */
    private Object sanitizeData(Object data) {
        if (data == null) {
            return null;
        }
        
        if (data instanceof String) {
            return sanitizeString((String) data);
        }
        
        if (data instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) data;
            Map<String, Object> sanitizedMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                sanitizedMap.put(sanitizeString(entry.getKey()), sanitizeData(entry.getValue()));
            }
            return sanitizedMap;
        }
        
        return data;
    }

    /**
     * 获取订单统计信息
     */
    @PostMapping("/statistics")
    @Operation(summary = "获取订单统计", description = "获取用户的订单统计信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "统计信息获取成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<?> getOrderStatistics(
        @Parameter(description = "获取订单统计请求体，包含用户ID和角色")
        @Valid @RequestBody OrderStatisticsRequest request) {
        try {
            if (!"buyer".equals(request.getRole()) && !"seller".equals(request.getRole())) {
                return errorResponse("角色参数必须是 buyer 或 seller", 400);
            }
            
            boolean isBuyer = "buyer".equals(request.getRole());
            OrderService.OrderStatistics statistics = orderService.getOrderStatistics(request.getUserId(), isBuyer);
            
            return successResponse("查询成功", Map.of(
                "totalOrders", statistics.getTotalOrders(),
                "pendingPaymentOrders", statistics.getPendingPaymentOrders(),
                "pendingTransactionOrders", statistics.getPendingTransactionOrders(),
                "completedOrders", statistics.getCompletedOrders(),
                "role", request.getRole()
            ));
        } catch (Exception e) {
            return handleException(e);
        }
    }
}
