package com.health.dao;

import com.health.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderSettingDao {
    //存储上传的excel解析的数据
    void add(OrderSetting orderSetting);

    //如果存在已有设置那么更新设置
    void editNumberByOrderDate(OrderSetting orderSetting);

    //查询当前上传设置时间是否有设置
    long findCountByOrderDate(Date orderDate);

    //查询当前月份的预约设置数据
    List<OrderSetting> getOrderSettingByMonth(Map<String,String> map);

    //根据日期查询当日的预约设置
    OrderSetting findByOrderDate(Date date);

    //更新已预约人数
    void editReservationsByOrderDate(OrderSetting orderSetting);
}
