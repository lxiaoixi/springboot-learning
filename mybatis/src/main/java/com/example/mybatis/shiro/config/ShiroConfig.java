package com.example.mybatis.shiro.config;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    // securityManager
    @Bean
    public DefaultWebSecurityManager securityManager(DefaultWebSubjectFactory subjectFactory, JWTStatelessAuthorizingRealm stalessRealm, DefaultSessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置subjectFactory
        securityManager.setSubjectFactory(subjectFactory);
        securityManager.setSessionManager(sessionManager);
        securityManager.setRealm(stalessRealm);
        /*
         * 第二 需要禁用使用session 作为存储策略。这个主要由securityManager的subjectDao的sessionStorageEvaluator
         */
        DefaultSubjectDAO defaultSubjectDAO = (DefaultSubjectDAO)securityManager.getSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator =(DefaultSessionStorageEvaluator)defaultSubjectDAO.getSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        return securityManager;
    }
    // shiro filter
    @Bean
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        // 添加自己的过滤器并且取名为jwt
        JWTStatelessAccessControlFilter statelessAccessControlFilter = new JWTStatelessAccessControlFilter();
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt", statelessAccessControlFilter);
        //将jwt Filter添加到Shiro的Filter中
        factoryBean.setFilters(filterMap);

        factoryBean.setSecurityManager(securityManager);
        factoryBean.setUnauthorizedUrl("/401");
        // 自定义url规则
        Map<String,String> filterChainDefinitionMap =new LinkedHashMap<>();
        filterChainDefinitionMap.put("/users/login","anon"); //login路由允许匿名访问
        filterChainDefinitionMap.put("/actuator/*","anon");
        filterChainDefinitionMap.put("/**","jwt");  //所有的路由访问都会先经过jwt Filter进行过滤
        //filterChainDefinitionMap.put("/**","anon");
        factoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return factoryBean;
    }
    /**
     * subject工厂管理器
     */
    @Bean
    public DefaultWebSubjectFactory subjectFactory() {
        return new JWTStatelessDefaultSubjectFactory();
    }
    /**
     * session管理器
     * 第三：需要禁用会话调度器，这个主要由sessionManager进行管理
     */
    @Bean
    public DefaultSessionManager sessionManager() {
        DefaultSessionManager sessionManager = new DefaultSessionManager();
        sessionManager.setSessionValidationSchedulerEnabled(false);
        return sessionManager;
    }
    @Bean
    public JWTStatelessAuthorizingRealm stalessRealm(){
        return new JWTStatelessAuthorizingRealm();
    }
    /**
     * 开启Shiro aop注解支持.
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        // 强制使用cglib，防止重复代理和可能引起代理出错的问题
        // https://zhuanlan.zhihu.com/p/29161098
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
