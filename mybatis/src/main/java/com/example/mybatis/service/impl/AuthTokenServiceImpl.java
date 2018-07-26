package com.example.mybatis.service.impl;

import com.example.mybatis.service.AuthTokenService;
import com.example.mybatis.shiro.bean.AuthTokenEntity;
import com.example.mybatis.shiro.mapper.AuthTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthTokenServiceImpl implements AuthTokenService {
    @Autowired
    private AuthTokenMapper authTokenMapper;
    @Override
    public AuthTokenEntity findTokenByTokenId(String id) {
        return authTokenMapper.findTokenByTokenId(id);
    }
}
