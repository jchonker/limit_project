package com.example.limit.controller;

import com.example.limit.annotation.ApiRateLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Author jchonker
 * @Date 2021/4/28 15:33
 * @Version 1.0
 */
@Slf4j
@Controller
public class HelloController {
    @ApiRateLimit(10)
    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
        //假设业务逻辑执行了x秒
        sleep();
        return "success";
    }

//    @ApiRateLimit(5)
//    @RequestMapping("/hi")
//    @ResponseBody
//    public String hi(){
//        //假设业务逻辑执行了x秒
//        sleep();
//        return "hi";
//    }

    /**
     * 睡眠随机时间
     */
    void sleep(){
        try {
            Random random=new Random();
            int a= random.nextInt(10);
            TimeUnit.SECONDS.sleep(a);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
