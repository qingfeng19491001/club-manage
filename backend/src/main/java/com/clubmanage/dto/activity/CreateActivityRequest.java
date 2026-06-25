package com.clubmanage.dto.activity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateActivityRequest {

    @NotNull(message = "社团ID不能为空")
    private Long clubId;

    @NotBlank(message = "活动标题不能为空")
    @Size(max = 256)
    private String title;

    private String description;
    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;

    @NotNull(message = "开始时间不能为空")
    private String startTime;

    @NotNull(message = "结束时间不能为空")
    private String endTime;

    /** 0 = unlimited */
    private Integer maxParticipants = 0;
    private String coverUrl;
}
