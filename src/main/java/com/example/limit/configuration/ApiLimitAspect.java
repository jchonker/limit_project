package com.example.limit.configuration;

import com.example.limit.annotation.ApiRateLimit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * @Author jchonker
 * @Date 2021/4/28 15:32
 * @Version 1.0
 */
@Slf4j
@Aspect
//最高优先级  注册到容器的顺序，控制bean的初始化顺序
//@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
public class ApiLimitAspect {
    //存储限流量和方法,必须是static且线程安全,保证所有线程进入都唯一
    public static Map<String, Semaphore> semaphoreMap= new ConcurrentHashMap<>();

    //拦截所有controller 的所有方法
    @Around("execution(* com.example.limit.controller.*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint){
        Object result=null;
        Semaphore semaphore=null;
        //获取目标对象
        Class<?> aClass = joinPoint.getTarget().getClass();
        //获取增强的方法的信息
        Signature signature = joinPoint.getSignature();
        //方法名
        String name = signature.getName();
        //判断当前方法是否是限流方法，是，获取方法名
        String methodKey="";
        Method[] methods = aClass.getMethods();
        for (Method method:methods) {
            //是否是当前方法
            //是否是限流方法，isAnnotationPresent指定类型的注释存在于此元素上,返回true
            if (method.getName().equals(name) && method.isAnnotationPresent(ApiRateLimit.class)){
                methodKey=name;
                break;
            }
        }
        if(methodKey!=null && !"".equals(methodKey)) {
            semaphore = semaphoreMap.get(methodKey);
            try {
                //消耗一个线程数，默认也是1，如果线程数被消耗完了，别的线程只能等待，等待到释放为止
//                semaphore.acquire(1);
                //尝试获取锁,返回获取结果
                boolean tryAcquireFlag = semaphore.tryAcquire(1);
                log.info("tryAcquireFlag:"+tryAcquireFlag);
                //如果获取到锁,就执行方法
                //否则直接返回被限流提示信息
                if(tryAcquireFlag){
                    log.debug(Thread.currentThread().getName()+"进入"+name+"方法");
                    //执行方法
                    result=joinPoint.proceed();
                    log.debug(Thread.currentThread().getName()+"离开"+name+"方法");

                    //只有获取到锁并执行完方法后才释放锁
                    //释放一个线程数，默认也是1，会将指定的线程数释放，然后唤醒等待的线程
                    semaphore.release(1);
                }
                else {
                    result = "throttle";
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }finally {
                //释放一个线程数，默认也是1，会将指定的线程数释放，然后唤醒等待的线程
                //semaphore.release(1);
            }
        }
        //方法返回值
        //返回被拦截方法的值(可修改返回值)
        return result;
    }
}
