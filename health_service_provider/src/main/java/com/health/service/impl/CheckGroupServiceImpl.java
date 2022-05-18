package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.health.dao.CheckGroupDao;
import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.pojo.CheckGroup;
import com.health.pojo.CheckItem;
import com.health.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//interfaceClass 代表如果在dubbo框架中使用Transactional注解需要告知对那个服务进行事务处理
@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {

    @Autowired
    private CheckGroupDao checkGroupDao;

    //添加检查组方法
    @Override
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        //新增检查组
        checkGroupDao.add(checkGroup);
        //设置检查组和检查项的关系
        Integer id = checkGroup.getId();
        System.out.println(id);
        System.out.println(Arrays.toString(checkitemIds));
        if(checkitemIds != null && checkitemIds.length>0){
            for(Integer checkitemId : checkitemIds){
                Map<String,Integer> map = new HashMap<>();
                map.put("checkgroupId",id);
                map.put("checkitemId",checkitemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }

    }

    //查询检查组分页数据
    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        //获取queryPageBean对象中的数据
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        //创建分页助手对象
        PageHelper.startPage(currentPage,pageSize);

        //调用dao层的方法
        Page<CheckGroup> checkGroupList = checkGroupDao.findPage(queryString);

        //获取数据列表中的总数
        long total = checkGroupList.getTotal();

        return new PageResult(total,checkGroupList);
    }

    //编辑检查组查询检查组信息
    @Override
    public CheckGroup findCheckGroupById(Integer id) {
        CheckGroup checkGroup = checkGroupDao.findCheckGroupById(id);
        return checkGroup;
    }

    //查询检查组下关联的检查项
    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        List<Integer> checkItemList= checkGroupDao.findCheckItemIdsByCheckGroupId(id);
        return checkItemList;
    }

    //编辑检查组方法
    @Override
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {

        //编辑检查组
        checkGroupDao.edit(checkGroup);

        //获取要编辑的检查组id
        Integer id = checkGroup.getId();

        //在保存检查组信息是先清除原有的旧信息
        checkGroupDao.delCheckGroupAndCheckItem(id);

        //添加编辑检查组的检查项信息
        if(checkitemIds != null && checkitemIds.length>0){
            for(Integer checkitemId : checkitemIds){
                Map<String,Integer> map = new HashMap<>();
                map.put("checkgroupId",id);
                map.put("checkitemId",checkitemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }

    //查询所有检查组的方法
    @Override
    public List<CheckGroup> findAll() {
        List<CheckGroup> all = checkGroupDao.findAll();
        return all;
    }

    //根据id删除检查组方法
    @Override
    public void delCheckGroupById(Integer id) {
        //删除检查组前先删除检查组与检查项方法
        checkGroupDao.delCheckGroupAndCheckItem(id);

        //删除检查组
        checkGroupDao.del(id);
    }

}
