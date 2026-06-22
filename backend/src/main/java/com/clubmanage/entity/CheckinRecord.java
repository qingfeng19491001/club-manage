package com.clubmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("checkin_records")
public class CheckinRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private Long userId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer status;
    private String appealReason;
    private String appealReply;
    private LocalDateTime checkedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}