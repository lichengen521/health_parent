package com.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.entity.Result;
import com.health.service.MemberService;
import com.health.service.ReportService;
import com.health.service.SetmealService;
import com.health.utils.DateUtils;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/report")
public class ReportController {

    //远程注入会员服务
    @Reference
    private MemberService memberService;

    @Reference
    private SetmealService setmealService;

    @Reference
    private ReportService reportService;


    @RequestMapping("/getMemberReport")
    public Result getMemberReport() {
        Map<String, Object> map = new HashMap<>();
        //创建月份集合
        List<String> monthList = new ArrayList<>();
        //获取当前时间的日历对象
        Calendar calendar = Calendar.getInstance();//获得日历对象

        //计算过去一年12个月的月份 按月份计算 往前推12个月
        calendar.add(Calendar.MONTH, -12);
        for (int i = 0; i < 12; i++) {
            Date time = calendar.getTime();//获取被推迟12月的时间
            monthList.add(new SimpleDateFormat("yyyy.MM").format(time));
            calendar.add(Calendar.MONTH, 1);
        }

        //将计算出的月份集合添加进map中
        map.put("months", monthList);
        try {
            //调用会员服务查询 计算出的月份集合中每一个月的会员总数
            List<Integer> memberList = memberService.findMemberCountByMonths(monthList);

            //将每一个月的会员数集合加入map
            map.put("memberCount", memberList);

            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS, map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport() {

        try {
            //创建前端需要格式数据集合
            Map<String, Object> resultMap = new HashMap();

            List<Map<String, Object>> setmealCount = setmealService.getSetmealReport();

            resultMap.put("setmealCount", setmealCount);

            //创建集合所需数据格式集合
            List<String> setmealNames = new ArrayList<>();

            //遍历setmealCount获取名称 并加入到setmealNames
            for (Map<String, Object> map : setmealCount) {
                setmealNames.add(map.get("name").toString());
            }

            resultMap.put("setmealNames", setmealNames);

            return new Result(true, MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS, resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL);
        }
    }

    //运营统计数据查询
    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData() throws Exception {


        try {
            //获取结果数据
            Map<String, Object> businessReportData = reportService.getBusinessReportData();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS, businessReportData);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

    //导出excel文件
    @RequestMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request, HttpServletResponse response) {
        try {
            //获取结果数据
            Map<String, Object> result = reportService.getBusinessReportData();

            //取出返回结果数据，准备将报表数据写入到Excel文件中
            String reportDate = (String) result.get("reportDate");
            Integer todayNewMember = (Integer) result.get("todayNewMember");
            Integer totalMember = (Integer) result.get("totalMember");
            Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");

            //获取模板文件真实路径
            String filePath = request.getSession().getServletContext().getRealPath("template") + File.separator + "report_template.xlsx";

            //基于提供的模板文件 在内存中存储的一个表格对象
            XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream(new File(filePath)));

            //读取第一个工作表
            XSSFSheet sheet = excel.getSheetAt(0);
            XSSFRow row = sheet.getRow(2);//获取第三行
            XSSFCell cell = row.getCell(5);
            cell.setCellValue(reportDate);
            row = sheet.getRow(4);
            row.getCell(5).setCellValue(todayNewMember);//新增会员数（本日）
            row.getCell(7).setCellValue(totalMember);//总会员数
            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数
            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日到诊数
            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周到诊数
            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//本月到诊数

            //获取模板的第十三行
            int rowNum = 12;
            for (Map map : hotSetmeal) {
                BigDecimal proportion = (BigDecimal) map.get("proportion");
                String name=(String)map.get("name");
                Long setmeal_count=(Long)map.get("setmeal_count");
                row = sheet.getRow(rowNum ++);
                row.getCell(4).setCellValue(name);//套餐名称
                row.getCell(5).setCellValue(setmeal_count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//占比
            }
            //通过输出流进行文件下载
            ServletOutputStream out = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");//代表excel文件类型
            response.setHeader("content-Disposition", "attachment;filename=report.xlsx");//指定以附件的方式进行下载
            excel.write(out);
            out.flush();
            out.close();
            excel.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

    //导出pdf文件
    @RequestMapping("/exportBusinessReport4PDF")
    public Result exportBusinessReport4PDF(HttpServletRequest request, HttpServletResponse response) {
        try {
            //获取结果数据
            Map<String, Object> result = reportService.getBusinessReportData();

            //取出返回结果数据，准备将报表数据写入到Excel文件中
            String reportDate = (String) result.get("reportDate");
            Integer todayNewMember = (Integer) result.get("todayNewMember");
            Integer totalMember = (Integer) result.get("totalMember");
            Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");

            //动态获取模板文件真实路径
            String jrxmlPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business3.jrxml";
            //动态获取编译后的模板的文件
            String jasperPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business3.jasper";
            //编译模板
            JasperCompileManager.compileReportToFile(jrxmlPath, jasperPath);

            //填充数据---使用JDBC数据源方式填充
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, result,new JRBeanCollectionDataSource(hotSetmeal));

            //通过输出流进行文件下载
            ServletOutputStream out = response.getOutputStream();
            response.setContentType("application/pdf");//代表pdf文件类型
            response.setHeader("content-Disposition", "attachment;filename=report.pdf");//指定以附件的方式进行下载
            JasperExportManager.exportReportToPdfStream(jasperPrint,out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
        return null;
    }
}
