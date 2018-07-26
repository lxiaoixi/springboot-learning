//package com.example.mybatis.common.druid;
//
//import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
//import com.alibaba.druid.pool.DruidDataSource;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//@Configuration
//public class MultiDataSourceConfig {
//    @Primary
//    @Bean(name = "oneDataSource")
//    @ConfigurationProperties("spring.datasource.druid.one")
//    public DruidDataSource dataSourceOne(){
//        return DruidDataSourceBuilder.create().build();
//    }
//    @Bean(name = "twoDataSource")
//    @ConfigurationProperties("spring.datasource.druid.two")
//    public DruidDataSource dataSourceTwo(){
//        return DruidDataSourceBuilder.create().build();
//    }
//
//}
