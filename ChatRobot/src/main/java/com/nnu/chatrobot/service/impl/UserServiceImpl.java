package com.nnu.chatrobot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nnu.chatrobot.entity.User;
import com.nnu.chatrobot.mapper.UserMapper;
import com.nnu.chatrobot.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getUserByName(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //查询名字为 Tom 的一条记录
        queryWrapper.eq("username",username);
        User user = baseMapper.selectOne(queryWrapper);
        return user;
    }
}
