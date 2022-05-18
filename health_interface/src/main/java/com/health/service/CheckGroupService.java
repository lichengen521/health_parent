package com.health.service;

import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.pojo.CheckGroup;
import com.health.pojo.CheckItem;

import java.util.List;

public interface CheckGroupService {
    //创建公共模块接口中的添加检查项的接口
    void add(CheckGroup checkGroup,Integer[] checkitemIds);

    //查询检查组分页信息
    PageResult findPage(QueryPageBean queryPageBean);

    //编辑检查组查询检查组信息
    CheckGroup findCheckGroupById(Integer id);

    //查询检查组关联的检查项
    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    //编辑检查组方法
    void edit(CheckGroup checkGroup, Integer[] checkitemIds);

    //查询所有检查组的方法
    List<CheckGroup> findAll();

    //根据id删除检查组方法
    void delCheckGroupById(Integer id);


}
