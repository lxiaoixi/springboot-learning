package com.example.mybatis.shiro.mapper;

import com.example.mybatis.shiro.bean.AuthTokenEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthTokenMapper {
    void addToken(AuthTokenEntity authTokenEntity);
    AuthTokenEntity findTokenByTokenId(String id);
}
