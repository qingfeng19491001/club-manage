package com.clubmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clubmanage.entity.Club;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClubMapper extends BaseMapper<Club> {
}