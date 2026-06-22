package com.clubmanage.dto.club;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApproveClubRequest {

    /** true=通过 false=驳回 */
    @NotNull
    private Boolean approved;

    private String rejectReason;
}