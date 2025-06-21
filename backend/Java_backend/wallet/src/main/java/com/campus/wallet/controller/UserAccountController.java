package com.campus.wallet.controller;

import com.campus.wallet.pojo.WithdrawRequest;
import com.campus.wallet.service.UserAccountService;
import org.springframework.web.bind.annotation.*;
import com.campus.wallet.pojo.RechargeRequest;
import com.campus.wallet.pojo.SellerRefundRequest;
import com.campus.wallet.pojo.PayRequest;
import com.campus.wallet.pojo.ConfirmReceiptRequest;
import com.campus.wallet.pojo.ServiceResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
// 移除Spring Security相关导入
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.campus.wallet.pojo.BalanceRequest;

@Tag(name = "用户钱包管理", description = "用户钱包相关接口")
@RestController
@RequestMapping("/api-8081/user/account")
@CrossOrigin(origins = "*")
public class UserAccountController {
    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Operation(summary = "确认收货", description = "用户确认收货操作")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "确认收货成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/confirmReceipt")
    public ResponseEntity<Map<String, Object>> confirmReceipt(
            @Parameter(description = "确认收货请求体，包含用户ID和订单ID", required = true)
            @Valid @RequestBody ConfirmReceiptRequest request) {
        try {
            // 从请求体中获取用户ID和订单ID
            String userID = request.getUserId();
            String orderID = request.getOrderID();
            
            ServiceResult<Void> result = userAccountService.confirmReceipt(userID, orderID);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.getCode() == 200);
            response.put("message", result.getMessage());
            response.put("code", result.getCode());
            
            if (result.getCode() == 200) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(result.getCode()).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "服务器内部错误: " + e.getMessage());
            errorResponse.put("code", 500);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Operation(summary = "提现", description = "用户提现操作")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "提现成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(
            @Parameter(description = "提现请求体，包含用户ID和提现金额", required = true)
            @Valid @RequestBody WithdrawRequest request) {
        try {
            // 从请求体中获取用户ID和金额
            String userID = request.getUserId();
            BigDecimal amount = request.getAmount();
            
            ServiceResult<Void> result = userAccountService.withdraw(userID, amount);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.getCode() == 200);
            response.put("message", result.getMessage());
            response.put("code", result.getCode());
            
            if (result.getCode() == 200) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(result.getCode()).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "服务器内部错误: " + e.getMessage());
            errorResponse.put("code", 500);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Operation(summary = "充值", description = "用户充值操作")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "充值成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/recharge")
    public ResponseEntity<Map<String, Object>> recharge(
            @Parameter(description = "充值请求体，包含用户ID和充值金额", required = true)
            @Valid @RequestBody RechargeRequest request) {
        try {
            // 从请求体中获取用户ID和金额
            String userID = request.getUserId();
            BigDecimal amount = request.getAmount();
            
            ServiceResult<Void> result = userAccountService.Recharge(userID, amount);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.getCode() == 200);
            response.put("message", result.getMessage());
            response.put("code", result.getCode());
            
            if (result.getCode() == 200) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(result.getCode()).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "服务器内部错误: " + e.getMessage());
            errorResponse.put("code", 500);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Operation(summary = "卖家退款", description = "卖家退款操作")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "退款成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/sellerRefund")
    public ResponseEntity<Map<String, Object>> sellerRefund(
            @Parameter(description = "卖家退款请求体，包含用户ID和订单ID", required = true)
            @Valid @RequestBody SellerRefundRequest request) {
        try {
            // 从请求体中获取用户ID和订单ID
            String userID = request.getUserId();
            String orderID = request.getOrderID();
            
            ServiceResult<Void> result = userAccountService.sellerRefund(userID, orderID);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.getCode() == 200);
            response.put("message", result.getMessage());
            response.put("code", result.getCode());
            
            if (result.getCode() == 200) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(result.getCode()).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "服务器内部错误: " + e.getMessage());
            errorResponse.put("code", 500);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Operation(summary = "支付", description = "用户支付操作")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "支付成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/pay")
    public ResponseEntity<Map<String, Object>> pay(
            @Parameter(description = "支付请求体，包含用户ID和订单ID", required = true)
            @Valid @RequestBody PayRequest request) {
        try {
            // 从请求体中获取用户ID和订单ID
            String userID = request.getUserId();
            String orderID = request.getOrderID();
            
            ServiceResult<Void> result = userAccountService.Pay(userID, orderID);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.getCode() == 200);
            response.put("message", result.getMessage());
            response.put("code", result.getCode());
            
            if (result.getCode() == 200) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(result.getCode()).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "服务器内部错误: " + e.getMessage());
            errorResponse.put("code", 500);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Operation(summary = "获取钱包余额", description = "获取用户钱包余额")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "404", description = "用户钱包不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance(
            @Parameter(description = "余额查询请求体，包含用户ID", required = true)
            @Valid @RequestBody BalanceRequest request) {
        try {
            // 从请求体中获取用户ID
            String userID = request.getUserId();
            
            ServiceResult<BigDecimal> result = userAccountService.getBalance(userID);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.getCode() == 200);
            response.put("message", result.getMessage());
            response.put("code", result.getCode());
            
            if (result.getCode() == 200) {
                response.put("data", result.getData());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(result.getCode()).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "服务器内部错误: " + e.getMessage());
            errorResponse.put("code", 500);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
