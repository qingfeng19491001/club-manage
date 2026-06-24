package com.clubmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.clubmanage.handler.LocalDateTimeAsStringTypeHandler;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String passwordHash;
    private String realName;
    private String studentNo;
    private String phone;
    private String email;
    private String avatarUrl;
    /** 0 member 1 leader 2 admin */
    private Integer role;
    private Integer status;

    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = LocalDateTimeAsStringTypeHandler.class)
    private LocalDateTime createdAt;

    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = LocalDateTimeAsStringTypeHandler.class)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}