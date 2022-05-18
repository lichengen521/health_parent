package com.health.dao;

import com.github.pagehelper.Page;
import com.health.pojo.CheckGroup;
import com.health.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetMealDao {

    //添加套餐持久层方法
    void add(Setmeal setmeal);

    //添加套餐-检查组关联关系方法
    void setSetmealAndCheckGroup(Map map);

    //查询套餐分页查询
    Page<CheckGroup> findPage(String queryString);

    //移动端查询体检套餐方法
    List<Setmeal> findAll();

    //移动端查询体检套餐详情方法
    Setmeal findById(Integer id);

    //web端根据id查询体检套餐的方法
    Setmeal findSetmealById(Integer id);

    //根据套餐id查询检查组id
    List<Integer> findCheckGroupIdsBySetMealId(Integer id);

    //web编辑套餐更改保存的方法
    void edit(Setmeal setmeal);

    //编辑套餐时删除原有与检查组的关系方法
    void delSetmealAndCheckGroup(Integer id);

    //根据id删除套餐
    void del(Integer id);

    //查询运营统计饼形图数据
    List<Map<String,Object>> getSetmealReport();

}
