package com.campus.ordermanagement.exception;

import org.springframework.http.HttpStatus;

/**
 * 订单业务异常类
 * 用于处理订单相关的业务异常
 */
public class OrderBusinessException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;

    /**
     * 构造函数
     * 
     * @param message 异常消息
     */
    public OrderBusinessException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.errorCode = "ORDER_ERROR";
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param httpStatus HTTP状态码
     */
    public OrderBusinessException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = "ORDER_ERROR";
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param httpStatus HTTP状态码
     * @param errorCode 错误代码
     */
    public OrderBusinessException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 原因
     */
    public OrderBusinessException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.errorCode = "ORDER_ERROR";
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 原因
     * @param httpStatus HTTP状态码
     * @param errorCode 错误代码
     */
    public OrderBusinessException(String message, Throwable cause, HttpStatus httpStatus, String errorCode) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // 静态工厂方法
    
    /**
     * 订单不存在异常
     */
    public static OrderBusinessException orderNotFound(String orderId) {
        return new OrderBusinessException(
            "订单不存在: " + orderId, 
            HttpStatus.NOT_FOUND, 
            "ORDER_NOT_FOUND"
        );
    }

    /**
     * 订单状态无效异常
     */
    public static OrderBusinessException invalidOrderStatus(String currentStatus, String targetStatus) {
        return new OrderBusinessException(
            String.format("无法将订单状态从 %s 更改为 %s", currentStatus, targetStatus),
            HttpStatus.BAD_REQUEST,
            "INVALID_ORDER_STATUS_TRANSITION"
        );
    }

    /**
     * 权限不足异常
     */
    public static OrderBusinessException insufficientPermission(String userId, String orderId) {
        return new OrderBusinessException(
            String.format("用户 %s 没有权限操作订单 %s", userId, orderId),
            HttpStatus.FORBIDDEN,
            "INSUFFICIENT_PERMISSION"
        );
    }

    /**
     * 商品不存在异常
     */
    public static OrderBusinessException commodityNotFound(String commodityId) {
        return new OrderBusinessException(
            "商品不存在: " + commodityId,
            HttpStatus.BAD_REQUEST,
            "COMMODITY_NOT_FOUND"
        );
    }

    /**
     * 用户不存在异常
     */
    public static OrderBusinessException userNotFound(String userId) {
        return new OrderBusinessException(
            "用户不存在: " + userId,
            HttpStatus.BAD_REQUEST,
            "USER_NOT_FOUND"
        );
    }

    /**
     * 订单创建失败异常
     */
    public static OrderBusinessException orderCreationFailed(String reason) {
        return new OrderBusinessException(
            "订单创建失败: " + reason,
            HttpStatus.INTERNAL_SERVER_ERROR,
            "ORDER_CREATION_FAILED"
        );
    }

    /**
     * 订单更新失败异常
     */
    public static OrderBusinessException orderUpdateFailed(String orderId, String reason) {
        return new OrderBusinessException(
            String.format("订单 %s 更新失败: %s", orderId, reason),
            HttpStatus.INTERNAL_SERVER_ERROR,
            "ORDER_UPDATE_FAILED"
        );
    }
}