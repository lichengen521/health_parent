package com.health.dao;

import com.health.pojo.User;

//用户服务数据持久层
public interface UserDao {

    User findByUsername(String username);

}
