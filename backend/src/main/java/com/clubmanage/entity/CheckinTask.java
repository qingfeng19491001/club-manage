package com.clubmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("checkin_tasks")
public class CheckinTask {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long clubId;
    private String title;
    private String description;
    private String locationName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer radiusMeters;
    private String startTime;
    private String endTime;
    private Long createdBy;
    private String createdAt;
    private String updatedAt;
    @TableLogic
    private Integer deleted;
}