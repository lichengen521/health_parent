package com.health.service;

import java.util.Map;

public interface ReportService {

    //获取运营统计数据
    Map<String,Object> getBusinessReportData() throws Exception;

}
