package com.clubmanage.dto.club;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateClubRequest {

    @JsonAlias({"clubName", "club_name"})
    @NotBlank(message = "社团名称不能为空")
    @Size(max = 128)
    private String name;

    @Size(max = 64)
    private String category;

    private String description;
    private String logoUrl;
}