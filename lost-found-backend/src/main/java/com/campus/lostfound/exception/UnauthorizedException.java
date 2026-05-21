package com.campus.lostfound.exception;

/**
 * 未授权异常，用于标识用户未登录或Token无效的场景
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * 构造未授权异常
     * @param message 异常消息
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
