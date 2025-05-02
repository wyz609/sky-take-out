package com.sky.task;

/**
 * Class name: MyTask
 * Package: task
 * Description:
 *
 * @Create: 2025/5/2 19:00
 * @Author: jay
 * @Version: 1.0
 */

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 自定义定时任务类
 */
@Component
@Slf4j
public class MyTask {

    @Autowired
    private OrderMapper orderMapper;

//    @Scheduled(cron = "0/5 * * * * ?")
    public void executeTask(){
        log.info("定时任务开始执行:{}",new Date());
    }

    /**
     * 处理支付超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder(){
        // 记录日志
        log.info("处理支付超时订单：{",new Date());

        // 获取当前时间减去15分钟的时间
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        // 获取状态为待支付且订单时间早于当前时间减去15分钟的所有订单
        List<Orders> ordersList = orderMapper.getByStatusAndOrdertimeLT(Orders.PENDING_PAYMENT, time);
        if(ordersList != null && ordersList.size() > 0){
            // 遍历订单列表
            ordersList.forEach(order ->{
                // 将订单状态设置为已取消
                order.setStatus(Orders.CANCELLED);
                // 设置取消原因
                order.setCancelReason("支付超时，自动取消");
                // 设置取消时间
                order.setCancelTime(LocalDateTime.now());
                // 更新订单信息
                orderMapper.update(order);
            });
        }
    }

    /**
     * 处理”派送中“状态的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("处理派送中的订单:{}",new Date());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getByStatusAndOrdertimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        if(ordersList != null && ordersList.size() > 0){
            ordersList.forEach(order -> {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            });
        }
    }
}

