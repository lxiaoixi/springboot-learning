package com.example.mybatis.shiro.param;

import com.example.mybatis.shiro.bean.PermissionEntity;
import com.example.mybatis.shiro.bean.RoleEntity;
import lombok.Data;

import java.util.List;

@Data
public class RolePermissionDto extends RoleEntity {
    List<PermissionEntity> permissions;
}
