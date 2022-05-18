import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;

public class POITest {

    //使用poi工具类读取excel文件内容
    @Test
    public void test1() throws IOException {
        //使用工具类中的方法读取excel文件 创建一个工作簿对象
        XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream(new File("C:\\Users\\liche\\Desktop\\poi.xlsx")));

        //读取excel文件中第一个sheet标签页对象
        XSSFSheet sheet = excel.getSheetAt(0);

        //遍历sheet 获取行
        for (Row row:sheet) {
            //遍历行 获取每个单元格数据
            for (Cell cell : row) {
                //将单元格中的数据读取成字符串
                String string = cell.getStringCellValue();
                System.out.println(string);
            }
        }
        //关闭资源
        excel.close();
    }

    //使用poi工具类读取excel文件内容 改造版
    @Test
    public void test2() throws IOException {
        //使用工具类中的方法读取excel文件 创建一个工作簿对象
        XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream(new File("C:\\Users\\liche\\Desktop\\poi.xlsx")));

        //读取excel文件中第一个sheet标签页对象
        XSSFSheet sheet = excel.getSheetAt(0);

        //获得当前工作表最后一个行号 注意行号从0开始
        int lastRowNum = sheet.getLastRowNum();

        for(int i = 0;i <=lastRowNum ; i++ ) {
            //获取行对象
            XSSFRow row = sheet.getRow(i);
            if(row!=null) {
                //获得当前行最后一个单元格的索引从1开始
                short lastCellNum = row.getLastCellNum();
                for(int j=0;j<lastCellNum;j++) {
                    //根据单元格索引获取单元格对象
                    XSSFCell cell = row.getCell(j);
                    if (cell != null) {
                        System.out.println(cell.getStringCellValue());
                    }else {
                        continue;
                    }
                }
            }else {
                continue;
            }
        }
        //关闭资源
        excel.close();
    }

    //使用poi像excel文件写入数
    @Test
    public void test3() throws Exception{
        //创建一个空的工作簿 在内存中创建
        XSSFWorkbook excel = new XSSFWorkbook();

        //创建一个工作表（标签页）
        XSSFSheet sheet_one = excel.createSheet("sheet_one");

        //在工作表中创建行 在第一行创建
        XSSFRow row = sheet_one.createRow(0);

        //在行中创建单元格
        row.createCell(0).setCellValue("name");
        row.createCell(1).setCellValue("age");
        row.createCell(2).setCellValue("sex");

        //在工作表中创建行 在第一行创建
        XSSFRow row1 = sheet_one.createRow(1);

        //为该行创建数据
        row1.createCell(0).setCellValue("zhangsan");
        row1.createCell(1).setCellValue(18);
        row1.createCell(2).setCellValue("man");

        //创建一个输出流将内存中的excel写出到磁盘
        FileOutputStream fileOutputStream = new FileOutputStream(new File("C:\\Users\\liche\\Desktop\\poi_1.xlsx"));

        //写出内存中的excel
        excel.write(fileOutputStream);

        //刷新输出流
        fileOutputStream.flush();

        //关闭excel文件
        excel.close();

    }

}
