package com.clubmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("members")
public class Member {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long clubId;
    private Long userId;
    private Integer role;
    private Integer status;
    private String applyReason;
    private String rejectReason;
    private String joinedAt;
    private String createdAt;
    private String updatedAt;
    @TableLogic
    private Integer deleted;
}