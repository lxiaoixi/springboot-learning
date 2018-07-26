package com.example.mybatis.service;

import com.example.mybatis.shiro.bean.AuthTokenEntity;

public interface AuthTokenService {
    AuthTokenEntity findTokenByTokenId(String id);
}
