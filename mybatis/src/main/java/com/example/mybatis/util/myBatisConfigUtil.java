package com.example.mybatis.util;


import org.apache.ibatis.io.VFS;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.slf4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class myBatisConfigUtil {
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private static String setTypeAliasesPackage(String typeAliasesPackage,Logger logger) {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
                resolver);
        typeAliasesPackage = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(typeAliasesPackage)
                + "/" + DEFAULT_RESOURCE_PATTERN;
        try {
            List<String> result = new ArrayList<>();
            Resource[] resources = resolver.getResources(typeAliasesPackage);
            if (resources.length > 0) {
                MetadataReader metadataReader;
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        metadataReader = metadataReaderFactory
                                .getMetadataReader(resource);
                        try {
                            // System.out.println(Class.forName(metadataReader.getClassMetadata().getClassName()).getPackage().getName());
                            result.add(Class
                                    .forName(
                                            metadataReader.getClassMetadata()
                                                    .getClassName())
                                    .getPackage().getName());
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (result.size() > 0) {
                HashSet<String> h = new HashSet<>(result);
                result.clear();
                result.addAll(h);
                typeAliasesPackage = String.join(",", (String[]) result.toArray(new String[0]));
                logger.info("typeAliasesPackage" + typeAliasesPackage);
            } else {
                throw new RuntimeException(
                        "mybatis typeAliasesPackage 路径扫描错误,参数typeAliasesPackage:"
                                + typeAliasesPackage + "未找到任何包");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return typeAliasesPackage;
    }

    public static SqlSessionFactory sqlSessionFactory(DataSource dataSource, Environment env, Logger logger)
            throws Exception {
        logger.info("-------配置[typeAliasesPackage,mapperLocations]START-------");
        VFS.addImplClass(SpringBootVFS.class);
        String typeAliasesPackage = env
                .getProperty("mybatis.type-aliases-package");
        String mapperLocations = env.getProperty("mybatis.mapper-locations");
        typeAliasesPackage = setTypeAliasesPackage(typeAliasesPackage,logger);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setUseGeneratedKeys(true); //使用jdbc的getGeneratedKeys获取数据库自增主键值
        configuration.setMapUnderscoreToCamelCase(true);//-自动使用驼峰命名属性映射字段   userId    user_id
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setConfiguration(configuration);
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTypeAliasesPackage(typeAliasesPackage);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        logger.info("-------配置[typeAliasesPackage,mapperLocations]END-------");
        return sessionFactory.getObject();
    }
}

