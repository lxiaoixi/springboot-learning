package com.example.mybatis.shiro.service;

import com.example.mybatis.param.PageParam;
import com.example.mybatis.shiro.bean.UsersEntity;
import com.example.mybatis.shiro.param.UserRolePermissionDto;
import com.example.mybatis.shiro.param.UsersParam;

import java.util.List;

public interface UsersService {
    String addUser(UsersParam usersParam);
    String login(UsersParam usersParam);
    UserRolePermissionDto findUserRolePermissionById(Long id);
    List<UsersEntity> findUsersByPage(UsersParam usersParam, PageParam pageParam);
    void deleteUserById(Long id);
    void updateUser(UsersParam usersParam);
}
