package com.campus.lostfound.exception;

import lombok.Getter;

/**
 * 业务异常类，用于封装业务逻辑中的异常信息
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    /**
     * 构造业务异常
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造业务异常（默认500错误码）
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }
}
