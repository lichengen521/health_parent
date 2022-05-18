package com.health.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.constant.RedisConstant;
import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.entity.Result;
import com.health.pojo.CheckGroup;
import com.health.pojo.Setmeal;
import com.health.service.SetmealService;
import com.health.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    //远程注入服务
    @Reference
    private SetmealService setmealService;

    //使用jedisPool来操作redis
    @Autowired
    private JedisPool jedisPool;

    /**
     * 上传图片方法
     * @param imgFile
     * @return
     */
    @RequestMapping("/upload")
    public Result upload(@RequestParam(value = "imgFile") MultipartFile imgFile){

        //获取文件后缀
        String originalFilename = imgFile.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");

        String extention = originalFilename.substring(i);

        //重新产生一个文件名称（随机）
        String fileName = UUID.randomUUID().toString() + extention;
        try {
            //调用七牛云工具类将文件上传至云服务器
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),fileName);

            //文件上传后操作redis存储文件名
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);

        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }

        //上传成功
        return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS,fileName);

    }

    /**
     * 新增套餐方法
     * @param setmeal
     * @param checkgroupIds
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Setmeal setmeal, Integer[] checkgroupIds){

        try {
            //调用service服务添加套餐方法
            setmealService.add(setmeal,checkgroupIds);

        } catch (Exception e) {
            //新增失败
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_SETMEAL_FAIL);
        }

        //上传成功
        return new Result(true, MessageConstant.ADD_SETMEAL_SUCCESS);

    }

    /**
     * 套餐的分页查询方法
     * @param queryPageBean
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){

        //通过try 。。。catch的方法来实现 查看新增检查项是否成功
        PageResult pageResult = setmealService.findPage(queryPageBean);
        return pageResult;
    }


    /**
     * 通过id查询套餐方法
     * @param id
     * @return
     */
    @RequestMapping("/findSetmealById")
    public Result findSetmealById(Integer id){
        try{
            //通过try 。。。catch的方法来实现 查看新增检查项是否成功
            Setmeal setmealById = setmealService.findSetmealById(id);
            return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmealById);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }

    /**
     * 根据setmealid查询改套餐下所有检查组信息
     * @param id
     * @return
     */
    @RequestMapping("/findCheckGroupIdsBySetMealId")
    public Result findCheckGroupIdsBySetMealId(Integer id) {
        try {
            //获取的时检查组id 用于页面回显数据
            List<Integer> checkGroupIdsBySetMealId = setmealService.findCheckGroupIdsBySetMealId(id);
            return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroupIdsBySetMealId);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
    }

    /**
     * web编辑套餐方法
     * @param setmeal
     * @param checkgroupIds
     * @return
     */
    @RequestMapping("/edit")
    public Result edit(@RequestBody Setmeal setmeal,Integer[] checkgroupIds) {
        try {
            setmealService.edit(setmeal,checkgroupIds);
            return new Result(true,MessageConstant.EDIT_SETMEAL_SUCCESS);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.EDIT_SETMEAL_FAIL);
        }
    }

    /**
     * 根据id删除该套餐
     * @param id
     * @return
     */
    @RequestMapping("/delSetMealById")
    public Result delSetMealById(Integer id) {
        try{
            setmealService.delSetMealById(id);
            return new Result(true,MessageConstant.DELETE_SETMEAL_SUCCESS);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.DELETE_SETMEAL_FAIL);
        }
    }
}
