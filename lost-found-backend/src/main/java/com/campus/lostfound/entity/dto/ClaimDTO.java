package com.campus.lostfound.entity.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 认领申请请求DTO
 */
@Data
public class ClaimDTO {

    private Long itemId;

    @NotBlank(message = "认领理由不能为空")
    @Size(max = 500, message = "认领理由最长500个字符")
    private String reason;

    @NotBlank(message = "联系方式不能为空")
    @Size(max = 200, message = "联系方式最长200个字符")
    private String contactInfo;
}
