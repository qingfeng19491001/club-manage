package com.clubmanage.dto.announcement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAnnouncementRequest {

    @NotNull(message = "社团ID不能为空")
    private Long clubId;

    @NotBlank(message = "标题不能为空")
    @Size(max = 256)
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private Boolean pinned;
}