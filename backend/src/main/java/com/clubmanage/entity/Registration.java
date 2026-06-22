package com.clubmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("registrations")
public class Registration {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Long userId;
    private Integer status;
    private LocalDateTime checkedInAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}