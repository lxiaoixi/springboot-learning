package com.example.mybatis.entity;

import com.example.mybatis.enums.UserSexEnum;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotEmpty;

@Data
public class UserEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    @NotEmpty(message = "{user.name.notBlank}")
    private String userName;
    private String passWord;
    private String nickName;
    private UserSexEnum userSex;
    private int age;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    //项目中一般都需要打印日志，所有实体的toString()方法都是用简单的"+"，因为每"＋" 一个就会 new 一个 String 对象，这样如果系统内存小的话会暴内存。使用ToStringBuilder就可以避免暴内存这种问题。
}
