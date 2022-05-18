package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.health.dao.CheckItemDao;
import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.pojo.CheckItem;
import com.health.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//interfaceClass 代表如果在dubbo框架中使用Transactional注解需要告知对那个服务进行事务处理
@Service(interfaceClass = CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    private CheckItemDao checkItemDao;

    /**
     * 新增检查组
     * @param checkItem
     */
    @Override
    public void add(CheckItem checkItem) {
        //调用dao层的对象执行添加检查项的方法
        checkItemDao.add(checkItem);
    }

    /**
     * 检查项分组查询
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        //获取当前页面大小
        Integer pageSize = queryPageBean.getPageSize();
        //获取当前页码
        Integer currentPage = queryPageBean.getCurrentPage();
        //获取查询条件
        String queryString = queryPageBean.getQueryString();

        //完成分页查询是基于mybatis框架的分页助手插件
        PageHelper.startPage(currentPage,pageSize);
        //查询数据
        Page<CheckItem> page = checkItemDao.findPage(queryString);

        //根据page对象中统计的属性获取总条数
        long total = page.getTotal();

        //根据page对象中统计的属性获取数据集合
        List<CheckItem> result = page.getResult();


        return new PageResult(total,result);
    }

    /**
     * 删除检查项
     * @param id
     */
    @Override
    public void delCheckItemById(int id) {
        //调用查项是否被被检查组关联方法
        long count = checkItemDao.findCountByCheckItemId(id);
        //判断当前检查项被检查组关联了
        if(count>0) {//说明该检查项已经被关联 无法被删除
            new RuntimeException();
        }

        checkItemDao.delCheckItemById(id);

    }

    /**
     * 根据id查询检查项
     * @param id
     * @return
     */
    @Override
    public CheckItem findCheckItemById(Integer id) {
        CheckItem checkItem = checkItemDao.findCheckItemById(id);
        return checkItem;
    }

    /**
     * 编辑检查项方法
     * @param checkItem
     */
    @Override
    public void edit(CheckItem checkItem) {

        checkItemDao.edit(checkItem);
    }

    @Override
    public List<CheckItem> findAll() {

        List<CheckItem> all = checkItemDao.findAll();
        return all;
    }


}
