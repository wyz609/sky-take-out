package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * Class name: ReportService
 * Package: com.sky.service
 * Description:
 *
 * @Create: 2025/5/10 23:34
 * @Author: jay
 * @Version: 1.0
 */
public interface ReportService {

    /**
     * 根据时间区间统计营业额
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 返回营业额统计结果
     */
    TurnoverReportVO getTurnoverReport(LocalDate beginTime , LocalDate endTime);

    /**
     * 用户数据统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 返回用户统计结果
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单数据统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 返回订单统计结果
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 查询指定时间区间内的销量排名前10的商品
     * @param begin 开始时间
     * @param end 结束时间
     * @return 返回销量排名前10的商品
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);

    /**
     * 导出近三十天的运营数据报表
     * @param response
     */
    void exportBusinessData(HttpServletResponse response);
}
