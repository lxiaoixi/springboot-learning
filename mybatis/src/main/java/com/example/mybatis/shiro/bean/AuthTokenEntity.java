package com.example.mybatis.shiro.bean;

import lombok.Data;

@Data
public class AuthTokenEntity {
    private static final long serialVersionUID = 1L;
    private String id;
    private String userId;
    private Long updateAt;
    private Long createAt;
}
