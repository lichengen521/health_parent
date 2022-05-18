package com.health.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.health.pojo.Menu;
import com.health.pojo.Permission;
import com.health.pojo.Role;
import com.health.pojo.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class SpringSecurityUserService implements UserDetailsService {
    //远程注入服务
    @Reference
    private UserService userService;

    //根据用户命查询数据库 获取用户信息
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //调用用户服务查询用户信息
        User user = userService.findByUsername(s);

        if (user == null) {
            //说明用户名不存在
            return null;
        }

        //用户名存在
        //创建权限集合
        List<GrantedAuthority> list = new ArrayList();
        //获取角色集合
        Set<Role> roles = user.getRoles();
        //角色集合遍历 为用户授予角色
        for (Role role : roles) {
            list.add(new SimpleGrantedAuthority(role.getKeyword()));
            Set<Permission> permissions = role.getPermissions();
            //遍历权限集合 为用户授权
            for (Permission permission : permissions) {
                list.add(new SimpleGrantedAuthority(permission.getKeyword()));
            }
            //遍历权限集合 为用户授予菜单
            /*LinkedHashSet<Menu> menuLinkedHashSet = role.getMenus();
            for(Menu menu:menuLinkedHashSet) {
                list.add(new SimpleGrantedAuthority(menu.))
            }*/
        }

        //为用户授权
        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        s, user.getPassword(), list);
        return springUser;
    }
}
