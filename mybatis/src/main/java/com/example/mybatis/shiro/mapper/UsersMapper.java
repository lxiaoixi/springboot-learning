package com.example.mybatis.shiro.mapper;

import com.example.mybatis.shiro.bean.UsersEntity;
import com.example.mybatis.shiro.param.UserRolePermissionDto;
import com.example.mybatis.shiro.param.UsersParam;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersMapper {
    void addUser(UsersParam usersParam);
    UsersEntity findUserByEmail(String email);
    UserRolePermissionDto findUserRolePermissionById(Long id);
    List<UsersEntity> findUsersByPage(UsersParam usersParam);
    void deleteUserById(Long id);
    void updateUser(UsersParam usersParam);
}
