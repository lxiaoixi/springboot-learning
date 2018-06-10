### springboot-mybatis

* 先配置`application.properties`
* 再配置 `mybatis-config.xml`
* 定义`UserEntity`类 => 配置`UserMapper.xml` => `UserMapper.java` => `UserService` = >`UserController`
* 一般参数传参时定义一个参数类param来封装要传的参数,
* 返回数据定义一个结果类result来封装要返回的数据

### Pagehelper分页插件

* 添加Maven依赖

  ```$xslt
    <dependency>
        <groupId>com.github.pagehelper</groupId>
        <artifactId>pagehelper-spring-boot-starter</artifactId>
        <version>1.2.5</version>
    </dependency>
  ```
* pagehelper分页插件配置

  ```$xslt
   pagehelper.helperDialect=mysql
   pagehelper.reasonable=true
   pagehelper.supportMethodsArguments=true
   pagehelper.params=count=countSql
  ```

  https://www.jianshu.com/p/6752c3d2c19d
  https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md
  https://github.com/pagehelper/pagehelper-spring-boot

# 使用 POI 实现 Microsoft Office 格式档案读和写的功能

##主要实现Excel的导出

* https://www.jianshu.com/p/34514b188014
* https://www.jianshu.com/p/1bfdd92bd385
* https://www.yiibai.com/apache_poi/apache_poi_formula.html

## 引入maven依赖
```
    <dependency>
          <groupId>org.apache.poi</groupId>
          <artifactId>poi</artifactId>
          <version>3.17</version>
    </dependency>
    若要导出.xlsx后缀的excel文件，必须引入该依赖
    <dependency>
      <groupId>org.apache.poi</groupId>
      <artifactId>poi-ooxml</artifactId>
      <version>3.17</version>
    </dependency>
```

## 使用http response 实现excel文件的下载
    