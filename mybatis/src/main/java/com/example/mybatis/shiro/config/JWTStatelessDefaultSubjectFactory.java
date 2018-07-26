package com.example.mybatis.shiro.config;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

public class JWTStatelessDefaultSubjectFactory extends DefaultWebSubjectFactory {
    /**
     * 第一 SubjectContext在创建的时候，需要关闭session创建，这个主要由
     * DefaultWebSubjectFactory的createSubject进行管理
     */
    @Override
    public Subject createSubject(SubjectContext context) {
        // 禁止创建session
        context.setSessionCreationEnabled(false);
        return super.createSubject(context);
    }
}
