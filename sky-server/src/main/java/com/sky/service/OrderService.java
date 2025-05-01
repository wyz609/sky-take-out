package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

/**
 * Class name: OrderService
 * Package: com.sky.service
 * Description:
 *
 * @Create: 2025/5/1 19:50
 * @Author: jay
 * @Version: 1.0
 */
public interface OrderService {

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
}
