package com.example.limit;

import org.springframework.web.client.RestTemplate;

/**
 * @Author jchonker
 * @Date 2021/4/28 15:35
 * @Version 1.0
 */
public class MyTest {
    public static void main(String[] args) {
        //测试信号并发量
        for (int i = 0; i < 50; i++) {
            new Thread(()->{
                //访问目标接口
                RestTemplate restTemplate = new RestTemplate();
                //String.class表示返回值类型
                String forObject = restTemplate.getForObject("http://localhost:9090/hello", String.class);
                System.out.println(forObject);
            }).start();
//            System.out.println("线程:"+Thread.currentThread().getName()+"已启动...");

//            new Thread(()->{
//                //访问目标接口
//                RestTemplate restTemplate = new RestTemplate();
//                //String.class表示返回值类型
//                String forObject = restTemplate.getForObject("http://localhost:8080/hi", String.class);
//                System.out.println(forObject);
//            }).start();
//
//            new Thread(()->{
//                //访问目标接口
//                RestTemplate restTemplate = new RestTemplate();
//                //String.class表示返回值类型
//                String forObject = restTemplate.getForObject("http://localhost:8080/getName?name='张三'", String.class);
//                System.out.println(forObject);
//            }).start();
        }
    }
}
