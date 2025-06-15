package com.example.view_product_information.common;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 统一响应结果
 */
@Data
@Accessors(chain = true)
public class Result<T> {

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>()
                .setCode(200)
                .setMessage("操作成功")
                .setData(data);
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<T>()
                .setCode(200)
                .setMessage("操作成功");
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error(String message) {
        return new Result<T>()
                .setCode(500)
                .setMessage(message);
    }

    /**
     * 失败响应（自定义状态码）
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<T>()
                .setCode(code)
                .setMessage(message);
    }

    /**
     * 资源未找到
     */
    public static <T> Result<T> notFound(String message) {
        return new Result<T>()
                .setCode(404)
                .setMessage(message);
    }
}