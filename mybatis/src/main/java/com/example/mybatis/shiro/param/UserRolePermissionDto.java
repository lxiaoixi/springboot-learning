package com.example.mybatis.shiro.param;

import com.example.mybatis.shiro.bean.UsersEntity;
import lombok.Data;

import java.util.List;

@Data
public class UserRolePermissionDto extends UsersEntity {
    private List<RolePermissionDto> roles;
}
