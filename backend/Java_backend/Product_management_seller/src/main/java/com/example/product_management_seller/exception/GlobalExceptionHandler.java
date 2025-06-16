package com.example.product_management_seller.exception;

import com.example.product_management_seller.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理参数验证异常（@Valid注解）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        
        logger.warn("参数验证失败: {}", e.getMessage());
        
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("参数验证失败: " + errorMessage, "VALIDATION_ERROR"));
    }
    
    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
        
        logger.warn("参数绑定失败: {}", e.getMessage());
        
        String errorMessage = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("参数绑定失败: " + errorMessage, "BIND_ERROR"));
    }
    
    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
            ConstraintViolationException e) {
        
        logger.warn("约束验证失败: {}", e.getMessage());
        
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("约束验证失败: " + errorMessage, "CONSTRAINT_VIOLATION"));
    }
    
    /**
     * 处理方法参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        
        logger.warn("参数类型不匹配: parameter={}, value={}, requiredType={}", 
                   e.getName(), e.getValue(), e.getRequiredType());
        
        String errorMessage = String.format("参数 '%s' 的值 '%s' 类型不正确，期望类型: %s", 
                                           e.getName(), e.getValue(), e.getRequiredType().getSimpleName());
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(errorMessage, "TYPE_MISMATCH"));
    }
    
    /**
     * 处理IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException e) {
        
        logger.warn("非法参数异常: {}", e.getMessage());
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("参数错误: " + e.getMessage(), "ILLEGAL_ARGUMENT"));
    }
    
    /**
     * 处理RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        
        logger.error("运行时异常: {}", e.getMessage(), e);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("服务器内部错误: " + e.getMessage(), "RUNTIME_ERROR"));
    }
    
    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        
        logger.error("未知异常: {}", e.getMessage(), e);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("服务器内部错误，请稍后重试", "UNKNOWN_ERROR"));
    }
    
    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Void>> handleNullPointerException(NullPointerException e) {
        
        logger.error("空指针异常: {}", e.getMessage(), e);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("服务器内部错误：空指针异常", "NULL_POINTER_ERROR"));
    }
    
    /**
     * 处理数据库相关异常
     */
    @ExceptionHandler({org.springframework.dao.DataAccessException.class})
    public ResponseEntity<ApiResponse<Void>> handleDataAccessException(
            org.springframework.dao.DataAccessException e) {
        
        logger.error("数据库访问异常: {}", e.getMessage(), e);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("数据库操作失败，请稍后重试", "DATABASE_ERROR"));
    }
}