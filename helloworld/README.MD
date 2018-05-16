
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
* @Configuration   //一般这里面会定义Bean，会把这个类中bean加载到spring容器中
* @Bean            //将类的生命周期交给spring,可以用在方法上面，在项目中直接注入该方法
* @MapperScan      //添加对 Mapper 包扫描,自动加载包路径下的 Mapper @MapperScan("com.example.mybatis.mapper")


### 接口

* InitializingBean

  实现InitializingBean的类，为bean提供了初始化方法的方式,在类加载后会调用afterPropertiesSet()方法
* DisposableBean
  
  实现DisposableBean接口的类，在类销毁时，会调用destroy()方法
  
  ```$xslt
    public class TestBean implements InitializingBean, DisposableBean  
    {  
        public void afterPropertiesSet(){  
            //bean加载后调用  
        }  
          
        public void destroy() {  
            //bean销毁时调用  
        }  
    }  

  ```
  
* CommandLineRunner

    Spring Boot 启动加载数据,在项目服务启动的时候就去加载一些数据或做一些事情。
    可使用@Order 注解来定义执行顺序。@Order 注解的执行优先级是按value值从小到大顺序
    ```$xslt
    @Component
    @Order(value=2)
    public class MyStartupRunner1 implements CommandLineRunner {
    
        @Override
        public void run(String... args) throws Exception {
            System.out.println(">>>>>>>>>>>>>>>服务启动执行，执行加载数据等操作<<<<<<<<<<<<<");
        }
    
    }
    ```
    
    
JAVA 文件相对路径和绝对路径
单元测试 Mockmvc
输入输出流