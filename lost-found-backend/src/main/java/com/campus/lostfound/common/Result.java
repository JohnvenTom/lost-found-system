package com.campus.lostfound.common;

import lombok.Data;

/**
 * 统一响应结果封装
 * @param <T> 数据类型
 */
@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    private Result() {}

    /**
     * 成功响应（无数据）
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        return result;
    }

    /**
     * 成功响应（带数据）
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（自定义消息和数据）
     * @param message 响应消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败响应
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 失败结果
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败响应（默认500错误码）
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 失败结果
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
}
