package com.example.mybatis.service;

import com.example.mybatis.entity.UserEntity;
import com.example.mybatis.mapper.UserMapper;
import com.example.mybatis.param.UserParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImp implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public String saveUser(UserEntity userEntity){
        userMapper.addUser(userEntity);
        return "save success";
    }

    @Override
    public String deleteUserById(Long id) {
        userMapper.deleteUser(id);
        return "delete success";
    }

    @Override
    public String updateUserById(UserEntity userEntity) {
        userMapper.updateUser(userEntity);
        return "update success";
    }

    @Override
    public UserEntity findUserById(Long id) {
        UserEntity userEntity = userMapper.getOne(id);
        return userEntity;
    }

    @Override
    public List<UserEntity> getUserList() {
        List<UserEntity>  users =  userMapper.getAll();
        return users;
    }

    @Override
    public List<UserEntity> userListByPage(UserParam userParam) {
        return userMapper.getList(userParam);
    }

    @Override
    public int countUsers(UserParam userParam) {
        return userMapper.getCount(userParam);
    }
}
