package com.health.service;

import com.health.entity.Result;
import com.health.pojo.Order;

import java.rmi.MarshalledObject;
import java.util.Map;

//预约服务
public interface OrderService {

    //体检预约
    Result order(Map map) throws Exception;

    //根据id查询预约信息
    Map findById(Integer id) throws Exception;
}
