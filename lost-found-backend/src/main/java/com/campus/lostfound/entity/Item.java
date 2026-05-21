package com.campus.lostfound.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物品实体类，映射t_item表
 * 关联用户表（多对一）和分类表（多对一）
 */
@Data
public class Item {

    private Long id;
    private Long userId;
    private Long categoryId;
    private String title;
    private String description;
    private String images;
    private String location;
    private String contactInfo;
    private Integer itemType;
    private Integer status;
    private String aiCategory;
    private String aiReviewResult;
    private Integer aiReviewPass;
    private LocalDateTime foundTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String username;
    private String userAvatar;
    private String categoryName;
}
