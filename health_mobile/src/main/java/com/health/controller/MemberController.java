package com.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.health.constant.MessageConstant;
import com.health.constant.RedisMessageConstant;
import com.health.entity.Result;
import com.health.pojo.Member;
import com.health.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private MemberService memberService;

    @RequestMapping("/login")
    public Result login(HttpServletResponse response, @RequestBody Map map) {
        /*
        1、校验用户输入的短信验证码是否正确，如果验证码错误则登录失败
        2、如果验证码正确，则判断当前用户是否为会员，如果不是会员则自动完成会员注册
        3、向客户端写入Cookie，内容为用户手机号
        4、将会员信息保存到Redis，使用手机号作为key，保存时长为30分钟
         */
        String telephone = (String) map.get("telephone");
        String validateCode = (String) map.get("validateCode");
        String s = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);

        if (s != null && validateCode != null && s.equals(validateCode)) {
            //验证码输入对比成功
            Member member = memberService.memberByTelephone(telephone);

            //判断会员是否存在 如果不存在那么自动注册会员
            if (member == null) {
                //如果不存在必须重新创建一个新的会员去注册
                member = new Member();
                member.setPhoneNumber(telephone);//存入手机号
                member.setRegTime(new Date());//存入创建时间
                //证明不是会员 自动完成注册
                memberService.add(member);
            }
            //如果是会员 向客户端写入cookie
            Cookie cookie = new Cookie("login_member_telephone", telephone);//创建cookie
            cookie.setMaxAge(60 * 60 * 24 * 30);//30天
            cookie.setPath("/");

            //将cookie返回给客户端
            response.addCookie(cookie);

            //redis存储会员信息 首先将会员信息转成json
            String json = JSON.toJSON(member).toString();
            jedisPool.getResource().setex(telephone, 1800, json);

            return new Result(true, MessageConstant.LOGIN_SUCCESS);
        } else {
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }

    }

}
