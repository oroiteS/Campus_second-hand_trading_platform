package com.campus.wallet.pojo;

public class ServiceResult<T> {
    private int code;
    private String message;
    private T data;

    public ServiceResult() {}

    public ServiceResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ServiceResult<T> success() {
        return new ServiceResult<>(200, "操作成功", null);
    }

    public static <T> ServiceResult<T> success(T data) {
        return new ServiceResult<>(200, "操作成功", data);
    }

    public static <T> ServiceResult<T> success(String message, T data) {
        return new ServiceResult<>(200, message, data);
    }

    public static <T> ServiceResult<T> error(int code, String message) {
        return new ServiceResult<>(code, message, null);
    }

    public static <T> ServiceResult<T> error(String message) {
        return new ServiceResult<>(500, message, null);
    }

    public boolean isSuccess() {
        return code == 200;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}