package com.health.service;

import com.health.pojo.User;

public interface UserService {

    //根据用户名查询用户信息
    User findByUsername(String username);
}
