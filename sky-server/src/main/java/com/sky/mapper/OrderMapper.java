package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * Class name: OrderMapper
 * Package: com.sky.mapper
 * Description:
 *
 * @Create: 2025/5/1 19:52
 * @Author: jay
 * @Version: 1.0
 */
@Mapper
public interface OrderMapper {

    void insert(Orders order);

}

