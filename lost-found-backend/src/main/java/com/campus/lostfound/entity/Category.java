package com.campus.lostfound.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类实体类，映射t_category表
 */
@Data
public class Category {

    private Long id;
    private String name;
    private String icon;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
