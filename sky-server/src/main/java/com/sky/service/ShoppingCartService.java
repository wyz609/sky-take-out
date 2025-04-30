package com.sky.service;

import com.sky.dto.ShoppingCartDTO;

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
}

