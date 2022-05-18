package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.health.dao.OrderSettingDao;
import com.health.pojo.OrderSetting;
import com.health.service.OrderSettingService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {
    @Autowired
    private OrderSettingDao orderSettingDao;

    //预约设置根据上传的excel数据进行存储
    @Override
    public void upload(List<OrderSetting> list) {
        //对上传时间数据进行查询 然后进行业务判断 如果已经设置 那么刷新设置 如果没有设置 那么直接插入设置
        if(list != null && list.size()>0) {
            for (OrderSetting orderSetting : list) {
                //判断当天是否有设置
                if(orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate())>0) {
                    //执行更新操作
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                }else {//没有设置执行插入操作
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }


    //查询当月的预约设置数据
    @Override
    public List<Map> getOrderSettingByMonth(String date) {//格式应该是yyyy-MM
        String begin = date + "-01";
        String end = date + "-31";
        Map<String,String> map = new HashMap<>();

        map.put("begin",begin);
        map.put("end",end);
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(map);

        List<Map> result = new ArrayList<>();

        if(list != null && list.size()>0) {
            for(OrderSetting orderSetting:list) {
                //创建map对象接受并转换参数
                Map<String,Object> mapObj = new HashMap<>();
                //orderSetting.getOrderDate().getDate() 过时方法获取日期
                mapObj.put("date",orderSetting.getOrderDate().getDate());
                mapObj.put("number",orderSetting.getNumber());
                mapObj.put("reservations",orderSetting.getReservations());
                //将获取的map添加进list集合
                result.add(mapObj);
            }
        }

        return result;
    }

    //修改某一天的预约设置
    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        //查询这一天的数据是否存在数据
        long countByOrderDate = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        //判断当天是否有设置
        if(countByOrderDate > 0) {
            //执行更新操作
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else {//没有设置执行插入操作
            orderSettingDao.add(orderSetting);
        }
    }
}
