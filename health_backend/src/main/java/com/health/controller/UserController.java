package com.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.entity.Result;
import com.health.service.SpringSecurityUserService;
import com.health.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//用户相关操作
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SpringSecurityUserService service;

    @Reference
    private UserService userService;

    @RequestMapping("/getUsername")
    public Result getUsername(){
        //当springsecurity 完成认证后 会将用户名保存在框架提供的上下文对象中 底层基于session会话
        //通过上下文对象拿去认证信息对象 再通过认证信息对象拿到User对象 框架提供的user对象
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.getUsername();


        if (username != null) {
            return new Result(true, MessageConstant.GET_USERNAME_SUCCESS,username);
        }
        return new Result(false, MessageConstant.GET_USERNAME_FAIL);
    }

//    @RequestMapping("/getUserMenu")
//    public Result getUserMenu(){
//        //当springsecurity 完成认证后 会将用户名保存在框架提供的上下文对象中 底层基于session会话
//        //通过上下文对象拿去认证信息对象 再通过认证信息对象拿到User对象 框架提供的user对象
//        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username = principal.getUsername();
//
//        if (username != null) {
//            com.health.pojo.User byUsername = userService.findByUsername(username);
//
//            return new Result(true, MessageConstant.GET_USERNAME_SUCCESS,username);
//        }
//        return new Result(false, MessageConstant.GET_USERNAME_FAIL);
//    }
}
