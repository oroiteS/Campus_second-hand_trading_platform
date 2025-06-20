package com.campus.admin_login.dto;
/*
*@belongsPackage: com.campus.admin_login.dto
*@author: syn
*@createTime: 2025-06-20
*@description:  Api回复类
*@version: 1.0
*/
import lombok.Data;

@Data
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("成功");
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(400);
        response.setMessage(message);
        return response;
    }
}
