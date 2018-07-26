//package com.example.mybatis.common.druid;
//
//import com.example.mybatis.util.myBatisConfigUtil;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionTemplate;
//import org.mybatis.spring.annotation.MapperScan;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.core.env.Environment;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//
//import javax.sql.DataSource;
//
//
//@Configuration
//@PropertySource("classpath:application.properties")
//@MapperScan(basePackages = "com.example.mybatis.mapper.one", sqlSessionTemplateRef  = "oneSqlSessionTemplate")
//public class OneDataSourceConfig {
//    private static Logger logger = LoggerFactory.getLogger(OneDataSourceConfig.class);
//    private final Environment env;
//
//    @Autowired
//    public OneDataSourceConfig(Environment env) {
//        this.env = env;
//    }
////    @Bean
////    public SqlSessionFactory sqlSessionFactory(DataSource dataSource)
////            throws Exception {
////        return myBatisConfigUtil.sqlSessionFactory(dataSource,env,logger);
////    }
//
//    //1.将第一个数据源注入到sqlSessionFactory
//    @Bean(name = "oneSqlSessionFactory")
//    @Primary
//    public SqlSessionFactory sqlSessionFactory(@Qualifier("oneDataSource")DataSource dataSource)
//            throws Exception {
//        return myBatisConfigUtil.sqlSessionFactory(dataSource,env,logger);
//    }
//    //2.将数据源添加到事物中
//    @Bean(name = "oneTransactionManager")
//    @Primary
//    public DataSourceTransactionManager testTransactionManager(@Qualifier("oneDataSource") DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }
//
//    //3.将上面创建的SqlSessionFactory注入，创建在 Mapper 中需要使用的SqlSessionTemplate。
//    @Bean(name = "oneSqlSessionTemplate")
//    @Primary
//    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("oneSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
//
//    //4.将上面创建的SqlSessionTemplate注入到对应的 Mapper 包路径下，这样这个包下面的 Mapper 都会使用第一个数据源来进行数据库操作
//
//
//}
