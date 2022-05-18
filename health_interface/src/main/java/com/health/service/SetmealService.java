package com.health.service;

import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.pojo.CheckGroup;
import com.health.pojo.Setmeal;

import java.util.List;
import java.util.Map;


public interface SetmealService {

    //新增套餐方法
    void add(Setmeal setmeal,Integer[] checkgroupIds);

    //套餐分页查询方法
    PageResult findPage(QueryPageBean queryPageBean);

    //移动端查询所有体检套餐方法
    List<Setmeal> fandAll();

    //移动端查询体检套餐的方法
    Setmeal findById(Integer id);

    //编辑套餐时根据查询套餐id查询数据回显方法
    Setmeal findSetmealById(Integer id);

    //根据setmealid查询该套餐下的检查组信息
    List<Integer> findCheckGroupIdsBySetMealId(Integer id);

    //web编辑套餐方法
    void edit(Setmeal setmeal, Integer[] checkgroupIds);

    //web删除套餐方法
    void delSetMealById(Integer id);

    //查询饼型图数据
    List<Map<String,Object>> getSetmealReport();
}
