package com.example.mybatis.shiro.bean;

import lombok.Data;

@Data
public class RolePermissionEntity {
    private static final long serialVersionUID = 1L;
    private Long permissionId;
    private Long roleId;
    private Long updateAt;
    private Long createAt;
}
