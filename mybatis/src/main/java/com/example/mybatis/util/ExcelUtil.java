package com.example.mybatis.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelUtil {

    public static void exportExcel(JSONObject jsonObject, String fileName, HttpServletResponse response, String newFileName) throws IOException,ParseException{

        /*
           1.读取模板excel文件
           2.读取配置信息sheet，获取单元格属性keys,属性type类型、模板示例单元格cellStyle及其他配置信息
           3.获取数据源jsonObject，将数据源根据属性key将对应的值映射到excel表中对应的单元格中
           4.移除配置信息表
           5.将excel表写入到response的输出流中，实现excel下载
         */
        //获取模板文件的路径
        String filePath = "excel/"+fileName;
        //读取Excel模板文件,获取文件输入流
        InputStream fileInputStream =
                ExcelUtil.class.getClassLoader().getResourceAsStream(filePath);
        //获取Excel表实例对象
        XSSFWorkbook wb = new XSSFWorkbook(fileInputStream);

        //读取配置信息sheet，获取属性、属性类型、单元格cellStyle及excel数据开始行startRow
        XSSFSheet sheetExample = wb.getSheetAt(1);
        XSSFRow keyRow = sheetExample.getRow(0);
        XSSFRow typeRow = sheetExample.getRow(1);
        XSSFRow  cellStyleRow = sheetExample.getRow(2);
        XSSFRow start = sheetExample.getRow(3);
        int startRow = (int) start.getCell(0).getNumericCellValue();

        //属性数组
        List<String> keys = new ArrayList<>();
        //type 类型数组
        List<String> cellTypes = new ArrayList<>();
        //cellStyle 数组
        List<CellStyle> cellStyleList = new ArrayList<>();

        for (Cell cell:keyRow) {
            keys.add(cell.getStringCellValue());
        }
        for (Cell cell:typeRow) {
            cellTypes.add(cell.getStringCellValue());
        }
        for (Cell cell:cellStyleRow) {
            cellStyleList.add(cell.getCellStyle());
        }

        //数据表
        XSSFSheet sheet = wb.getSheetAt(0);

        //获取List数据源，遍历List插入数据
        List list = jsonObject.getJSONArray("list");
        for(int i=0;i<list.size();i++){
            //获取该行数据
            JSONObject object = JSONObject.parseObject(JSON.toJSONString(list.get(i)));
            //创建行，设置起始行
            XSSFRow row = sheet.createRow(i+startRow-1);
            //创建列
            for(int j=0;j<keys.size();j++){
                //创建列
                Cell cell = row.createCell(j);
                //设置单元格样式为模板文件中的单元格样式
                cell.setCellStyle(cellStyleList.get(j));
                if(j==0){
                    //第一列，序号列
                    cell.setCellValue(i+1);
                }else{
                    setCellValue(cell,j,object,cellTypes,keys);
                }
            }
        }

        //数据源sum,总计sum
        JSONObject sum = jsonObject.getJSONObject("sum");
        if(sum!=null){
            XSSFRow row = sheet.createRow(list.size()+startRow-1);
            //创建一个单元格

            for(int j=2;j<keys.size();j++){
                Cell cell = row.createCell(j);
                cell.setCellStyle(cellStyleList.get(j));
                setCellValue(cell,j,sum,cellTypes,keys);
            }

            Cell cellSum = row.createCell(0);
            cellSum.setCellValue("总计");
            //合并单元格（起始行,结束行,起始列,结束列）
            sheet.addMergedRegion(new CellRangeAddress(list.size()+startRow-1,list.size()+startRow-1,0,1));

        }
        wb.removeSheetAt(1);
        //写入在输出流
        newFileName = URLEncoder.encode(newFileName,"UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition", "attachment;filename=" + newFileName);
        OutputStream outPutStream = response.getOutputStream();
        //写入在输出流
        wb.write(outPutStream);
        outPutStream.flush();
        outPutStream.close();

    }

    //合并单元格
    public static void mergeExcel(JSONObject jsonObject, String fileName, HttpServletResponse response,String newFileName) throws IOException,ParseException {
        /*
            主要实现excel表格数据源中会出现单元格合并的情况
            如：1个订单对应多张发票，同时该订单对应多个商品，1个订单5张发票2个商品
            实现过程 比较发票和商品的数量，以数量多的为参照，
            该行，发票数大于商品数，故该条数据总共占excel表5行，
            发票每行一张发票，
            订单信息合并5行
            商品信息 2条商品按5行合并，合并规则如下：
            int mergeRow = testings.size()/invoices.size(); // 2
            int lastRow = testings.size() % invoices.size(); // 1
            mergeRow 为发票数除以商品数 得的行数，商品的每条信息（除了最后一行）都要合并mergeRow行
            lastRow  为发票数除以商品数 得的余数，商品的最后一行要合并（mergeRow+lastRow） 行

        */
        //读取Excel模板文件
        String filePath = "excel/"+fileName;
        //读取Excel模板文件
        InputStream fileInputStream =
                ExcelUtil.class.getClassLoader().getResourceAsStream(filePath);

        XSSFWorkbook wb = new XSSFWorkbook(fileInputStream);

        //获取属性、属性类型、单元格cellStyle
        XSSFSheet sheetExample = wb.getSheetAt(1);
        XSSFRow keyRow = sheetExample.getRow(0);
        XSSFRow typeRow = sheetExample.getRow(1);
        XSSFRow  cellStyleRow = sheetExample.getRow(2);
        XSSFRow start = sheetExample.getRow(3);
        int startRow = (int) start.getCell(0).getNumericCellValue();
        //发票开始列
        int invoiceStartCell = (int) start.getCell(1).getNumericCellValue();
        //商品开始列和结束列
        int testStartCell = (int) start.getCell(2).getNumericCellValue();
        int testEndCell = (int) start.getCell(3).getNumericCellValue();

        //属性数组
        List<String> keys = new ArrayList<>();
        //type 类型数组
        List<String> cellTypes = new ArrayList<>();
        //cellStyle 数组
        List<CellStyle> cellStyleList = new ArrayList<>();


        for (Cell cell:keyRow) {
            keys.add(cell.getStringCellValue());
        }
        for (Cell cell:typeRow) {
            cellTypes.add(cell.getStringCellValue());
        }
        for (Cell cell:cellStyleRow) {
            cellStyleList.add(cell.getCellStyle());
        }
        int nowRow = startRow - 1;

        //数据表
        XSSFSheet sheet = wb.getSheetAt(0);

        //遍历List插入数据
        List list = jsonObject.getJSONArray("list");
        for(int i=0;i<list.size();i++){
            JSONObject jsonObject1 = JSONObject.parseObject(JSON.toJSONString(list.get(i)));
            //获取发票
            JSONArray invoices = jsonObject1.getJSONArray("invoices");
            //获取检测套餐
            JSONArray testings = jsonObject1.getJSONArray("testings");
            //该订单总共占多少行
            int rowSize = 0;

            if(invoices.size() > testings.size()){
                //该订单总共的行数
                rowSize = invoices.size();  //5

                //合并的行数
                int mergeRow = invoices.size()/testings.size();  //2
                //最后一行多的行数
                int lastRow = invoices.size()% testings.size(); //1

                //发票
                for(int j=0;j<invoices.size();j++){
                    JSONObject object = JSONObject.parseObject(JSON.toJSONString(invoices.get(j)));
                    //发票每一行数据
                    XSSFRow row = sheet.createRow(nowRow+j);  //2、3、4、5、6
                    //发票的每一列
                    for(int z=invoiceStartCell;z<=testStartCell-1;z++){
                        Cell cell1 = row.createCell(z);
                        cell1.setCellStyle(cellStyleList.get(z));
                        setCellValue(cell1,z,object,cellTypes,keys);
                    }
                }
                //检测套餐
                for(int x=0;x<testings.size();x++) {
                    JSONObject object = JSONObject.parseObject(JSON.toJSONString(testings.get(x)));
                    //检测套餐的每一行数据,起始行nowRow+mergeRow*x ,结束行nowRow+mergeRow*x+mergeRow-1
                    XSSFRow rowTest = sheet.getRow(nowRow + mergeRow * x); //2、4
                    for (int y = testStartCell; y <= testEndCell; y++) {
                        Cell cell1 = rowTest.createCell(y);
                        cell1.setCellStyle(cellStyleList.get(y));
                        //合并单元格（起始行,结束行,起始列,结束列）
                        if(x==testings.size()-1){
                            //最后一行，合并多的行数
                            sheet.addMergedRegion(new CellRangeAddress(nowRow + mergeRow * x, nowRow + mergeRow * x + mergeRow - 1+lastRow, y, y));
                        }else if(mergeRow>1){
                            sheet.addMergedRegion(new CellRangeAddress(nowRow + mergeRow * x, nowRow + mergeRow * x + mergeRow - 1, y, y));
                        }
                        setCellValue(cell1,y,object,cellTypes,keys);
                    }

                }

            }else if(invoices.size()< testings.size()){
                rowSize = testings.size();  //5
                //invoices 3 testings 5
                int mergeRow = testings.size()/invoices.size(); //2  1
                int lastRow = testings.size() % invoices.size(); //1  2

                //检测套餐
                for(int j=0;j<testings.size();j++){
                    JSONObject object = JSONObject.parseObject(JSON.toJSONString(testings.get(j)));
                    //检测套餐每一行数据
                    XSSFRow row = sheet.createRow(nowRow+j);  //2、3、4、5、6
                    //检测套餐的每一列
                    for(int z=testStartCell;z<=testEndCell;z++){
                        Cell cell1 = row.createCell(z);
                        cell1.setCellStyle(cellStyleList.get(z));
                        setCellValue(cell1,z,object,cellTypes,keys);
                    }
                }

                //发票
                for(int x=0;x<invoices.size();x++) {
                    JSONObject object = JSONObject.parseObject(JSON.toJSONString(invoices.get(x)));
                    //发票的每一行数据,起始行nowRow+mergeRow*x ,结束行nowRow+mergeRow*x+mergeRow-1
                    XSSFRow rowTest = sheet.getRow(nowRow + mergeRow * x); //2、4
                    for (int y = invoiceStartCell; y <= testStartCell-1; y++) {
                        Cell cell1 = rowTest.createCell(y);
                        cell1.setCellStyle(cellStyleList.get(y));
                        //合并单元格（起始行,结束行,起始列,结束列）
                        if(x==invoices.size()-1){
                            //最后一行，合并多的行数
                            sheet.addMergedRegion(new CellRangeAddress(nowRow + mergeRow * x, nowRow + mergeRow * x + mergeRow - 1+lastRow, y, y));
                        }else if(mergeRow>1){
                            sheet.addMergedRegion(new CellRangeAddress(nowRow + mergeRow * x, nowRow + mergeRow * x + mergeRow - 1, y, y));
                        }
                        setCellValue(cell1,y,object,cellTypes,keys);
                    }

                }
            }else{
                rowSize = testings.size();
                //检测套餐
                for(int j=0;j<testings.size();j++){
                    JSONObject object = JSONObject.parseObject(JSON.toJSONString(testings.get(j)));
                    //检测套餐每一行数据
                    XSSFRow row = sheet.createRow(nowRow+j);  //2、3、4、5、6
                    //发票的每一列
                    for(int z=testStartCell;z<=testEndCell;z++){
                        Cell cell1 = row.createCell(z);
                        cell1.setCellStyle(cellStyleList.get(z));
                        setCellValue(cell1,z,object,cellTypes,keys);
                    }
                }

                //发票
                for(int j=0;j<invoices.size();j++){
                    JSONObject object = JSONObject.parseObject(JSON.toJSONString(invoices.get(j)));
                    //发票每一行数据
                    XSSFRow row = sheet.getRow(nowRow+j);  //2、3、4、5、6
                    //发票的每一列
                    for(int z=invoiceStartCell;z<=testStartCell-1;z++){
                        Cell cell1 = row.createCell(z);
                        cell1.setCellStyle(cellStyleList.get(z));
                        setCellValue(cell1,z,object,cellTypes,keys);
                    }
                }
            }

            //设置序号列
            XSSFRow rowOrder = sheet.getRow(nowRow);
            Cell cell = rowOrder.createCell(0);
            if(rowSize>1){
                //rowSize 等于1时不需要合并单元格
                sheet.addMergedRegion(new CellRangeAddress(nowRow,nowRow+rowSize-1,0,0));
            }
            cell.setCellValue(i+1);

            //订单信息
            for(int j=1;j<=invoiceStartCell-1;j++){
                Cell cell1 = rowOrder.createCell(j);
                cell1.setCellStyle(cellStyleList.get(j));
                //合并单元格（起始行,结束行,起始列,结束列）
                if(rowSize>1){
                    sheet.addMergedRegion(new CellRangeAddress(nowRow,nowRow+rowSize-1,j,j));
                }
                setCellValue(cell1,j,jsonObject1,cellTypes,keys);
            }
            nowRow = nowRow + rowSize;

        }

        //总计sum
        JSONObject sum = jsonObject.getJSONObject("sum");
        if(sum!=null){
            XSSFRow row = sheet.createRow(nowRow);
            //创建一个单元格

            for(int j=2;j<keys.size();j++){
                Cell cell = row.createCell(j);
                cell.setCellStyle(cellStyleList.get(j));
                setCellValue(cell,j,sum,cellTypes,keys);
            }

            Cell cellSum = row.createCell(0);
            cellSum.setCellValue("总计");
            //合并单元格（起始行,结束行,起始列,结束列）
            sheet.addMergedRegion(new CellRangeAddress(nowRow,nowRow,0,1));

        }
        wb.removeSheetAt(1);
        newFileName = URLEncoder.encode(newFileName,"UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition", "attachment;filename=" + newFileName);
        OutputStream outPutStream = response.getOutputStream();
//        //定义一个输出流
//        FileOutputStream fileOutputStream = new FileOutputStream("income1.xlsx");
//        //写入在输出流
//        excel.write(fileOutputStream);
        wb.write(outPutStream);
        outPutStream.flush();
        outPutStream.close();
    }

    public static void setCellValue(Cell cell,int j,JSONObject object,List<String> cellTypes,List<String> keys) throws IOException,ParseException {
        String dateStr = object.getString(keys.get(j));
        switch (cellTypes.get(j)) {
            case "number"://数值
                if(object.containsKey(keys.get(j))){
                    cell.setCellValue(object.getIntValue(keys.get(j)));
                }else{
                    cell.setCellValue("");
                }
                break;
            case "percentage"://百分比，excel百分比格式是 数值*100再添加%号，故原数值需先除100
                if(object.containsKey(keys.get(j))){
                    cell.setCellValue(Double.valueOf(object.getIntValue(keys.get(j)))/100);
                }else{
                    cell.setCellValue("");
                }
                break;
            case "amount"://金额
                if(object.containsKey(keys.get(j))){
                    Double money = Double.valueOf(object.getLongValue(keys.get(j)))/1000;
                    money = (double)Math.round(money*100)/100;
                    cell.setCellValue(money);
                }else{
                    cell.setCellValue(0);
                }
                break;
            case "string"://字符串
                if(object.containsKey(keys.get(j))&& object.getString(keys.get(j))!=null){
                    cell.setCellValue(object.getString(keys.get(j)));
                }else{
                    cell.setCellValue("");
                }
                break;
            case "dateDay"://日
                if(object.containsKey(keys.get(j))&& dateStr!=null){
                    cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(dateStr));
                }else{
                    cell.setCellValue("");
                }
                break;
            case "dateMonth"://月
                if(object.containsKey(keys.get(j))&& dateStr!=null){
                    cell.setCellValue(new SimpleDateFormat("yyyy-MM").parse(dateStr));
                }else{
                    cell.setCellValue("");
                }
                break;
            case "dateYear"://年
                if(object.containsKey(keys.get(j))&& dateStr!=null){
                    cell.setCellValue(new SimpleDateFormat("yyyy").parse(dateStr));
                }else{
                    cell.setCellValue("");
                }
                break;
            case "dateLong"://Long类型的日期
                Long date = object.getLongValue(keys.get(j));
                if(object.containsKey(keys.get(j))&& date!=null){
                    cell.setCellValue(new Date(date));
                }else{
                    cell.setCellValue("");
                }
                break;
            default:
        }
    }

}
