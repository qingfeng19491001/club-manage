package com.clubmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("activities")
public class Activity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long clubId;
    private String title;
    private String description;
    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxParticipants;
    private Integer registeredCount;
    private Integer status;
    private String coverUrl;
    private String recap;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}