package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Class name: OrderController
 * Package: com.sky.controller.user
 * Description:
 *
 * @Create: 2025/5/1 19:45
 * @Author: jay
 * @Version: 1.0
 */
@RequestMapping("/user/order")
@RestController("userOderController")
@Slf4j
@Api(tags = "C端-订单接口")
public class OrderController {


    @Autowired
    private OrderService orderService;


    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO orderSubmitDTO) {
        log.info("用户下单:{}", orderSubmitDTO);

        OrderSubmitVO orderSubmitVO = orderService.submitOrder(orderSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     * @throws Exception
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付:{}", ordersPaymentDTO);

        OrderPaymentVO order = orderService.payment(ordersPaymentDTO);

        log.info("生成预支付交易单:{}", order);
        return Result.success(order);
    }
}

