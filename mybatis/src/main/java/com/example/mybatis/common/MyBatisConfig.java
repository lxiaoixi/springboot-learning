package com.example.mybatis.common;

import com.example.mybatis.util.myBatisConfigUtil;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;


@Configuration
@PropertySource("classpath:application.properties")
public class MyBatisConfig {
    private static Logger logger = LoggerFactory.getLogger(MyBatisConfig.class);
    private final Environment env;

    @Autowired
    public MyBatisConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource)
            throws Exception {
        return myBatisConfigUtil.sqlSessionFactory(dataSource,env,logger);
    }
}
