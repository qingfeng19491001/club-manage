package com.clubmanage.dto.checkin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateCheckinTaskRequest {

    @NotNull(message = "社团ID不能为空")
    private Long clubId;

    @NotBlank(message = "任务标题不能为空")
    @Size(max = 256)
    private String title;

    private String description;
    private String locationName;

    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;

    private Integer radiusMeters;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
}