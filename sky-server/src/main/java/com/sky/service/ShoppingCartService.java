package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * Class name: ShoppingCartService
 * Package: com.sky.service
 * Description:
 *
 * @Create: 2025/4/29 22:43
 * @Author: jay
 * @Version: 1.0
 */
public interface ShoppingCartService {


    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车
     * @return
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 清空购物车
     */
    void cleanShoppingCart();

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    void deleteShoppingCart(ShoppingCartDTO shoppingCartDTO);
}

