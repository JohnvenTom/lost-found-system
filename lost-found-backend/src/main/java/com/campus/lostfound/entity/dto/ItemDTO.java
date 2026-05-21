package com.campus.lostfound.entity.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 物品发布请求DTO
 */
@Data
public class ItemDTO {

    @NotBlank(message = "物品名称不能为空")
    @Size(max = 100, message = "物品名称最长100个字符")
    private String title;

    @NotBlank(message = "物品描述不能为空")
    @Size(max = 2000, message = "物品描述最长2000个字符")
    private String description;

    private String images;

    @NotBlank(message = "地点不能为空")
    @Size(max = 200, message = "地点最长200个字符")
    private String location;

    @Size(max = 200, message = "联系方式最长200个字符")
    private String contactInfo;

    private Integer itemType;

    private String foundTime;
}
