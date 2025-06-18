package com.campus.product_management_seller.exception;

import com.campus.product_management_seller.dto.ApiResponse;
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
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.bind.MissingServletRequestParameterException;

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
        
        // 特殊处理 images 参数的错误
        if ("images".equals(e.getName())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("图片参数格式不正确，请确保使用正确的 multipart/form-data 格式上传图片文件", "IMAGES_FORMAT_ERROR"));
        }
        
        String errorMessage = String.format("参数 '%s' 的值 '%s' 类型不正确，期望类型: %s", 
                                           e.getName(), e.getValue(), e.getRequiredType().getSimpleName());
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(errorMessage, "TYPE_MISMATCH"));
    }
    
    /**
     * 处理 multipart 请求解析异常
     */
    @ExceptionHandler(org.springframework.web.multipart.MultipartException.class)
    public ResponseEntity<ApiResponse<Void>> handleMultipartException(
            org.springframework.web.multipart.MultipartException e) {
        
        logger.warn("Multipart 请求解析异常: {}", e.getMessage());
        
        // 检查是否是图片格式相关的错误
        if (e.getMessage() != null && 
            (e.getMessage().contains("index0errorValue must be a string") ||
             e.getMessage().contains("Failed to parse multipart") ||
             e.getMessage().contains("Current request is not a multipart request"))) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("图片上传格式错误。请确保：1) 使用 multipart/form-data 格式；2) 图片参数名为 'images'；3) 每个图片文件单独添加到 FormData 中；4) 不要手动设置 Content-Type", "MULTIPART_FORMAT_ERROR"));
        }
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("文件上传格式错误: " + e.getMessage(), "MULTIPART_ERROR"));
    }
    
    /**
     * 处理缺失的 multipart 请求部分异常
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestPartException(
            MissingServletRequestPartException e) {
        
        logger.warn("缺失请求部分: {}", e.getMessage());
        
        if ("images".equals(e.getRequestPartName())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("商品图片不能为空，请至少上传一张图片", "IMAGES_REQUIRED"));
        }
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("缺失必要的请求参数: " + e.getRequestPartName(), "MISSING_REQUEST_PART"));
    }
    
    /**
     * 处理缺失的请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        
        logger.warn("缺失请求参数: {}", e.getMessage());
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("缺失必要的请求参数: " + e.getParameterName(), "MISSING_PARAMETER"));
    }
    
    /**
     * 处理 HTTP 消息不可读异常（通常是请求体解析失败）
     */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            org.springframework.http.converter.HttpMessageNotReadableException e) {
        
        logger.warn("HTTP 消息不可读异常: {}", e.getMessage());
        
        // 检查是否是图片上传相关的错误
        if (e.getMessage() != null && 
            (e.getMessage().contains("index0errorValue must be a string") ||
             e.getMessage().contains("Required request part") ||
             e.getMessage().contains("multipart"))) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("请求格式错误。对于图片上传，请确保：1) 使用 POST 方法；2) Content-Type 为 multipart/form-data；3) 图片文件正确添加到 'images' 参数中", "REQUEST_FORMAT_ERROR"));
        }
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("请求格式错误: " + e.getMessage(), "MESSAGE_NOT_READABLE"));
    }
    
    /**
     * 处理IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException e) {
        
        logger.warn("非法参数异常: {}", e.getMessage());
        
        // 特殊处理图片相关的错误信息
        if (e.getMessage() != null && e.getMessage().contains("index0errorValue must be a string")) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("图片上传格式错误，请确保使用正确的 FormData 格式，每个图片文件都要单独添加到 'images' 参数中", "IMAGES_FORMAT_ERROR"));
        }
        
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