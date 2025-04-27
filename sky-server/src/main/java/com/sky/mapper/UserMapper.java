package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * Class name: UserMapper
 * Package: com.sky.mapper
 * Description:
 *
 * @Create: 2025/4/26 20:16
 * @Author: jay
 * @Version: 1.0
 */
@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
     User getByOpenid(String openid);

    /**
     * 插入用户数据
     * @param user
     */
    void insert(User user);
}

