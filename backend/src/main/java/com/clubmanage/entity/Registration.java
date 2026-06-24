package com.clubmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableField;
import com.clubmanage.handler.LocalDateTimeAsStringTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

@Data
@TableName("registrations")
public class Registration {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Long userId;
    private Integer status;
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = LocalDateTimeAsStringTypeHandler.class)
    private LocalDateTime checkedInAt;
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = LocalDateTimeAsStringTypeHandler.class)
    private LocalDateTime createdAt;
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = LocalDateTimeAsStringTypeHandler.class)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}