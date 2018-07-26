package com.example.mybatis.shiro.config;

import com.example.mybatis.service.AuthTokenService;
import com.example.mybatis.shiro.bean.AuthTokenEntity;
import com.example.mybatis.shiro.bean.PermissionEntity;
import com.example.mybatis.shiro.param.RolePermissionDto;
import com.example.mybatis.shiro.param.UserRolePermissionDto;
import com.example.mybatis.shiro.service.UsersService;
import com.example.mybatis.util.JWTUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;

import java.util.HashMap;

public class JWTStatelessAuthorizingRealm extends AuthorizingRealm {
    @Value("${sys.token.secret}")
    private String secret;
    @Autowired
    @Lazy
    private UsersService userService;
    @Autowired
    @Lazy
    private AuthTokenService authTokenService;
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTStatelessAuthenticationToken;
    }
    // 授权，将用户角色权限注入到Shiro中，当访问路由时，配置了相应的权限或者shiro标签时会执行此方法
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) throws AuthorizationException {
        //如果打印信息只执行一次的话，说明缓存生效了，否则不生效. --- 配置缓存成功之后，只会执行1次/每个用户，因为每个用户的权限是不一样的.
        System.out.println("MyShiroRealm.doGetAuthorizationInfo()");
        //这是shiro提供的.
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //获取到用户的权限信息.以下根据业务需求自定义实现
        UserRolePermissionDto userInfo = (UserRolePermissionDto)principals.getPrimaryPrincipal();
        if(userInfo != null) {
            for(RolePermissionDto role:userInfo.getRoles()){
                //添加角色.
                authorizationInfo.addRole(role.getName());
                //添加权限.
                for(PermissionEntity p: role.getPermissions()){
                    authorizationInfo.addStringPermission(p.getName());
                }
            }
        }
        return authorizationInfo;
    }

    // 身份认证校验
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //Filter传来的token
        JWTStatelessAuthenticationToken jwtStatelessAuthenticationToken = (JWTStatelessAuthenticationToken) token;
        String jwtToken = jwtStatelessAuthenticationToken.getToken();
        if(jwtToken == null || jwtToken.equals("")) {
            throw new AuthenticationException("token invalid");
        }
        //检验Token
        HashMap<String, String> hashMap = JWTUtil.verify(jwtToken, secret);
        if (hashMap == null) {
            throw new AuthenticationException("token invalid");
        }
        AuthTokenEntity authTokenEntity = authTokenService.findTokenByTokenId(hashMap.get("tokenId"));
        if (authTokenEntity == null) {
            throw new AuthenticationException("token invalid");
        }
        Long uid = Long.valueOf(hashMap.get("uId"));
        //获取该用户的信息
        UserRolePermissionDto userInfo = userService.findUserRolePermissionById(uid);
        if (userInfo == null) {
            throw new AuthenticationException("token invalid");
        }
        return new SimpleAuthenticationInfo(userInfo, jwtToken, getName());
    }
}
