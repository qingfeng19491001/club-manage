package com.clubmanage.dto.auth;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 64)
    private String realName;

    @Size(max = 32)
    private String studentNo;

    @Size(max = 20)
    private String phone;

    @Size(max = 128)
    private String email;

    @Size(max = 512)
    private String avatarUrl;
}