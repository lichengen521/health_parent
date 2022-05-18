package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.health.constant.MessageConstant;
import com.health.dao.MemberDao;
import com.health.dao.OrderDao;
import com.health.dao.OrderSettingDao;
import com.health.dao.SetMealDao;
import com.health.entity.Result;
import com.health.pojo.Member;
import com.health.pojo.Order;
import com.health.pojo.OrderSetting;
import com.health.pojo.Setmeal;
import com.health.service.OrderService;
import com.health.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private SetMealDao setMealDao;

    //体检预约
    @Override
    public Result order(Map map) throws Exception {
//        1、检查用户所选择的预约日期是否已经提前进行了预约设置，如果没有设置则无法进行预约、
        //获取前端传参过来的预约时间
        String orderDate = (String) map.get("orderDate");
        //调用工具类的时间转换
        Date date = DateUtils.parseString2Date(orderDate);
        //根据时间查询当天是否有预约设置
        OrderSetting orderSetting = orderSettingDao.findByOrderDate(date);
        if (orderSetting == null) {//当日没有进行预约设置 无法完成预约
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        //2、检查用户所选择的预约日期是否已经约满，如果已经约满则无法预约
        if (orderSetting.getNumber() <= orderSetting.getReservations()) {
            return new Result(false, MessageConstant.ORDER_FULL);
        }
//        3、检查用户是否重复预约（同一个用户在同一天预约了同一个套餐），如果是重复预约则无法完成再次预约
        //获取用户输入的手机号
        String telephone = (String) map.get("telephone");

        Member member = memberDao.findByTelephone(telephone);
        if (member != null) {
            //当该用户存在时 进行下列判断
            //判断该用户是否重复预约
            //获取用户id
            Integer id = member.getId();
            //获取套餐id
            String string_SetmealId = (String) map.get("setmealId");
            //类型转换
            Integer setmealId = Integer.valueOf(string_SetmealId);


            //封装参数
            Order order = new Order(id, date, setmealId);
            List<Order> orderList = orderDao.findByCondition(order);
            if (orderList != null && orderList.size() != 0) {
                //证明用户在当天已经进行过改套餐的预约 所以无法完成再次预约
                return new Result(false, MessageConstant.HAS_ORDERED);
            }
        } else {//说明当前手机号不是会员
//        4、检查当前用户是否为会员，如果是会员则直接完成预约，如果不是会员则自动完成注册并进行预约
            //创建新的会员
            member = new Member();
            member.setName((String) map.get("name"));
            member.setIdCard((String) map.get("idCard"));
            member.setPhoneNumber(telephone);
            member.setSex((String) map.get("sex"));
            //保存该会员
            memberDao.add(member);
        }
//        5、预约成功，更新当日的已预约人数
        Order order = new Order();
        order.setMemberId(member.getId());//设置会员id
        order.setOrderDate(date);//设置预约日期
        order.setOrderType((String) map.get("orderType"));//设置预约类型 微信预约
        order.setOrderStatus((String) map.get("orderStatus"));//设置预约 未到诊
        order.setSetmealId(Integer.parseInt((String) map.get("setmealId")));//设置套餐id
        //调用方法保存改条预约信息
        orderDao.add(order);

        //并更新当天的预约设置人数
        orderSetting.setReservations(orderSetting.getReservations() + 1);

        //根据预约日期调用dap持久层方法更新当天设置
        orderSettingDao.editReservationsByOrderDate(orderSetting);

        return new Result(true,MessageConstant.ORDER_SUCCESS,order.getId());
    }

    //根据id查询预约信息（体检人姓名、预约日期、套餐名称、预约类型）
    @Override
    public Map findById(Integer id) throws Exception {
        //调用持久层 查询出体检人id 套餐id 预约日期 预约类型
        Map orderDetail = orderDao.findById4Detail(id);
        if (orderDetail != null) {
            //获取日期并处理格式
            Date orderDate = (Date) orderDetail.get("orderDate");
            String orderDateString = DateUtils.parseDate2String(orderDate);
            orderDetail.put("orderDate",orderDateString);
        }

        return orderDetail;
    }
}
