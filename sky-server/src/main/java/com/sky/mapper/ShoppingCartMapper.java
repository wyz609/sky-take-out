package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Class name: ShoppingCartMapper
 * Package: com.sky.mapper
 * Description:
 *
 * @Create: 2025/4/29 22:45
 * @Author: jay
 * @Version: 1.0
 */
@Mapper
public interface ShoppingCartMapper {

    /**
     * 查询条件
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 更新商品数量
     * @param shoppingCart
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 插入购物车数据
     * @param shoppingCart
     */
//    @Insert("insert into shopping_cart (name, user_id, dish_id, setmeal_id, dish_flavor, number, amount, image, create_time) " +
//            " values (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{image},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 将购物车对象批量添加到购物车
     * @param shoppingCartList
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
