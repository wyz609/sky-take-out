package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * Class name: ReportController
 * Package: com.sky.controller.admin
 * Description:
 *
 * @Create: 2025/5/10 23:33
 * @Author: jay
 * @Version: 1.0
 */

@RestController
@RequestMapping("/admin/report")
@Api(tags = "统计报表相关接口")
public class ReportController {

    @Resource
    private ReportService reportService;

    /**
     * 营业额统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 返回营业额统计结果
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额数据统计")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){

        if (begin.isAfter(end)){
            return Result.error("开始时间不能大于结束时间");
        }

        TurnoverReportVO turnoverReport = reportService.getTurnoverReport(begin, end);

        return Result.success(turnoverReport);
    }

    /**
     * 用户统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 返回用户统计结果
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户数据统计")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        if (begin.isAfter(end)){
            return Result.error("开始时间不能大于结束时间");
        }
        UserReportVO userReport = reportService.getUserStatistics(begin, end);
        return Result.success(userReport);
    }

    /**
     * 订单数据统计
     * @param begin 开始时间
     * @param end   结束时间
     * @return 返回订单统计结果
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单数据统计")
    public Result<OrderReportVO> orderStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        if (begin.isAfter(end)){
            return Result.error("开始时间不能大于结束时间");
        }
        OrderReportVO orderReport = reportService.getOrderStatistics(begin, end);
        return Result.success(orderReport);
    }

    /**
     * 销量前10商品统计
     * @param begin 开始时间
     * @param end   结束时间
     * @return 返回销量前10商品统计结果
     */
    @GetMapping("/top10")
    @ApiOperation("销量前10商品统计")
    public Result<SalesTop10ReportVO> top10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        if (begin.isAfter(end)){
            return Result.error("开始时间不能大于结束时间");
        }
        SalesTop10ReportVO salesTop10Report = reportService.getSalesTop10(begin, end);
        return Result.success(salesTop10Report);
    }

    @GetMapping("/export")
    @ApiOperation("导出营业数据报表")
    public void export(HttpServletResponse response){
        reportService.exportBusinessData(response);
    }

}

