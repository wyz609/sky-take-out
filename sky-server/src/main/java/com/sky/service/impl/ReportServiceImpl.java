package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    private Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime) {
        Map map = new HashMap();
        map.put("begin",beginTime);
        map.put("end",endTime);
        return userMapper.countByMap(map);
    }
}

