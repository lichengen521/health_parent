package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.health.dao.PermissionDao;
import com.health.dao.RoleDao;
import com.health.dao.UserDao;
import com.health.pojo.Menu;
import com.health.pojo.Permission;
import com.health.pojo.Role;
import com.health.pojo.User;
import com.health.service.UserService;
import org.aspectj.lang.annotation.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;

@Service(interfaceClass = UserService.class)
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;

    @Override
    public User findByUsername(String username) {
        //根据用户名查询数据库获取的用户信息以及关联的角色信息 同时还要查询角色关联的权限信息
        User user = userDao.findByUsername(username);//查询用户基本信息
        if (user == null) {
            return null;
        }

        //获取用户id
        Integer userId = user.getId();
        //根据用户id查询角色
        Set<Role> roles = roleDao.findByUserId(userId);

        //根据角色查询所关联的权限
        for (Role role : roles) {
            //获取角色id
            Integer roleId = role.getId();
            //根据角色id查询该角色所需要的权限集合
            Set<Permission> permissions = permissionDao.findByRoleId(roleId);
            //将该权限集合赋予该角色
            role.setPermissions(permissions);
           /* //将角色中的菜单赋给角色
            LinkedHashSet<Menu> menuByRoleId = roleDao.findMenuByRoleId(role.getId());
            role.setMenus(menuByRoleId);*/
        }

        //将查询到的用户集合赋给用户
        user.setRoles(roles);//用户关联角色

        return user;
    }
}
