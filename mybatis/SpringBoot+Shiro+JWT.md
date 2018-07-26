# SpringBoot+Shiro+JWT 实现无状态鉴（放弃Cookie、Session,使用jwt token实现无状态鉴权）

## 程序逻辑
1.我们`POST`用户名与密码到`/login`进行登入，如果成功，创健一个token,返回一个加密`token`，失败的话直接返回401错误。
2.之后用户访问每一个需要权限的网址请求必须在`header`中添加`Authorization`字段，例如`Authorization: token`，`token`为密钥。
3.后台会进行`token`的校验，判断如果`token`失效或对应的用户不存在,直接返回未授权401。

## JWT 配置

1.添加`Maven`依赖
```
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>3.2.0</version>
</dependency>

```
2.配置JWT

添加签发和校验token的方法,此处签发token时在`token`中附带了用户的uId信息，同时设置token的过期时间expireTime，
检验`token`时，同时获取`tokenId`和用户`uId`,返回一个HashMap结构
```
public class JWTUtil {
    public static String sign(String uId, String tokenId, String secret, int expireTime) {
        try {
            Date date = new Date(System.currentTimeMillis() + expireTime);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withClaim("uId", uId)
                    .withJWTId(tokenId)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static HashMap<String, String> verify(String token, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("tokenId", jwt.getId());
            hashMap.put("uId", jwt.getClaim("uId").asString());
            return hashMap;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
```

## Shiro配置(JWTToken-->StatelessAccessControlFilter-->StatelessAuthorizingRealm-->ShiroConfig)

> 主要修改 StatelessAuthorizingRealm 的doGetAuthorizationInfo和doGetAuthenticationInfo来实现用户身份校验和将用户权限角色注入到shiro中，交给shiro进行管理。ShiroConfig自定义配置路由规则。

1.添加Maven依赖

```
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring</artifactId>
    <version>1.3.2</version>
</dependency>

```

2.配置自定义的JWT无状态token(StatelessAuthenticationToken)

```
public class StatelessAuthenticationToken implements AuthenticationToken {
    private static final long serialVersionUID= 1L;
    private String token;
    StatelessAuthenticationToken(String token) {
        this.token = token;
    }
    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
```

3.配置JWTFilter(StatelessAccessControlFilter),以后添加到Shiro Filter中,所有的请求都会经过该Filter进行过滤

```
public class StatelessAccessControlFilter extends AccessControlFilter {

    @Override
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        // 允许跨域
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        httpServletResponse.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return isAccessAllowed(request, response, mappedValue) || onAccessDenied(request, response, mappedValue);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o){
        return false;
    }

    /**
     *
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        // 获取header
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //获取web端传来的token，此处可自己设置请求头
        String token = request.getHeader("x-auth");
        StatelessAuthenticationToken statelessAuthenticationToken = new StatelessAuthenticationToken(token);
        try {
            // 委托给Realm进行登录,在realm进行身份认证
            super.getSubject(servletRequest, servletResponse).login(statelessAuthenticationToken);
        } catch (Exception e) {
            // e.printStackTrace();
            // 登录失败.
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            //返回401编码.
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResponse errorResponse = new ErrorResponse(1002,"未授权");
            httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
            httpServletResponse.getWriter().write(JSON.toJSONString(errorResponse));
            return false;// 直接返回请求者.
        }
        return true;// 登录成功.
    }
}

```

4.配置自定义Realm(StatelessAuthorizingRealm)
自己的测试数据大致如下，此处自定义实现
```
{
	"activate": true,
	"createTime": 1528269149000,
	"deleted": false,
	"email": "admin@viathink.com",
	"id": 1,
	"nickname": "admin",
	"phoneNumber": "18653889398",
	"roles": [{
		"createTime": null,
		"id": 2,
		"label": "管理员",
		"name": "admin",
		"permissions": [{
				"createTime": 1531897668738,
				"id": 1,
				"label": "今日各维度数据",
				"name": "dashboard:dimension-count",
				"pageId": 1,
				"parent": null,
				"sort": 0,
				"type": null,
				"updateTime": 1531897668738,
				"url": null
			},
			{
				"createTime": 1531897668738,
				"id": 2,
				"label": "销售额趋势",
				"name": "dashboard:order-trend",
				"pageId": 1,
				"parent": null,
				"sort": 1,
				"type": null,
				"updateTime": 1531897668738,
				"url": null
			},
			{
				"createTime": 1531897668738,
				"id": 50,
				"label": "基因检测业务-未开票明细表-导出Excel",
				"name": "query:order-without-invoice-export",
				"pageId": 18,
				"parent": null,
				"sort": 1,
				"type": null,
				"updateTime": 1531897668738,
				"url": null
			},
			{
				"createTime": 1531897668738,
				"id": 51,
				"label": "月均销量趋势图",
				"name": "dashboard:order-trend-month-avg",
				"pageId": 1,
				"parent": null,
				"sort": 4,
				"type": null,
				"updateTime": 1531897668738,
				"url": null
			},
			{
				"createTime": 1531897668738,
				"id": 52,
				"label": "基因检测业务-月平均销量表",
				"name": "query:order-trend-month-avg",
				"pageId": 19,
				"parent": null,
				"sort": 0,
				"type": null,
				"updateTime": 1531897668738,
				"url": null
			},
			{
				"createTime": 1531897668738,
				"id": 53,
				"label": "基因检测业务-月平均销量表-导出Excel",
				"name": "query:order-trend-month-avg-export",
				"pageId": 19,
				"parent": null,
				"sort": 1,
				"type": null,
				"updateTime": 1531897668738,
				"url": null
			},
			{
				"createTime": 1531897668738,
				"id": 54,
				"label": "转诊业务-业务明细查询",
				"name": "query:transfer-business-detail",
				"pageId": 20,
				"parent": null,
				"sort": 0,
				"type": null,
				"updateTime": 1531897668738,
				"url": null
			},
			{
				"createTime": 1531897668738,
				"id": 120,
				"label": "cro业务-业务明细对比-省份对比查询",
				"name": "report:cro-province-contrast",
				"pageId": 53,
				"parent": null,
				"sort": 0,
				"type": null,
				"updateTime": 1531897668738,
				"url": null
			},
			{
				"createTime": 1531897668738,
				"id": 121,
				"label": "cro业务-业务明细对比-省份对比查询-导出Excel",
				"name": "report:cro-province-contrast-export",
				"pageId": 53,
				"parent": null,
				"sort": 1,
				"type": null,
				"updateTime": 1531897668738,
				"url": null
			},
			{
				"createTime": 1531897668738,
				"id": 122,
				"label": "cro业务-业务明细对比-cro报名项目对比查询",
				"name": "report:cro-registration-contrast",
				"pageId": 54,
				"parent": null,
				"sort": 0,
				"type": null,
				"updateTime": 1531897668738,
				"url": null
			},
			{
				"createTime": 1531897668738,
				"id": 123,
				"label": "cro业务-业务明细对比-cro报名项目对比查询-导出Excel",
				"name": "report:cro-registration-contrast-export",
				"pageId": 54,
				"parent": null,
				"sort": 1,
				"type": null,
				"updateTime": 1531897668738,
				"url": null
			},
			{
				"createTime": 1531897668738,
				"id": 124,
				"label": "cro业务-收入明细查询",
				"name": "report:cro-income-detail",
				"pageId": 55,
				"parent": null,
				"sort": 0,
				"type": null,
				"updateTime": 1531897668738,
				"url": null
			},
			{
				"createTime": 1531897668738,
				"id": 125,
				"label": "cro业务-收入明细查询-导出Excel",
				"name": "report:cro-income-detail-export",
				"pageId": 55,
				"parent": null,
				"sort": 1,
				"type": null,
				"updateTime": 1531897668738,
				"url": null
			}
		],
		"updateTime": 1528269419000
	}],
	"tokenId": null,
	"updateTime": 1528269149000
}

```

```
public class StatelessAuthorizingRealm extends AuthorizingRealm {

    @Value("${sys.token.secret}")
    private String secret;
    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    @Lazy
    private AuthTokenService authTokenService;
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof StatelessAuthenticationToken;
    }
    // 授权，将用户角色权限注入到Shiro中，当访问路由时，配置了相应的权限或者shiro标签时会执行此方法
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //如果打印信息只执行一次的话，说明缓存生效了，否则不生效. --- 配置缓存成功之后，只会执行1次/每个用户，因为每个用户的权限是不一样的.
        System.out.println("MyShiroRealm.doGetAuthorizationInfo()");
        //这是shiro提供的.
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //获取到用户的权限信息.以下根据业务需求自定义实现
        UserRolePermissionDto userInfo = (UserRolePermissionDto)principals.getPrimaryPrincipal();
        if(userInfo != null) {
            for(RolePermissionDto role:userInfo.getRoles()){
                //添加角色.
                authorizationInfo.addRole(role.getName());
                //添加权限.
                for(PermissionEntity p: role.getPermissions()){
                    authorizationInfo.addStringPermission(p.getName());
                }
            }
        }
        return authorizationInfo;
    }

    // 身份认证校验
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //Filter传来的token
        StatelessAuthenticationToken statelessToken = (StatelessAuthenticationToken) token;
        String jwtToken = statelessToken.getToken();
        if(jwtToken == null || jwtToken.equals("")) {
            throw new AuthenticationException("token invalid");
        }
        //检验Token
        HashMap<String, String> hashMap = JWTUtil.verify(jwtToken, secret);
        if (hashMap == null) {
            throw new AuthenticationException("token invalid");
        }
        AuthTokenEntity authTokenEntity = authTokenService.findAuthTokenById(hashMap.get("tokenId"));
        if (authTokenEntity == null) {
            throw new AuthenticationException("token invalid");
        }
        Long uid = Long.valueOf(hashMap.get("uId"));
        //获取该用户的信息，此处根据自己自定义实现
        UserRolePermissionDto userInfo = userService.findUserRolePermissionById(uid);
        if (userInfo == null) {
            throw new AuthenticationException("token invalid");
        }
        return new SimpleAuthenticationInfo(userInfo, jwtToken, getName());
    }
}
```

5.配置Shiro,禁用Session会话管理

```
@Configuration
public class ShiroConfig {
    // securityManager
    @Bean
    public DefaultWebSecurityManager securityManager(DefaultWebSubjectFactory subjectFactory, StatelessAuthorizingRealm stalessRealm, DefaultSessionManager sessionManager) {
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
        StatelessAccessControlFilter statelessAccessControlFilter = new StatelessAccessControlFilter();
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt", statelessAccessControlFilter);
        //将jwt Filter添加到Shiro的Filter中
        factoryBean.setFilters(filterMap);

        factoryBean.setSecurityManager(securityManager);
        factoryBean.setUnauthorizedUrl("/401");
        // 自定义url规则
        Map<String,String> filterChainDefinitionMap =new LinkedHashMap<>();
        filterChainDefinitionMap.put("/login","anon"); //login路由允许匿名访问
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
        return new StatelessDefaultSubjectFactory();
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
    public StatelessAuthorizingRealm stalessRealm(){
        return new StatelessAuthorizingRealm();
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
```

6.自定义会话工厂，关闭session

```
public class StatelessDefaultSubjectFactory extends DefaultWebSubjectFactory {
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

```
## Controller 路由添加路由校验

1.`@RequiresUser` 用户登录就可以访问
```
@RequiresUser
@GetMapping(value = "/users/current")
public UserRoleMapDto getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    UserEntity user = (UserEntity) subject.getPrincipal();
    return userService.findUserRoleByUserId(user.getId());
}
```
2.`@RequiresRoles`用户拥有`admin`角色才能访问

```
@RequiresRoles("admin")
@GetMapping(value = "/users/{id}")
public UserRoleResultDto getUserById(@PathVariable Long id) {
    UserRoleResultDto userRoleResultDto = userService.findUserByIdJoinRole(id);
    if (userRoleResultDto == null) {
        throw new ParamsInvalidException(2008,"此id对应的用户不存在");
    }
    return userRoleResultDto;
}
```
3.`@RequiresPermissions`用户拥有`report:cro-business-detail`权限才能访问

```
@RequiresPermissions("report:cro-business-detail")
@GetMapping(value = "/business-detail")
public JSONObject getBusinessDetailReport(@Valid ReportDetailParamDto reportDetailParamDto) {
    return ReportControllerUtil.getSnapshootByClass(snapshootService, reportDetailParamDto, BatchUtil.TYPE_CRO_BUSINESS_DETAIL);
}
```
4.设置多个值，权限值`value`用数组代替，再设置`logical`

```
多选一：logical = Logical.OR
@RequiresPermissions(value = { "product_create", "product_edit" }, logical = Logical.OR)
必须全部符合：logical = Logical.AND
@RequiresPermissions(value = { "product_create", "product_edit" }, logical = Logical.AND)
```

## 参考
https://juejin.im/post/59f1b2766fb9a0450e755993