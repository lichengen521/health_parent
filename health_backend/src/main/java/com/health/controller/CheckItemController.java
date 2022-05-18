package com.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.entity.Result;
import com.health.pojo.CheckItem;
import com.health.service.CheckItemService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
检查项管理
 */
//@Controller
@RestController//代表ResponseBody + Controller
@RequestMapping("/checkItem")
public class CheckItemController {

    @Reference//去zookeeper中查找名为checkItemService的服务
    private CheckItemService checkItemService;

    @RequestMapping("/add")
    public Result add(@RequestBody CheckItem checkItem) {

        try{//通过try 。。。catch的方法来实现 查看新增检查项是否成功
            checkItemService.add(checkItem);

        }catch (Exception e) {
            e.printStackTrace();
            //服务调用失败 设置失败返回信息对象
            return new Result(false, MessageConstant.ADD_CHECKITEM_FAIL);
        }

        return new Result(true, MessageConstant.ADD_CHECKITEM_SUCCESS);
    }

    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {

        //调用服务中的查询方法
        PageResult page = checkItemService.findPage(queryPageBean);


        return page;
    }

    @PreAuthorize("hasAnyAuthority('CHECKITEM_DELETE')")//为删除checkitem方法赋予权限
    @RequestMapping("/delCheckItemById")
    public Result delCheckItemById(Integer id) {

        try{//通过try 。。。catch的方法来实现 查看删除检查项是否成功
            //调用服务中的删除检查项方法
            checkItemService.delCheckItemById(id);
            return new Result(true,MessageConstant.DELETE_CHECKITEM_SUCCESS);
        }catch (Exception e) {
            e.printStackTrace();
            //服务调用失败 设置失败返回信息对象
            return new Result(false, MessageConstant.DELETE_CHECKITEM_FAIL);
        }
    }

    @RequestMapping("/findCheckItemById")
    public Result findCheckItemById(Integer id) {

        try{//通过try 。。。catch的方法来实现 查看删除检查项是否成功
            //调用服务中的删除检查项方法
            CheckItem checkItem = checkItemService.findCheckItemById(id);
            return new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItem);
        }catch (Exception e) {
            e.printStackTrace();
            //服务调用失败 设置失败返回信息对象
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }

    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckItem checkItem) {

        try{//通过try 。。。catch的方法来实现 查看删除检查项是否成功
            //调用服务中的删除检查项方法
            checkItemService.edit(checkItem);
            return new Result(true,MessageConstant.EDIT_CHECKITEM_SUCCESS);
        }catch (Exception e) {
            e.printStackTrace();
            //服务调用失败 设置失败返回信息对象
            return new Result(false, MessageConstant.EDIT_CHECKITEM_FAIL);
        }
    }

    @RequestMapping("/findAll")
    public Result findAll() {

        try{//通过try 。。。catch的方法来实现 查看删除检查项是否成功
            //调用服务中的删除检查项方法
            List<CheckItem> checkItemList =  checkItemService.findAll();
            return new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItemList);
        }catch (Exception e) {
            e.printStackTrace();
            //服务调用失败 设置失败返回信息对象
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }
}
