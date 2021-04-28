package com.example.limit.annotation;

import java.lang.annotation.*;

/**
 * @Author jchonker
 * @Date 2021/4/28 15:27
 * @Version 1.0
 * 限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiRateLimit {
    //控制并发最大数量
    int value();
    //是否使用公平锁,默认是非公平锁
    boolean fair() default false;
}
