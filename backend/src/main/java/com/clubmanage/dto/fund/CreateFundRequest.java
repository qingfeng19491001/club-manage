package com.clubmanage.dto.fund;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateFundRequest {

    @NotNull(message = "社团ID不能为空")
    private Long clubId;

    @NotBlank(message = "标题不能为空")
    @Size(max = 256)
    private String title;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    /** 1 income 2 expense */
    @NotNull(message = "类型不能为空")
    private Integer type;

    private String description;
}