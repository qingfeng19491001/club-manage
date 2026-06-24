package com.clubmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableField;
import com.clubmanage.handler.LocalDateTimeAsStringTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = LocalDateTimeAsStringTypeHandler.class)
    private LocalDateTime createdAt;
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = LocalDateTimeAsStringTypeHandler.class)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}