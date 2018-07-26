package com.example.mybatis.shiro.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class UsersEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String username;
    private String phoneNumber;
    private String email;
    private String nickname;
    private Integer age;
    @JSONField(serialize = false)  //@JSONField注解该字段前端不返回
    private String password;
    @JSONField(serialize = false)
    private String salt;
    private Long updateAt;
    private Long createAt;
}
