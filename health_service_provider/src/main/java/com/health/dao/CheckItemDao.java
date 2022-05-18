package com.health.dao;

import com.github.pagehelper.Page;
import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.pojo.CheckItem;

import java.util.List;

public interface CheckItemDao {

    //添加检查项dao层方法
    void add(CheckItem checkItem);

    //查询检查项dao层方法 Page类型是pageHelper定义好的类型
    Page<CheckItem> findPage(String value);

    //根据检查项id查询该检查项是否被检查组所关联
    long findCountByCheckItemId(Integer id);

    //根据id删除检查项
    void delCheckItemById(Integer id);

    //根据id查询检查项
    CheckItem findCheckItemById(Integer id);

    //编辑检查项
    void edit(CheckItem checkItem);

    //查询所有检查项的方法
    List<CheckItem> findAll();
}
