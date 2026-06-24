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
@TableName("messages")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Integer type;
    private Long refId;
    private Integer isRead;
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = LocalDateTimeAsStringTypeHandler.class)
    private LocalDateTime createdAt;
    @TableLogic
    private Integer deleted;
}