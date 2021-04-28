package com.example.limit.configuration;

import com.example.limit.annotation.ApiRateLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * @Author jchonker
 * @Date 2021/4/28 15:30
 * @Version 1.0
 */
@Slf4j
@Component
public class InitApiLimit implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //找出标注了@Controller注解的所有bean  key是beanName value是bean实例
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Controller.class);
        log.debug("一共有"+beansWithAnnotation.size()+"个Controller");
        //查看具体有哪些Controller类
//        beansWithAnnotation.forEach((k,v)->{
//            log.debug(k+" "+v);
//        });
//        log.debug("------");

        //遍历所有Controller
        beansWithAnnotation.forEach((k,v)->{
            //获取类的class对象的两种方式：
            //① 如果你知道一个实例，那么你可以通过实例的“getClass()”方法获得该对象的class对象
            //② 如果你知道一个类型（String名称），那么你可以使用“类名.class”的方法获得该类型的class对象
            Class<?> controllerClass = v.getClass();
            log.debug("子类："+controllerClass.toString());
            log.debug("父类："+controllerClass.getSuperclass().toString());

            //获取所有声明的方法
            Method[] declaredMethods = controllerClass.getSuperclass().getDeclaredMethods();
            Arrays.stream(declaredMethods).forEach(declaredMethod->{
                log.debug("方法名："+declaredMethod.getName());
                //判断方法是否使用了限流注解
                if (declaredMethod.isAnnotationPresent(ApiRateLimit.class)){
                    //获取限流注解
                    ApiRateLimit annotation = declaredMethod.getAnnotation(ApiRateLimit.class);
                    boolean fair = annotation.fair();
                    int value = annotation.value();
                    //key为方法名.value为具体限流量,fair是否使用公平锁,传递到切面的map中
                    ApiLimitAspect.semaphoreMap.put(declaredMethod.getName(),new Semaphore(value,fair));
                }
            });
            log.debug("信号量个数："+ApiLimitAspect.semaphoreMap.size());
        });
    }
}
