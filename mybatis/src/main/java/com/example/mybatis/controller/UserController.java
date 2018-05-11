package com.example.mybatis.controller;

import com.example.mybatis.entity.UserEntity;
import com.example.mybatis.param.UserParam;
import com.example.mybatis.result.Page;
import com.example.mybatis.service.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    //@Value获取application.properties配置中的属性
    @Value("${com.example.mybatis.name}")
    private String myname;

    @Autowired
    private UserServiceImp userServiceImp;

    @PostMapping("/add")
    public String saveUser(UserEntity userEntity){
        String data = userServiceImp.saveUser(userEntity);
        System.out.print(data);
        return data;
    }

    @PostMapping("/delete")
    public String deleteUser(Long id){
        return userServiceImp.deleteUserById(id);
    }

    @PostMapping("/update")
    public String updateUser(UserEntity userEntity){
        return userServiceImp.updateUserById(userEntity);
    }

    @GetMapping("/search/{id}")
    public UserEntity findUserById(@PathVariable Long id){
        UserEntity userEntity = userServiceImp.findUserById(id);
        System.out.print(userEntity.getUserSex());
        System.out.print(userEntity.getUserName());
        return userEntity;
    }

    @GetMapping("/search/users")
    public List<UserEntity> findAllUsers(){
        System.out.print(myname);
        return userServiceImp.getUserList();
    }

    @GetMapping("/search/pages/users")
    public Page<UserEntity> searchUserByPage(UserParam userParam){
        List<UserEntity> users =  userServiceImp.userListByPage(userParam);
        int sum = userServiceImp.countUsers(userParam);
        //构造返回结果类
        Page page = new Page(userParam.getCurrentPage(),userParam.getPageSize(),sum,users);
        return page;
    }


}
