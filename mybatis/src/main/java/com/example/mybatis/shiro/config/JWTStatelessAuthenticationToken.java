package com.example.mybatis.shiro.config;

import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

@Data
public class JWTStatelessAuthenticationToken implements AuthenticationToken {
    private static final long serialVersionUID= 1L;
    private String token;
    JWTStatelessAuthenticationToken(String token) {
        this.token = token;
    }
    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
