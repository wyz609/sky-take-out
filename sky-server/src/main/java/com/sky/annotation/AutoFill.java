package com.sky.annotation;

/**
 * Class name: AutoFill
 * Package: com.sky.annotation
 * Description:
 *
 * @Create: 2025/4/22 21:48
 * @Author: jay
 * @Version: 1.0
 */

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识某个方法需要进行功能字段自动填充处理
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    OperationType value();
}

