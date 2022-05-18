package com.health.dao;

import com.health.pojo.Menu;
import com.health.pojo.Permission;
import com.health.pojo.Role;

import java.util.LinkedHashSet;
import java.util.Set;

//角色服务的数据层接口
public interface RoleDao {

    //根据用户id查询角色
    Set<Role> findByUserId(Integer userId);

    //根据角色id查询菜单
    LinkedHashSet<Menu> findMenuByRoleId(Integer roleId);


}
