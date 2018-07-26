package com.example.mybatis.shiro.service.impl;

import com.example.mybatis.param.PageParam;
import com.example.mybatis.shiro.bean.AuthTokenEntity;
import com.example.mybatis.shiro.bean.UsersEntity;
import com.example.mybatis.shiro.mapper.AuthTokenMapper;
import com.example.mybatis.shiro.mapper.UsersMapper;
import com.example.mybatis.shiro.param.UserRolePermissionDto;
import com.example.mybatis.shiro.param.UsersParam;
import com.example.mybatis.shiro.service.UsersService;
import com.example.mybatis.util.CryptoUtil;
import com.example.mybatis.util.JWTUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsersServiceImpl implements UsersService {
    @Value("${sys.token.secret}")
    private String secret;
    @Value("${sys.token.jwt-expire-time}")
    private int jwtExpireTime;

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private AuthTokenMapper authTokenMapper;

    @Override
    public String addUser(UsersParam usersParam) {
        //查看用户是否已存在
        UsersEntity usersEntity = usersMapper.findUserByEmail(usersParam.getEmail());
        if(usersEntity!=null){
            return "userIsExits";
        }
        //密码通过盐进行加密
        String salt = CryptoUtil.getRandomSalt();
        String password = CryptoUtil.sha1Hash(usersParam.getPassword(),salt);
        usersParam.setSalt(salt);
        usersParam.setPassword(password);
        usersMapper.addUser(usersParam);
        return "success";
    }

    @Override
    public String login(UsersParam usersParam) {
        //查看用户是否存在
        UsersEntity usersEntity = usersMapper.findUserByEmail(usersParam.getEmail());
        if(usersEntity==null){
            return "userIsNotExits";
        }
        //检验密码是否正确
        String salt = usersEntity.getSalt();
        String password = CryptoUtil.sha1Hash(usersParam.getPassword(),salt);
        if(!password.equals(usersEntity.getPassword())){
            return "passwordIsWrong";
        }
        //签发token
        String tokenId = CryptoUtil.getRandomSalt();
        //添加token-user记录表
        AuthTokenEntity authTokenEntity = new AuthTokenEntity();
        authTokenEntity.setId(tokenId);
        authTokenEntity.setUserId(String.valueOf(usersEntity.getId()));
        authTokenMapper.addToken(authTokenEntity);
        return JWTUtil.sign(String.valueOf(usersEntity.getId()),tokenId,secret,jwtExpireTime);
    }

    @Override
    public UserRolePermissionDto findUserRolePermissionById(Long id) {
        return usersMapper.findUserRolePermissionById(id);
    }

    @Override
    public List<UsersEntity> findUsersByPage(UsersParam usersParam, PageParam pageParam) {
        PageHelper.startPage(pageParam.getCurrentPage(),pageParam.getPageSize());
        return usersMapper.findUsersByPage(usersParam);
    }

    @Override
    public void deleteUserById(Long id) {
        usersMapper.deleteUserById(id);
    }

    @Override
    public void updateUser(UsersParam usersParam) {
        usersMapper.updateUser(usersParam);
    }
}
