package com.nnu.chatrobot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nnu.chatrobot.entity.User;

public interface UserService extends IService<User> {
    User getUserByName(String username);


}
