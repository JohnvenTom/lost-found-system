package com.campus.lostfound.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 认领实体类，映射t_claim表
 * 关联物品表（多对一）和用户表（多对一）
 */
@Data
public class Claim {

    private Long id;
    private Long itemId;
    private Long userId;
    private String reason;
    private String contactInfo;
    private Integer status;
    private String reviewRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String username;
    private String userAvatar;
    private String itemTitle;
}
