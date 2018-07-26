package com.example.mybatis.shiro.controller;

import com.example.mybatis.common.exception.ParamsInvalidException;
import com.example.mybatis.param.PageParam;
import com.example.mybatis.shiro.bean.UsersEntity;
import com.example.mybatis.shiro.param.UserRolePermissionDto;
import com.example.mybatis.shiro.param.UsersParam;
import com.example.mybatis.shiro.service.UsersService;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UsersController {
    @Autowired
    private UsersService usersService;

    @RequiresRoles("admin")
    @PostMapping(value="/users")
    public Map<String,Long> addUser(@Validated(UsersParam.UserAddGroup.class) @RequestBody  UsersParam usersParam){
        String isExit = usersService.addUser(usersParam);
        if(isExit.equals("userIsExits")){
            throw new ParamsInvalidException(2009,"该邮箱已经被注册");
        }
        Map<String,Long> returnMap = new HashMap<>();
        returnMap.put("id",usersParam.getId());
        return returnMap;
    }

    @PostMapping("/users/login")
    public Map<String,String> login(@Validated(UsersParam.UserLoginGroup.class) @RequestBody  UsersParam usersParam){
        String result = usersService.login(usersParam);
        if(result.equals("userIsNotExits")||result.equals("passwordIsWrong")){
            throw new ParamsInvalidException(2009,"用户名或密码不正确");
        }
        Map<String,String> returnMap = new HashMap<>();
        returnMap.put("token",result);
        return returnMap;
    }

    @RequiresPermissions("user:update")
    @PutMapping("/users/{id}")
    public Map<String, Long> updateUserById(@PathVariable Long id,@Validated(UsersParam.UserUpdateGroup.class) @RequestBody  UsersParam usersParam){
        usersParam.setId(id);
        usersService.updateUser(usersParam);
        Map<String, Long> returnMap = new HashMap<>();
        returnMap.put("id", id);
        return returnMap;
    }

    @RequiresPermissions("user:delete")
    @DeleteMapping("/users/{id}")
    public ResponseEntity deleteUserById(@PathVariable Long id){
        usersService.deleteUserById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequiresPermissions("user:find")
    @GetMapping("/users/{id}")
    public UserRolePermissionDto findUserById(@PathVariable Long id){
        return usersService.findUserRolePermissionById(id);
    }


    @RequiresPermissions("user:list")
    @GetMapping("/users")
    public PageInfo<UsersEntity> getUsersByPage(UsersParam usersParam, PageParam pageParam){
        List<UsersEntity> users = usersService.findUsersByPage(usersParam,pageParam);
        return new PageInfo<>(users);
    }
}
