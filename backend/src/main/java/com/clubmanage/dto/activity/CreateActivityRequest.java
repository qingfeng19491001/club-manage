package com.clubmanage.dto.activity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateActivityRequest {

    @NotNull(message = "缁€鎯ф礋ID娑撳秷鍏樻稉铏光敄")
    private Long clubId;

    @NotBlank(message = "濞茶濮╅弽鍥暯娑撳秷鍏樻稉铏光敄")
    @Size(max = 256)
    private String title;

    private String description;
    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;

    @NotNull(message = "瀵偓婵妞傞梻缈犵瑝閼虫垝璐熺粚?)
    private String startTime;

    @NotNull(message = "缂佹挻娼弮鍫曟？娑撳秷鍏樻稉铏光敄")
    private String endTime;

    /** 0 = unlimited */
    private Integer maxParticipants = 0;
    private String coverUrl;
}