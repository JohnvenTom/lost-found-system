package com.campus.lostfound.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告实体类，映射t_announcement表
 * 关联用户表（多对一）
 */
@Data
public class Announcement {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private Integer isTop;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String username;
}
