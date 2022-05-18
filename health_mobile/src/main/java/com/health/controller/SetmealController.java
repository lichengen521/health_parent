package com.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.entity.Result;
import com.health.pojo.Setmeal;
import com.health.service.SetmealService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/c/setmeal")
public class SetmealController {

    @Reference
    private SetmealService setmealService;

    /**
     * 移动端查询所有体检套餐信息
     * @return
     */
    @RequestMapping("/getSetmeal")
    public Result getAllSetmeal() {
        try{
            List<Setmeal>list =  setmealService.fandAll();
            return new Result(true, MessageConstant.QUERY_SETMEALLIST_SUCCESS,list);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_SETMEALLIST_FAIL);
        }
    }

    /**
     * c端查询体检套餐详情方法
     * @param id
     * @return
     */
    @RequestMapping("/findById")
    public Result findById(Integer id) {
        try{
             Setmeal setmeal =  setmealService.findById(id);
            return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }

}
