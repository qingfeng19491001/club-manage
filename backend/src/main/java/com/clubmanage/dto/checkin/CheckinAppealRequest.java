package com.clubmanage.dto.checkin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CheckinAppealRequest {

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;

    @NotBlank(message = "申诉理由不能为空")
    @Size(max = 512)
    private String appealReason;
}