package com.clubmanage.dto.club;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewMemberRequest {

    @NotNull
    private Boolean approved;

    private String rejectReason;
}