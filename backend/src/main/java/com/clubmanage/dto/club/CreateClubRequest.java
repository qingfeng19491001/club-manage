package com.clubmanage.dto.club;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateClubRequest {

    @NotBlank(message = "社团名称不能为空")
    @Size(max = 128)
    private String name;

    private String description;
    private String logoUrl;
}