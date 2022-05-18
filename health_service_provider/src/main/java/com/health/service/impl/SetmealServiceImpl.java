package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.health.constant.RedisConstant;
import com.health.dao.SetMealDao;
import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.pojo.CheckGroup;
import com.health.pojo.Setmeal;
import com.health.service.SetmealService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService{

    //注入dao层对象
    @Autowired
    private SetMealDao setMealDao;

    //注入jedis连接池对象
    @Autowired
    private JedisPool jedisPool;

    //注入freemarker静态页面生成对象
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    //从properties中获取属性值 -- 静态页面的生成目录
    @Value("${out_put_path}")
    private String outPutPath;
    //新增套餐方法 同时关联检查组
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {

        //调用添加套餐基础参数方法
        setMealDao.add(setmeal);

        //接收数据库新增套餐的id
        Integer id = setmeal.getId();

        //调用本类方法存储检查组关系
        this.setSetmealAndCheckGroup(id,checkgroupIds);

        //保存完套餐信息后操作redis存储改文件名
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());

        //添加套餐后 需要生成相对应的静态页面（套餐列表页面、多个套餐详情页面）
        generateMobileStaticHtml();

    }

    //生成当前方法所需的静态页面
    public void generateMobileStaticHtml(){
        //在生成静态页面之前 需要查询数据
        List<Setmeal> setmeals = setMealDao.findAll();

        //需要生成套餐列表静态页面
        generateMobileSetmealListHtml(setmeals);

        //需要生成套餐详情静态页面
        generateMobileSetmealDetailHtml(setmeals);
    }

    //生成套餐列表静态页面方法
    public void generateMobileSetmealListHtml(List<Setmeal> list) {
        //封装所需数据
        Map map = new HashMap();
        //w为模板生成所需要的数据
        map.put("setmealList",list);
        //调用通用方法生成静态页面
        generateHtml("mobile_setmeal.ftl","m_setmeal.html",map);
    }

    //生成套餐详情静态页面
    public void generateMobileSetmealDetailHtml(List<Setmeal> list){
        //遍历集合
        for (Setmeal setmeal : list) {
            //封装所需数据
            Map map = new HashMap();
            map.put("setmeal",setMealDao.findById(setmeal.getId()));
            //生成静态页面
            generateHtml("mobile_setmeal_detail.ftl","setmeal_detail_" + setmeal.getId() + ".html",map);
        }
    }

    //生成静态页面方法 --通用方法(添加和编辑调用的方法)
    public void generateHtml(String templateName,String htmlName,Map map) {
        //获取配置对象
        Configuration configuration = freeMarkerConfigurer.getConfiguration();

        Writer out = null;
        try {
            //加载模板名称 获取模板对象
            Template template = configuration.getTemplate(templateName);
            //创建输出流
            out = new FileWriter(new File(outPutPath + "/" + htmlName));

            //输出文件
            template.process(map,out);
            //关闭输出流
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }



    //插入套餐时将关联的检查组信息存储的方法
    public void setSetmealAndCheckGroup(Integer id,Integer[] checkgroupIds){
        //判断数组是否有数据
        if(checkgroupIds != null && checkgroupIds.length>0) {
            //遍历检查组数组
            for(Integer i:checkgroupIds) {
                //创建map集合用来传参对应关系
                Map<String,Integer> map = new HashMap<>();
                map.put("setmeal_id",id);
                map.put("checkgroup_id",i);
                //存储套餐与检查组的关系集合
                setMealDao.setSetmealAndCheckGroup(map);
            }
        }
    }

    //套餐分页查询方法
    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        //进入页面时 默认生成静态页面
        generateMobileStaticHtml();
        //获取queryPageBean对象中的数据
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        //创建分页助手对象
        PageHelper.startPage(currentPage,pageSize);

        //调用dao层的方法
        Page<CheckGroup> setMealList = setMealDao.findPage(queryString);

        //获取数据列表中的总数
        long total = setMealList.getTotal();

        return new PageResult(total,setMealList);
    }

    @Override
    public List<Setmeal> fandAll() {

        //dao层调用查询所有体检套餐方法
        List<Setmeal> all = setMealDao.findAll();
        return all;
    }

    //c端查询体检套餐的方法
    @Override
    public Setmeal findById(Integer id) {
        //dao层查询体检套餐的方法
        Setmeal byId = setMealDao.findById(id);
        return byId;
    }

    //web编辑套餐时根据查询套餐id查询数据回显方法
    @Override
    public Setmeal findSetmealById(Integer id) {
        Setmeal setmealById = setMealDao.findSetmealById(id);
        return setmealById;
    }

    //根据套餐id查询该套餐下检查组信息
    @Override
    public List<Integer> findCheckGroupIdsBySetMealId(Integer id) {
        List<Integer> checkGroupIdsBySetMealId = setMealDao.findCheckGroupIdsBySetMealId(id);
        return checkGroupIdsBySetMealId;
    }

    //编辑套餐方法
    @Override
    public void edit(Setmeal setmeal, Integer[] checkgroupIds) {
        //获取被编辑的套餐id
        Integer id = setmeal.getId();

        //获取当前要编辑的套餐图片名称
        Setmeal setmealById = setMealDao.findSetmealById(id);

        //当前套餐已存图片名称
        String img = setmealById.getImg();


        //判断是否更改图片
        if (!setmeal.getImg().equals(img)) {
            //保存完套餐信息后操作redis存储改文件名
            jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_DB_RESOURCES,img);

            //然后在保存新更改的图片名称
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());
        }

        //首先先更改所要编辑的套餐
        setMealDao.edit(setmeal);

        //删除原有的套餐与检查组的关联
        setMealDao.delSetmealAndCheckGroup(id);

        //删除原有逻辑后保存编辑后的套餐与检查组的关联关系
        this.setSetmealAndCheckGroup(id,checkgroupIds);

        //编辑套餐后 需要刷新相对应的静态页面（套餐列表页面、多个套餐详情页面）
        generateMobileStaticHtml();
    }

    //根据id删除套餐
    @Override
    public void delSetMealById(Integer id) {
        //删除套餐之前先删除原有与检查组的关联
        setMealDao.delSetmealAndCheckGroup(id);

        //获取当前要编辑的套餐图片名称
        Setmeal setmealById = setMealDao.findSetmealById(id);

        //当前套餐已存图片名称
        String img = setmealById.getImg();

        //根据id删除套餐
        setMealDao.del(id);

        //同时删除redis中的缓存图片
        //保存完套餐信息后操作redis存储改文件名
        jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_DB_RESOURCES,img);

        try {
            //删除套餐后要刷新列表套静态页面
            generateMobileStaticHtml();
            //删除套餐后 需要删除之前被删除的静态页面
            File file = new File(outPutPath + "/" + "setmeal_detail_" + id);

            //判断是否存在改套餐的详情静态页面
            if (file.exists() && file.isFile()) {
                file.delete();

            }else {
                return;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    //运营统计饼形图的数据
    @Override
    public List<Map<String, Object>> getSetmealReport() {

        List<Map<String, Object>> setmealReport = setMealDao.getSetmealReport();

        return setmealReport;
    }
}
