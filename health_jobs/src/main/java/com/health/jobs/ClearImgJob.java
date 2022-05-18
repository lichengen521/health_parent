package com.health.jobs;

import com.health.constant.RedisConstant;
import com.health.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/*
自定义job 实现定时清理垃圾图片
 */
public class ClearImgJob {

    @Autowired
    private JedisPool jedisPool;

    public void clearImg() {
        //获取jedis对象操作redis数据库  jedis对象.sdiff计算两个集合的差值 前者为大集合 后者为小集合
        Set<String> smembers = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES,RedisConstant.SETMEAL_PIC_DB_RESOURCES);

        int i = 0;
        //遍历set集合
        //首先判断该差值集合是否为空
        if(smembers != null) {
            for (String img:smembers) {
                // 调用七牛云工具类删除图片的方法
                QiniuUtils.deleteFileFromQiniu(img);
                //删除图片后将缓存redis数据库中的缓存图片
                jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,img);

                //打印删除图片名称
                System.out.println("Job"+i+" is now delete images:"+img);
                i++;
            }
        }

    }
}
