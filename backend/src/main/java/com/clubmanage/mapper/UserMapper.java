package com.clubmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clubmanage.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}