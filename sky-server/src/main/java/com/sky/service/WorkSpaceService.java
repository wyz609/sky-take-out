package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

/**
 * Class name: WorkSpaceService
 * Package: com.sky.service
 * Description:
 *
 * @Create: 2025/5/11 13:53
 * @Author: jay
 * @Version: 1.0
 */

public interface WorkSpaceService {

    /**
     * 根据时间段统计营业数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 返回营业数据
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * 查询订单管理数据
     * @return 返回订单管理数据
     */
    OrderOverViewVO getOrderOverView();

    /**
     * 查询菜品总览
     *
     * @return 返回菜品数据
     */
    DishOverViewVO getDishOverView();

    /**
     * 查询套餐总览
     * @return 返回套餐数据
     */
    SetmealOverViewVO getSetmealOverView();
}
