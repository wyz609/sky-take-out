package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class name: ReportService
 * Package: com.sky.service.impl
 * Description:
 *
 * @Create: 2025/5/10 23:35
 * @Author: jay
 * @Version: 1.0
 */
@Service
public class ReportServiceImpl implements ReportService {
    
    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;
    @Resource
    private WorkSpaceService workSpaceService;


    @Override
    public TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        
        while(!begin.isAfter(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);

        }

        List<Double> turnoverList = new ArrayList<>();
        
        for(LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover =  orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
 * 获取用户统计数据
 *
 * 统计指定日期范围内的每日新增用户数量和总用户数量
 *
 * @param begin 开始日期
 * @param end 结束日期
 * @return 包含日期、每日新增用户列表和总用户列表的用户报告VO对象
 */
@Override
public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
    // 创建一个列表，用于存储从开始到结束日期之间的所有日期
    ArrayList<LocalDate> dateList = new ArrayList<>();
    dateList.add(begin);

    // 循环增加日期，直到开始日期超过结束日期
    while(!begin.isAfter(end)){
        begin = begin.plusDays(1);
        dateList.add(begin);
    }

    // 创建列表，用于存储每日新增用户数量
    List<Integer> newUserList = new ArrayList<>();
    // 创建列表，用于存储每日总用户数量
    List<Integer> totalUserList = new ArrayList<>();

    // 遍历每个日期，计算每日新增用户数量和总用户数量
    for(LocalDate date : dateList){
        // 获取当天的开始时间
        LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
        // 获取当天的结束时间
        LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

        // 计算新增用户数量
        Integer newUser = getOrderCount(beginTime, endTime);
        // 计算总用户数量
        Integer totalUser = getOrderCount(null, endTime);

        newUserList.add(newUser);
        totalUserList.add(totalUser);
    }
    // 构建并返回用户报告VO对象
    return UserReportVO.builder()
            .dateList(StringUtils.join(dateList, ","))
            .newUserList(StringUtils.join(newUserList, ","))
            .totalUserList(StringUtils.join(totalUserList, ","))
            .build();
}

    /**
     * 获取订单统计数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 订单统计报告对象
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 初始化日期列表，用于存储开始时间到结束时间之间的所有日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        // 循环增加日期，直到开始时间超过结束时间
        while(!begin.isAfter(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 初始化订单数量和有效订单数量列表
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        // 遍历每个日期，计算订单数量和有效订单数量
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 查询每天订单总数
            Integer orderCount = getOrderCount(beginTime, endTime,null);
            // 查询每天有效订单数
            Integer validOrderCount = getOrderCount(beginTime, endTime,Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        // 计算时间区间内的订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        // 计算时间区间内的总有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        // 计算订单完成率
        Double orderCompletionRate = 0.0;

        if(totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }
        // 构建并返回订单统计报告对象
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 查询指定时间区间内的销量排名前10的商品
     * 该方法通过调用orderMapper中的getSalesTop10方法获取指定时间区间内销量排名前10的商品信息，
     * 并将其格式化为一个包含商品名称和销售数量的字符串列表
     *
     * @param begin 开始时间
     * @param end 结束时间
     * @return 返回销量排名前10的商品
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        // 将开始时间和结束时间转换为时间区间的开始和结束，以便后续查询
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        // 调用orderMapper的getSalesTop10方法获取销量排名前10的商品信息
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime, endTime);

        // 将商品名称和销售数量分别格式化为逗号分隔的字符串
        String nameList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()), ",");
        String numberList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()), ",");

        // 构建并返回包含商品名称和销售数量字符串的SalesTop10ReportVO对象
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }


    /**
     * 导出近三十天的运营数据报表
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);

        BusinessDataVO businessData = workSpaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        if(inputStream == null){
            throw new RuntimeException("模板文件不存在");
        }
        try {
            //基于提供好的模板文件创建一个新的Excel表格对象
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            //获得Excel文件中的一个Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            sheet.getRow(1).getCell(1).setCellValue(begin + "至" + end);
            //获得第4行
            XSSFRow row = sheet.getRow(3);
            //获取单元格
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                //准备明细数据
                businessData = workSpaceService.getBusinessData(LocalDateTime.of(date,LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }
            //通过输出流将文件下载到客户端浏览器中
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            //关闭资源
            out.flush();
            out.close();
            excel.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 根据动态条件统计用户数量
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 返回用户数量
     */
    private Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime, Integer status) {
        Map map = new HashMap();
        map.put("begin",beginTime);
        map.put("end",endTime);
        if (status != null){
            map.put("status",status);
        }
        return orderMapper.countByMap(map);
    }


    private Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime) {
        Map map = new HashMap();
        map.put("begin",beginTime);
        map.put("end",endTime);
        return userMapper.countByMap(map);
    }
}

