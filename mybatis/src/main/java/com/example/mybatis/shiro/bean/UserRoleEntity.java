package com.example.mybatis.shiro.bean;

import lombok.Data;

@Data
public class UserRoleEntity {
    private static final long serialVersionUID = 1L;
    private Long userId;
    private Long roleId;
    private Long updateAt;
    private Long createAt;
}
