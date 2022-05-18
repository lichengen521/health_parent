package com.health.controller;

//处理体检预约的controller

import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.constant.RedisMessageConstant;
import com.health.entity.Result;
import com.health.pojo.Order;
import com.health.service.OrderService;
import com.health.service.SetmealService;
import com.health.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @Autowired
    private JedisPool jedisPool;


    @RequestMapping("/submit")
    public Result submit(@RequestBody Map map) {
        //从redis获取保存的验证码 比对参数中的验证码
        String telephone = (String) map.get("telephone");
        String redisCode = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        //前端参数传回的验证码
        String validateCode = (String) map.get("validateCode");

        //对比验证码
        if (validateCode != null && redisCode != null && validateCode.equals(redisCode)) {
            //比对成功 完成预约业务处理
            map.put("orderStatus", Order.ORDERTYPE_WEIXIN);//设置预约类型 微信预约/电话预约
            map.put("orderType",Order.ORDERSTATUS_NO);//设置预约状态
            Result result = null;
            try {
                //远程调用服务进行体检预约
                result = orderService.order(map);
            } catch (Exception e) {
                e.printStackTrace();
                return result;
            }
            if (result.isFlag()) {
                try {
                    //预约成功就删除在redis中存储的验证码 防止再次使用
                    Jedis resource = jedisPool.getResource();
                    //删除在redis中存储的验证码
                    resource.del(telephone + RedisMessageConstant.SENDTYPE_ORDER);
                    //预约成功可以给用户发送短信 预约成功
//                    SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE,telephone,map.get("orderDate").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        } else {
            //对比失败 返回结果给页面
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
    }

    //根据id查询预约信息
    @RequestMapping("/findById")
    public Result findById(Integer id) {
        try {
            Map orderSuccess = orderService.findById(id);
            return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,orderSuccess);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_ORDER_FAIL);
        }
    }
}
