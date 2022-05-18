package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.health.dao.MemberDao;
import com.health.dao.OrderDao;
import com.health.dao.SetMealDao;
import com.health.service.MemberService;
import com.health.service.ReportService;
import com.health.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//运营数据统计服务
@Service(interfaceClass = ReportService.class)
@Transactional
public class ReportServiceImpl implements ReportService {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private SetMealDao setMealDao;


    //获取运营统计数据
    @Override
    public Map<String, Object> getBusinessReportData() throws Exception {
        Map<String, Object> result = new HashMap<>();

        //获取当前时间的yyyy-MM-dd 格式的时间字符串
        String today = DateUtils.parseDate2String(new Date());

        //添加数据时间
        result.put("reportDate", today);

        //获取本日新增会员数
        Integer todayNewMember = memberDao.findMemberCountByDate(today);

        //获取总会员数
        Integer totalMember = memberDao.findMemberTotalCount();

        //获取本周新增会员数
        //获得本周一的日期
        String thisWeekMonday = DateUtils.parseDate2String(DateUtils.getThisWeekMonday());
        Integer thisWeekNewMember = memberDao.findMemberCountAfterDate(thisWeekMonday);

        //获取本月新增会员数
        //获取当前月一日
        String firstDay4ThisMonth = DateUtils.parseDate2String(DateUtils.getFirstDay4ThisMonth());
        Integer thisMonthNewMember = memberDao.findMemberCountAfterDate(firstDay4ThisMonth);

        //今日预约数
        Integer todayOrderNumber = orderDao.findOrderCountByDate(today);
        //本周预约数
        Integer thisWeekOrderNumber = orderDao.findOrderCountAfterDate(thisWeekMonday);
        //本月预约数
        Integer thisMonthOrderNumber = orderDao.findOrderCountAfterDate(firstDay4ThisMonth);
        //今日到诊数
        Integer todayVisitsNumber = orderDao.findVisitsCountByDate(today);
        //本周到诊数
        Integer thisWeekVisitsNumber = orderDao.findVisitsCountAfterDate(thisWeekMonday);
        //本月到诊数
        Integer thisMonthVisitsNumber = orderDao.findVisitsCountAfterDate(firstDay4ThisMonth);

        //热门套餐的查询
        List<Map> hotSetmeal = orderDao.findHotSetmeal();

        result.put("reportDate", today);
        result.put("todayNewMember", todayNewMember);
        result.put("totalMember", totalMember);
        result.put("thisWeekNewMember", thisWeekNewMember);
        result.put("thisMonthNewMember", thisMonthNewMember);
        result.put("todayOrderNumber", todayOrderNumber);
        result.put("thisWeekOrderNumber", thisWeekOrderNumber);
        result.put("thisMonthOrderNumber", thisMonthOrderNumber);
        result.put("todayVisitsNumber", todayVisitsNumber);
        result.put("thisWeekVisitsNumber", thisWeekVisitsNumber);
        result.put("thisMonthVisitsNumber", thisMonthVisitsNumber);
        result.put("hotSetmeal", hotSetmeal);

        return result;
    }
}
