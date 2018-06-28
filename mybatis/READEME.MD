# springboot-mybatis

* 先配置`application.properties`
* 再配置 `mybatis-config.xml`
* 定义`UserEntity`类 => 配置`UserMapper.xml` => `UserMapper.java` => `UserService` = >`UserController`
* 一般参数传参时定义一个参数类param来封装要传的参数,
* 返回数据定义一个结果类result来封装要返回的数据

# Pagehelper分页插件

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

# 使用http response 实现excel文件的下载

* 读取excel 模板文件（获取classpath也就是resources 下文件）
* 通过http response 输出流 实现文件的下载


# fastJson 配置

## 引入maven依赖
```
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version>${fastjson.version}</version>
    </dependency>
```

# 定时任务（@Schedule注解和Quartz）

# 异常处理封装



# bug

1. pageHelper 配置后如果不需要分页，mapper层就不能带分页参数，否则即使不执行
  ```
    PageHelper.startPage(userParam.getCurrentPage(), userParam.getPageSize())
  ```
  仍会分页
  
2. pageHelper 分页，使用new PageInfo 包装时，必须为返回的dto,不能以新的dto来包装，否则分页会失效，
如果需要处理返回的数据结构，最好是分页的dto和处理结果的dto用同一个dto
  
3. 类一旦使用了注解注入交给spring管理后就不能再使用该类的构造函数了，要么交给spring管理要么不交给spring管理
  否则，会出现注解为null的情形
  
4. `shiro`导致`springboot`事务不起效解决办法

  > app目录为源码文件
  * 该问题是`shiro`比`spring`先启动，导致`shiro`的自定义`realm`中注入的`service`层无法交给`spring`管理导致；有3种解决方案
  * `shiro`的自定义`realm`中不注入`service`，注入`dao`
  * `shiro`的自定义`realm`中注入的`service`上使用`@Lazy`注解，将该`service`对象使用懒加载，亲测有效
  * 创建监听器，等spring接在完成后再加载shiro，参见shiro导致springboot事务不起效解决办法

    