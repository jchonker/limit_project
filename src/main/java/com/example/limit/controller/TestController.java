package com.example.limit.controller;

import com.example.limit.annotation.ApiRateLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeUnit;

/**
 * @Author jchonker
 * @Date 2021/4/28 15:34
 * @Version 1.0
 */
@Slf4j
@Controller
public class TestController {
    @ApiRateLimit(5)
    @RequestMapping("/getName")
    @ResponseBody
    public String getName(String name){
        //假设业务逻辑执行了两秒
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return name;
    }
}
