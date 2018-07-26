package com.example.mybatis.shiro.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UsersParam {

    public static interface UserAddGroup {};
    public static interface UserUpdateGroup {};
    public static interface UserLoginGroup{};
    private Long id;
    @NotNull(message="{user.name.notBlank}",groups = {UserAddGroup.class, UserUpdateGroup.class})
    private String username;
    private String phoneNumber;
    @NotBlank(message = "{user.UserForm.email}", groups = {UserAddGroup.class, UserUpdateGroup.class,UserLoginGroup.class})
    @Email(message = "{user.UserForm.email}", groups = {UserAddGroup.class, UserUpdateGroup.class,UserLoginGroup.class})
    private String email;
    private String nickname;
    private Integer age;
    @NotBlank(message = "{user.UserForm.password}", groups = {UserAddGroup.class,UserLoginGroup.class})
    @Length(min = 6, max = 30, message = "{user.UserForm.password}",groups = {UserAddGroup.class,UserLoginGroup.class})
    private String password;
    private String salt;
    private Long updateAt;
    private Long createAt;
}
