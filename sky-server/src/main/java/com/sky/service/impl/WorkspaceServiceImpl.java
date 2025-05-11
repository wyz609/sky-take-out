package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Class name: WorkspaceServiceImpl
 * Package: com.sky.service.impl
 * Description:
 *
 * @Create: 2025/5/11 14:03
 * @Author: jay
 * @Version: 1.0
 */
@Service
public class WorkspaceServiceImpl implements WorkSpaceService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private DishMapper dishMapper;

    @Resource
    private SetmealMapper setmealMapper;

    /**
     * 根据是时间段统计营业数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return  返回营业数据
     */
    @Override
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        /**
         * 营业额：当日已完成订单的总金额
         * 有效订单：当日已完成订单的数量
         * 订单完成率： 有效订单数 / 总订单数
         * 平均客单价： 营业额 / 有效订单数
         * 新增用户: 当日新增用户的数量
         */

        // 创建一个HashMap用于存储查询条件
        Map map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);

        // 计算总订单数
        Integer totalOrderCount = orderMapper.countByMap(map);

        // 添加订单状态为已完成的查询条件
        map.put("status", Orders.COMPLETED);

        // 计算营业额
        Double turnvoer = orderMapper.sumByMap(map);
        turnvoer = turnvoer == null ? 0.0 : turnvoer;

        // 计算有效订单数
        Integer validOrderCount = orderMapper.countByMap(map);

        // 初始化平均客单价和订单完成率
        Double unitPrice = 0.0;
        Double orderCompletionRate = 0.0;

        // 如果总订单数和有效订单数不为0，计算平均客单价和订单完成率
        if(totalOrderCount != 0 && validOrderCount != 0){
            unitPrice = Math.round((turnvoer / validOrderCount) * 100.0) / 100.0;
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        // 计算新增用户数
        Integer newUsers = userMapper.countByMap(map);

        // 构建并返回营业数据对象
        return BusinessDataVO.builder()
                .turnover(turnvoer)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    /**
     * 查询订单管理数据
     * @return
     */
    @Override
    public OrderOverViewVO getOrderOverView() {
        Map map = new HashMap();
        map.put("begin", LocalDateTime.now().with(LocalTime.MIN));
        map.put("status", Orders.TO_BE_CONFIRMED);

        //待接单
        Integer waitingOrders = orderMapper.countByMap(map);

        //待派送
        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.countByMap(map);

        //已完成
        map.put("status", Orders.COMPLETED);
        Integer completedOrders = orderMapper.countByMap(map);

        //已取消
        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.countByMap(map);

        //全部订单
        map.put("status", null);
        Integer allOrders = orderMapper.countByMap(map);

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    @Override
    public DishOverViewVO getDishOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = dishMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = dishMapper.countByMap(map);

        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    @Override
    public SetmealOverViewVO getSetmealOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = setmealMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = setmealMapper.countByMap(map);

        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }
}

