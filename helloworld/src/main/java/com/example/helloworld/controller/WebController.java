package com.example.helloworld.controller;

import com.example.helloworld.domain.User;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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

    //使用 Url 进行传参，这种形式的传参地址栏会更加美观一些。
    //http://localhost:8080/get/neo
    @RequestMapping(value="get/{name}",method=RequestMethod.GET)
    public User get(@PathVariable String name){
        User user = new User();
        user.setName(name);
        return user;
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
