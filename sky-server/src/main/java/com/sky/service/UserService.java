package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
 * Class name: UserService
 * Package: com.sky.service
 * Description:
 *
 * @Create: 2025/4/26 20:12
 * @Author: jay
 * @Version: 1.0
 */

public interface UserService {
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}

