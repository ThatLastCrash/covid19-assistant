package com.nnu.chatrobot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nnu.chatrobot.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User>{
}
