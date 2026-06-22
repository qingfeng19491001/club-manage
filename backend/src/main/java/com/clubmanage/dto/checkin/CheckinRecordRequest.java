package com.clubmanage.dto.checkin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CheckinRecordRequest {

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;
}