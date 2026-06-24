package com.clubmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("funds")
public class Fund {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long clubId;
    private String title;
    private BigDecimal amount;
    private Integer type;
    private String description;
    private Integer status;
    private Long applicantId;
    private Long approverId;
    private String rejectReason;
    private String createdAt;
    private String updatedAt;
    @TableLogic
    private Integer deleted;
}