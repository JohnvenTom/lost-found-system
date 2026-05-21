package com.campus.lostfound.entity.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 留言请求DTO
 */
@Data
public class MessageDTO {

    private Long itemId;

    @NotBlank(message = "留言内容不能为空")
    @Size(max = 500, message = "留言内容最长500个字符")
    private String content;

    private Long parentId;
}
