package com.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.entity.Result;
import com.health.pojo.CheckGroup;
import com.health.pojo.CheckItem;
import com.health.service.CheckGroupService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 检查组管理
 */

@RestController
@RequestMapping("/checkGroup")
public class CheckGroupController {

    @Reference
    private CheckGroupService checkGroupService;

    /**
     * 添加检查组方法
     * @param checkGroup
     * @param checkitemIds
     * @return
     */
    @RequestMapping("/add")//接收一个对象以及一个数组
    public Result add(@RequestBody CheckGroup checkGroup,Integer[] checkitemIds) {

        try{//通过try 。。。catch的方法来实现 查看新增检查项是否成功
            checkGroupService.add(checkGroup,checkitemIds);
            return new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS);
        }catch (Exception e) {
            e.printStackTrace();
            //服务调用失败 设置失败返回信息对象
            return new Result(false, MessageConstant.ADD_CHECKGROUP_FAIL);
        }
    }

    /**
     * 查询检查组分页
     * @param queryPageBean
     * @return
     */
    @RequestMapping("/findPage")//接收一个对象以及一个数组
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {

        //通过try 。。。catch的方法来实现 查看新增检查项是否成功
        PageResult pageResult = checkGroupService.findPage(queryPageBean);
        return pageResult;

    }

    /**
     * 根据id查询检查组的详细信息
     * @param id
     * @return
     */
    @RequestMapping("/findCheckGroupById")//接收一个对象以及一个数组
    public Result findCheckGroupById(Integer id) {

        try{
            //通过try 。。。catch的方法来实现 查看查询检查组是否成功
            CheckGroup checkGroup = checkGroupService.findCheckGroupById(id);
            return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroup);
        }catch (Exception e) {
            e.printStackTrace();
            //服务调用失败 设置失败返回信息对象
            return new Result(false, MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
    }

    /**
     * 根据检查组id查询该检查组下关联的检查项
     * @param id
     * @return
     */
    @RequestMapping("/findCheckItemIdsByCheckGroupId")//接收一个对象以及一个数组
    public Result findCheckItemIdsByCheckGroupId(Integer id) {
        try{
            //通过try 。。。catch的方法来实现 查看查询检查组是否成功
            List<Integer> checkItemList = checkGroupService.findCheckItemIdsByCheckGroupId(id);
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItemList);
        }catch (Exception e) {
            e.printStackTrace();
            //服务调用失败 设置失败返回信息对象
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }

    /**
     * 编辑检查组方法
     * @param checkGroup
     * @param checkitemIds
     * @return
     */
    @RequestMapping("/edit")//接收一个对象以及一个数组
    public Result edit(@RequestBody CheckGroup checkGroup,Integer[] checkitemIds) {

        try{//通过try 。。。catch的方法来实现 查看新增检查项是否成功
            checkGroupService.edit(checkGroup,checkitemIds);
            return new Result(true, MessageConstant.EDIT_CHECKGROUP_SUCCESS);
        }catch (Exception e) {
            e.printStackTrace();
            //服务调用失败 设置失败返回信息对象
            return new Result(false, MessageConstant.EDIT_CHECKGROUP_FAIL);
        }
    }

    /**
     * 查询所有检查组的方法
     *
     * @return
     */
    @RequestMapping("/findAll")//接收一个对象以及一个数组
    public Result findAll() {

        try{//通过try 。。。catch的方法来实现 查看新增检查项是否成功
            List<CheckGroup> checkGroupList = checkGroupService.findAll();
            return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroupList);
        }catch (Exception e) {
            e.printStackTrace();
            //服务调用失败 设置失败返回信息对象
            return new Result(false, MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
    }

    @RequestMapping(value = "/delCheckGroupById",method = RequestMethod.GET)
    public Result delCheckGroupById(Integer id) {
        try{
            checkGroupService.delCheckGroupById(id);
            return new Result(true,MessageConstant.DELETE_CHECKGROUP_SUCCESS);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.DELETE_CHECKGROUP_FAIL);
        }
    }
}
