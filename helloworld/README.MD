
### 注解

* @RestController  //页面输出以json格式返回
* @Controller      //以页面形式输出
* @RequestMapping  //路由映射
* @PathVariable    //(Restful 风格接口获取参数)
* @Autowired       //装配Bean,注入对象,引用对象（默认按类型装配）@Autowired(required=false) //允许为null
* @Resource        //装配Bean,注入对象,引用对象（默认按名称装配，当找不到与名称匹配的bean时才按照类型进行装配）
                   //@Resource(name="user")
                   //private User user;
* @Data            //注解在类上, 为类提供读写属性,不用再手动添加getter和setter方法 此外还提供了 equals()、hashCode()、toString() 方法，可简化代码，需添加Lombok
* @Value           // 获取配置文件自定义的属性
* @Component       //泛指组件，当组件不好归类的时候，我们可以使用这个注解进行标注。
* @Repository      //持久层,用于标注数据访问组件，即DAO组件
* @Service         //业务层,用于标注业务层组件,Service层
* @Configuration
* @Bean
* @MapperScan      //添加对 Mapper 包扫描,自动加载包路径下的 Mapper @MapperScan("com.example.mybatis.mapper")

JAVA 文件相对路径和绝对路径
单元测试 Mockmvc
输入输出流