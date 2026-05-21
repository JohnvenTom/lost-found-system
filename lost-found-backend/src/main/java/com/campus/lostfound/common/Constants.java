package com.campus.lostfound.common;

/**
 * 系统常量定义
 */
public class Constants {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final Integer ROLE_USER = 0;
    public static final Integer ROLE_ADMIN = 1;

    public static final Integer ITEM_STATUS_PENDING = 0;
    public static final Integer ITEM_STATUS_APPROVED = 1;
    public static final Integer ITEM_STATUS_REJECTED = 2;
    public static final Integer ITEM_STATUS_CLAIMED = 3;

    public static final Integer CLAIM_STATUS_PENDING = 0;
    public static final Integer CLAIM_STATUS_APPROVED = 1;
    public static final Integer CLAIM_STATUS_REJECTED = 2;

    public static final Integer CONTENT_REVIEW_PASS = 1;
    public static final Integer CONTENT_REVIEW_REJECT = 0;
}
