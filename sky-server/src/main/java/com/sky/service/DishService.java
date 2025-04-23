package com.sky.service;

import com.sky.dto.DishDTO;

/**
 * Class name: DishService
 * Package: com.sky.service
 * Description:
 *
 * @Create: 2025/4/23 8:25
 * @Author: jay
 * @Version: 1.0
 */
public interface DishService {
    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);
}

