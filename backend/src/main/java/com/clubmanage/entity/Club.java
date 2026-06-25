package com.clubmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("clubs")
public class Club {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String category;
    private String logoUrl;
    private Long founderId;
    /** 0 pending 1 approved 2 rejected 3 disbanded */
    private Integer status;
    private String rejectReason;
    private Integer memberCount;
    private String createdAt;
    private String updatedAt;
    @TableLogic
    private Integer deleted;
}