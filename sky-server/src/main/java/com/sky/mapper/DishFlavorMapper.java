package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Class name: DishFlavorMapper
 * Package: com.sky.mapper
 * Description:
 *
 * @Create: 2025/4/23 8:36
 * @Author: jay
 * @Version: 1.0
 */
@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味数据
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);
}
