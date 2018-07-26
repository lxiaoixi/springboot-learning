package com.example.mybatis.shiro.bean;

import lombok.Data;

@Data
public class RoleEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String label;
    private Long updateAt;
    private Long createAt;
}
