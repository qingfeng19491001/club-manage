package com.clubmanage.dto.club;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateClubRequest {

    @JsonAlias({"clubName", "club_name"})
    @Size(max = 128)
    private String name;

    private String description;
    private String logoUrl;
}
