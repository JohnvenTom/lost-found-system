package com.campus.lostfound.entity.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户注册请求DTO
 */
@Data
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度2-50个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度6-20个字符")
    private String password;

    @Size(max = 50, message = "昵称最长50个字符")
    private String nickname;

    @Size(max = 20, message = "手机号最长20个字符")
    private String phone;

    @Size(max = 100, message = "邮箱最长100个字符")
    private String email;
}
