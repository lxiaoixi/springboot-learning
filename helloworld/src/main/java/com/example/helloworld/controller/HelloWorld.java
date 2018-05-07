package com.example.helloworld.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController 的意思就是 controller 里面的方法都以 json 格式输出，不用再配置什么 jackjson 的了
//如果配置为@Controller 就代表着输出为页面内容

@RestController
@RequestMapping("/api") //该controller下的路由前缀
public class HelloWorld {

    //@RequestMapping 映射路由
    @RequestMapping("/hello")
    public String hello(){
        return "hello world";
    }

    @RequestMapping("/worldquery")
    //http://localhost:8080/world?name=xiaoxi,传入参数
    public String world(String name){
        return "hello world,"+name;
    }
}
