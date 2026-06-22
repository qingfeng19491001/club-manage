package com.clubmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clubmanage.entity.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper extends BaseMapper<Member> {
}