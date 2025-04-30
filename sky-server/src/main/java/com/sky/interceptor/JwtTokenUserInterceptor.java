package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class name: JwtTokenUserInterceptor
 * Package: com.sky.interceptor
 * Description:
 *
 * @Create: 2025/4/27 16:40
 * @Author: jay
 * @Version: 1.0
 */

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;


    /**
     * 校验jwt
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception {

        // 判断当前拦截到的是Controller的方法还是其他的资源
        if(!(handler instanceof HandlerMethod)){
            // 当前拦截到的不是动态方法，直接放行
            return true;
        }

        // 1.从请求头获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());
        try {
            // 校验令牌
            log.info("jwt校验：{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("userId:{}", userId);
            BaseContext.setCurrentId(userId);
            // 3.通过，放行
            return true;
        }catch (Exception e){
            response.setStatus(401);
            return false;
        }
    }

}

