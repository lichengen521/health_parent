package com.health.dao;

import com.health.pojo.Permission;

import java.util.Set;

//权限服务所需数据层接口
public interface PermissionDao {

    //根据角色id查询权限
    Set<Permission> findByRoleId(Integer roleId);
}
