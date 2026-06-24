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
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = LocalDateTimeAsStringTypeHandler.class)
    private LocalDateTime startTime;
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = LocalDateTimeAsStringTypeHandler.class)
    private LocalDateTime endTime;
    private Integer maxParticipants;
    private Integer registeredCount;
    private Integer status;
    private String coverUrl;
    private String recap;
    private Long createdBy;
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = LocalDateTimeAsStringTypeHandler.class)
    private LocalDateTime createdAt;
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = LocalDateTimeAsStringTypeHandler.class)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}