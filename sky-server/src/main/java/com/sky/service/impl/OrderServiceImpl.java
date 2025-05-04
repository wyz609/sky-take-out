package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Request;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class name: OrderServiceImpl
 * Package: com.sky.service.impl
 * Description:
 *
 * @Create: 2025/5/1 19:51
 * @Author: jay
 * @Version: 1.0
 */

/**
 * 订单
 */

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        // 异常情况的处理(收货地址为空，超出配送的范围，购物车为空)
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);

        // 查询当前用户的购物车数据
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if(shoppingCartList == null || shoppingCartList.size() == 0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }


        // 构造订单数据
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, order);
        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getDetail());
        order.setConsignee(addressBook.getConsignee());
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setUserId(userId);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setPayStatus(Orders.UN_PAID);
        order.setOrderTime(LocalDateTime.now());

        // 向订单表插入一条数据
        orderMapper.insert(order);

        // 订单明细数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetailList.add(orderDetail);
        }

        // 向明细表插入n条数据
        orderDetailMapper.insertBatch(orderDetailList);

        // 清理购物车中的数据
        shoppingCartMapper.deleteByUserId(userId);


        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime())
                .build();
        return orderSubmitVO;
    }

    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        Long userId = BaseContext.getCurrentId();
        userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        /*JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }*/

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        Integer OrderPaidStatus = Orders.PAID;
        Integer OrderStatus = Orders.TO_BE_CONFIRMED;

        LocalDateTime check_out_time = LocalDateTime.now();

        String orderNumber = ordersPaymentDTO.getOrderNumber();

        log.info("调用updateStatus，用于替换微信支付更新数据库状态的问题");
        orderMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, orderNumber);


        return vo;
    }

    @Override
    public void paySuccess(String outTradeNo) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单号查询当前用户的订单
        Orders ordersDB = orderMapper.getByNumberAndUserId(outTradeNo, userId);

        // 根据订单id更新订单的状态，支付方式，支付状态，结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED) // 该接单
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        // 更新数据库中的数据状态
        orderMapper.update(orders);

    }

    /**
     * 用户订单分页查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult pageQueryUser(int page, int pageSize, Integer status) {
        // 开启分页
        PageHelper.startPage(page,pageSize);

        // 创建订单分页查询对象
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        // 设置当前用户id
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        // 设置订单状态
        ordersPageQueryDTO.setStatus(status);

        // 分页查询订单
        Page<Orders> OrdersPage =  orderMapper.pageQuery(ordersPageQueryDTO);

        List<Orders> list = new ArrayList();

        // 判断查询结果是否为空
        if(OrdersPage != null && OrdersPage.getTotal() > 0){
            // 遍历查询结果
            for(Orders orders : OrdersPage){
                Long orderId = orders.getId(); // 订单id

                List<OrderDetail> ordersDetails = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(ordersDetails);

                list.add(orderVO);
            }
        }

        return new PageResult(OrdersPage.getTotal(),list);
    }

    @Override
    public OrderVO detail(Long id) {

        // 根据id查询订单
        Orders orders =  orderMapper.getById(id);

        // 查询该订单对应的菜品/ 套餐明细
        List<OrderDetail> orderDetailList =  orderDetailMapper.getByOrderId(orders.getId());

        // 将该订单及其详情封装到OrderVO并返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }

    /**
     * 用户取消订单
     * @param id
     * @throws Exception
     */
    @Override
    public void userCancelById(Long id) throws Exception {
        // 根据id获取订单信息
        Orders ordersDB = orderMapper.getById(id);

        // 如果订单不存在，抛出异常
        if(ordersDB == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 订单状态 1 代付款 2 待接单 3 已接单 4 派送中 5 已完成 6 已取消
        // 如果订单状态大于2，抛出异常
        if(ordersDB.getStatus() > 2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 创建新的订单对象
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        // 如果订单状态为待确认，则进行退款操作
        if(ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            // 这里前期是跳过了微信支付，所以这里不用再进行调用微信支付工具的信息
//            // 调用微信支付工具进行退款
//            weChatPayUtil.refund(
//                    ordersDB.getNumber(),// 商户订单号
//                    ordersDB.getNumber(), // 商户退款单号
//                    new BigDecimal(0.01), // 退款金额，单位 元
//                    new BigDecimal(0.01) // 原订单金额
//                     );
            // 支付状态修改为 退款
            orders.setPayStatus(Orders.REFUND);
        }

        // 设置订单状态为已取消
        orders.setStatus(Orders.CANCELLED);
        // 设置取消原因
        orders.setCancelReason("用户取消");
        // 设置取消时间
        orders.setCancelTime(LocalDateTime.now());
        // 更新订单信息
        orderMapper.update(orders);

    }

    @Override
    public void repetition(Long id) {
        // 获取当前用户ID
        Long userId =  BaseContext.getCurrentId();

        // 根据订单ID获取订单详情列表
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        // 将订单详情列表转换为购物车列表
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x,shoppingCart,"id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // 将购物车对象批量添加到数据库
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        // 使用PageHelper插件进行分页
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());

        // 调用orderMapper的pageQuery方法进行条件查询，返回分页结果
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        // 将分页结果转换为OrderVO列表
        List<OrderVO> orderVOList = getOrderVOList(page);

        // 返回分页结果
        return new PageResult(page.getTotal(),orderVOList);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {

        // 统计待确认订单数量
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        // 统计已确认订单数量
        Integer confirmed =  orderMapper.countStatus(Orders.CONFIRMED);
        // 统计配送中订单数量
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        // 创建订单统计对象
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        // 设置已确认订单数量
        orderStatisticsVO.setConfirmed(confirmed);
        // 设置待确认订单数量
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        // 设置配送中订单数量
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);

        return orderStatisticsVO;
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();
    }

    /**
     * 拒绝接单
     * @param ordersRejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(ordersRejectionDTO.getId());

        // 判断订单是否存在且状态为待确认
        if (ordersDB == null && !ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 支付状态
        Integer payStatus = ordersDB.getPayStatus();
        Orders orders = new Orders();

        // 如果订单已支付
        if (payStatus == Orders.PAID){

            //            //用户已支付，需要退款
//            String refund = weChatPayUtil.refund(
//                    ordersDB.getNumber(),
//                    ordersDB.getNumber(),
//                    new BigDecimal(0.01),
//                    new BigDecimal(0.01));
//            log.info("申请退款：{}", refund);

            // 这里进行手动设置订单状态
            orders.setStatus(ordersDB.getStatus());
            orders.setId(ordersDB.getId());
            orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
            orders.setCancelTime(LocalDateTime.now());
        }

        // 更新订单
        orderMapper.update(orders);

    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(ordersCancelDTO.getId());

        Integer payStatus = ordersDB.getPayStatus();

        Orders orders = new Orders();
        if(payStatus == 1){
            //            //用户已支付，需要退款
//            String refund = weChatPayUtil.refund(
//                    ordersDB.getNumber(),
//                    ordersDB.getNumber(),
//                    new BigDecimal(0.01),
//                    new BigDecimal(0.01));
//            log.info("申请退款：{}", refund);


            // 管理端取消订单需要退款，根据订单id更新订单状态、取消原因、取消时间
            // 设置订单id
            orders.setId(ordersDB.getId());
            // 设置订单状态为已取消
            orders.setStatus(Orders.CANCELLED);
            // 设置取消原因
            orders.setCancelReason(ordersCancelDTO.getCancelReason());
            // 设置取消时间
            orders.setCancelTime(LocalDateTime.now());
        }

        // 更新订单
        orderMapper.update(orders);

    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void delivery(Long id) {
        // 根据id获取订单信息
        Orders ordersDB = orderMapper.getById(id);

        // 如果订单不存在或者订单状态不是已确认，则抛出异常
        if(ordersDB == null && !ordersDB.getStatus().equals(Orders.CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 创建新的订单对象
        Orders orders = new Orders();
        // 设置订单id
        orders.setId(ordersDB.getId());
        // 更新订单状态， 状态转换为派送中
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        // 更新订单信息
        orderMapper.update(orders);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void complete(Long id) {
        // 根据id获取订单信息
        Orders ordersDB = orderMapper.getById(id);

        // 如果订单不存在或者订单状态不是正在配送，则抛出异常
        if(ordersDB == null && !ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 创建新的订单对象
        Orders orders = new Orders();
        // 设置订单id
        orders.setId(ordersDB.getId());
        // 设置订单状态为已完成
        orders.setStatus(Orders.COMPLETED);
        // 设置订单完成时间
        orders.setDeliveryTime(LocalDateTime.now());

        // 更新订单信息
        orderMapper.update(orders);


    }

    // 根据分页对象获取订单VO列表
    private List<OrderVO> getOrderVOList(Page<Orders> page) {
        // 创建订单VO列表
        List<OrderVO> orderOVList = new ArrayList<>();

        // 获取订单列表
        List<Orders> ordersList = page.getResult();

        // 如果订单列表不为空
        if(!CollectionUtils.isEmpty(ordersList)){
            // 遍历订单列表
            for(Orders orders : ordersList) {
                // 创建订单VO对象
                OrderVO orderVO = new OrderVO();
                // 将订单对象属性复制到订单VO对象
                BeanUtils.copyProperties(orders, orderVO);
                // 获取订单菜品字符串
                String orderDishes = getOrderDishesStr(orders);

                // 设置订单VO对象的订单菜品字符串
                orderVO.setOrderDishes(orderDishes);
                // 将订单VO对象添加到订单VO列表
                orderOVList.add(orderVO);
            }
        }
        // 返回订单VO列表
        return orderOVList;

    }

    private String getOrderDishesStr(Orders orders) {
        // 查询订单菜品详情信息(订单中的菜品和数量)
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        // 将订单菜品详情信息转换为字符串形式
        List<String> orderDishList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        // 将字符串列表转换为字符串，以空格分隔
        return String.join(" ", orderDishList);
    }
}

