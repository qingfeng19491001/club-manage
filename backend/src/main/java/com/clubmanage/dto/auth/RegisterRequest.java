package com.clubmanage.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 64)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64)
    private String password;

    private String realName;
    private String studentNo;
    private String phone;
    private String email;
}