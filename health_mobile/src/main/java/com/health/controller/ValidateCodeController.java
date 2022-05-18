package com.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyuncs.exceptions.ClientException;
import com.health.constant.MessageConstant;
import com.health.constant.RedisMessageConstant;
import com.health.entity.Result;
import com.health.service.SetmealService;
import com.health.utils.SMSUtils;
import com.health.utils.ValidateCodeUtils;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

//验证码操作
@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    @Autowired
    private JedisPool jedisPool;

    //用户在线体检预约发送验证码
    @RequestMapping("/send4Order")
    public Result send4Order(String telephone) {
        //随机生成一个四位数的验证码
        Integer validateCode = ValidateCodeUtils.generateValidateCode(4);

        //调用aliyun的短信服务工具类
        try {
//            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,validateCode.toString());
            System.out.println("发送短息到:" + telephone + ",短信验证码为:" +validateCode);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        //将验证码缓存在redis中(5分钟)
        Jedis resource = jedisPool.getResource();

        //将发送的验证码保存到redis中指定保存的时间
        resource.setex(telephone + RedisMessageConstant.SENDTYPE_ORDER,300,validateCode.toString());

        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

    //手机快速登陆 访问方法
    @RequestMapping("/send4Login")
    public Result send4Login(String telephone) {
        //随机生成一个六位数的验证码
        Integer validateCode = ValidateCodeUtils.generateValidateCode(6);
        //调用aliyun的短信服务工具类
        try {
//            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,validateCode.toString());
            System.out.println("发送短息到:" + telephone + ",短信验证码为:" +validateCode);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        //将验证码缓存在redis中(5分钟)
        Jedis resource = jedisPool.getResource();

        //将发送的验证码保存到redis中指定保存的时间
        resource.setex(telephone + RedisMessageConstant.SENDTYPE_LOGIN,300,validateCode.toString());

        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }
}
