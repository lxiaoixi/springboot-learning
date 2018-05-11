package com.example.helloworld.controller;

import com.example.helloworld.domain.User;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/web")
@RestController
public class WebController {

    @RequestMapping("/getUser")
    public User getUser(){
        User user = new User();
        user.setName("xiaoxi");
        user.setAge(30);
        user.setPass("xing");
        return user;
    }

    @RequestMapping("/getUsers")
    public List<User> getUsers(){
        List<User> users = new ArrayList<User>();
        User user1 = new User();
        user1.setName("xiaoxi");
        user1.setAge(25);
        user1.setPass("boy");
        User user2 = new User();
        user2.setName("xing");
        user2.setAge(23);
        user2.setPass("girl");
        users.add(user1);
        users.add(user2);
        return users;
    }
    //该方法只允许POST请求访问
    @RequestMapping(name="/onlypost",method=RequestMethod.POST)
    public User postUser(){
        User user = new User();
        user.setName("xiaoxi_post");
        user.setAge(10);
        user.setPass("boy_post");
        return user;
    }

    //post和get各种传参方式

    //使用 Url 进行传参，这种形式的传参地址栏会更加美观一些。
    //http://localhost:8080/get/neo
    @GetMapping("/get/{name}")
    public User get(@PathVariable String name){
        User user = new User();
        user.setName(name);
        return user;
    }

    //query-string 传参方式
    @GetMapping("/world")
    //http://localhost:8080/world?name=xiaoxi,传入参数
    public String world(String name){
        return "hello world,"+name;
    }

    //参数是一个类，也可以定义一个param类来接收参数，此处用User类来示范
    @GetMapping("/worldPram")
    public String worldPram(User user){
        System.out.print(user.getName());
        return "hello world,"+user.getName();
    }

    //也可以定义一个param类来接收参数,此处用User类来示范
    @PostMapping("/postPram")
    public String postPram(User user){
        return "hello world,"+user.getName();
    }

    @RequestMapping("/saveUser")
    public void saveUser(@Valid User user, BindingResult result){
        System.out.println("user:"+user);
        if(result.hasErrors()) {
            List<ObjectError> list = result.getAllErrors();
            for (ObjectError error : list) {
                System.out.println(error.getCode() + "-" + error.getDefaultMessage());
            }
        }
    }


}
