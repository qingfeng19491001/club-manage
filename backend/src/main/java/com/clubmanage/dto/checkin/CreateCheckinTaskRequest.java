package com.clubmanage.dto.checkin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCheckinTaskRequest {

    @NotNull(message = "缁€鎯ф礋ID娑撳秷鍏樻稉铏光敄")
    private Long clubId;

    @NotBlank(message = "娴犺濮熼弽鍥暯娑撳秷鍏樻稉铏光敄")
    @Size(max = 256)
    private String title;

    private String description;
    private String locationName;

    @NotNull(message = "缁绢剙瀹虫稉宥堝厴娑撹櫣鈹?)
    private BigDecimal latitude;

    @NotNull(message = "缂佸繐瀹虫稉宥堝厴娑撹櫣鈹?)
    private BigDecimal longitude;

    private Integer radiusMeters;

    @NotNull(message = "瀵偓婵妞傞梻缈犵瑝閼虫垝璐熺粚?)
    private String startTime;

    @NotNull(message = "缂佹挻娼弮鍫曟？娑撳秷鍏樻稉铏光敄")
    private String endTime;
}