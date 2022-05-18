package com.health.dao;

import com.github.pagehelper.Page;
import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.pojo.CheckGroup;
import com.health.pojo.CheckItem;

import java.util.List;
import java.util.Map;

public interface CheckGroupDao {
    //添加检查项dao层方法
    void add(CheckGroup checkGroup);

    //设置检查组与检查项的关系
    void setCheckGroupAndCheckItem(Map map);

    //查询检查组分页数据
    Page<CheckGroup> findPage(String queryString);

    //编辑检查组查询检查组信息
    CheckGroup findCheckGroupById(Integer id);

    //根据检查组id查询该检查组下关联的检查项
    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    //编辑检查组保存基础信息的方法
    void edit(CheckGroup checkGroup);

    //在保存编辑检查组时清除之前的关联检查项信息
    void delCheckGroupAndCheckItem(Integer id);

    //查询所有检查组方法
    List<CheckGroup> findAll();

    //删除检查组方法
    void del(Integer id);
}
