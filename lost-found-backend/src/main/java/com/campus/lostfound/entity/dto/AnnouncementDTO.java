package com.campus.lostfound.entity.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 公告请求DTO
 */
@Data
public class AnnouncementDTO {

    @NotBlank(message = "公告标题不能为空")
    @Size(max = 200, message = "公告标题最长200个字符")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    private String content;

    private Integer isTop;

    private Integer status;
}
