package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

import java.util.List;

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
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 启用禁用菜品状态
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    void deleteBatch(List<Long> ids);
}

