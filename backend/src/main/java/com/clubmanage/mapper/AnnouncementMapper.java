package com.clubmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clubmanage.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}