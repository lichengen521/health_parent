package com.health.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.health.pojo.OrderSetting;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface OrderSettingService {

    //上传excel数据
    void upload(List<OrderSetting> list);

    //根据月份查询当月的预约设置
    List<Map> getOrderSettingByMonth(String date);

    void editNumberByDate(OrderSetting orderSetting);
}
