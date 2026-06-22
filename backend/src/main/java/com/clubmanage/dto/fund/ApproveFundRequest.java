package com.clubmanage.dto.fund;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApproveFundRequest {

    @jakarta.validation.constraints.NotNull(message = "审批结果不能为空")
    private Boolean approved;

    @Size(max = 512)
    private String rejectReason;
}