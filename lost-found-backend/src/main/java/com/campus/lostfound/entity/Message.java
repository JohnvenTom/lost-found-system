package com.campus.lostfound.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 留言实体类，映射t_message表
 * 关联物品表（多对一）和用户表（多对一），支持树形回复结构
 */
@Data
public class Message {

    private Long id;
    private Long itemId;
    private Long userId;
    private String content;
    private Long parentId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String username;
    private String userAvatar;
    private String parentUsername;
}
