package com.health.service;

import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.entity.Result;
import com.health.pojo.CheckItem;

import java.util.List;

public interface CheckItemService {
    //创建公共模块接口中的添加检查项的接口
    void add(CheckItem checkItem);

    //查新检查项列表的接口
    PageResult findPage(QueryPageBean queryPageBean);

    //根据id删除检查项
    void delCheckItemById(int id);

    //根据id查询检查项
    CheckItem findCheckItemById(Integer id);

    //编辑检查项
    void edit(CheckItem checkItem);

    //查询所有检查项的方法
    List<CheckItem> findAll();
}
