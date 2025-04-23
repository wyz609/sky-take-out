package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Class name: SetmealDishMapper
 * Package: com.sky.mapper
 * Description:
 *
 * @Create: 2025/4/23 19:52
 * @Author: jay
 * @Version: 1.0
 */
@Mapper
public interface SetmealDishMapper {
    /**
     * 判断当前菜品是否被套餐关联了
     * @param ids
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    /**
     * 保存套餐和菜品的关联关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 删除套餐餐品关系中表的数据
     * @param id
     */
    void deleteBySetmaleId(Long id);

    /**
     * 根据套餐信息查询菜品信息
     * @param id
     * @return
     */
    List<SetmealDish> getBySetmealId(Long id);
}
