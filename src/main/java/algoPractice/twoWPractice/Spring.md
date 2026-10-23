# Spring 与 Spring Boot 高级面试 60 题

## 使用说明

这份题单面向有多年 Java 经验、熟悉旧版 Spring MVC、目标是快速突破高级岗位面试的候选人。排序综合了 2025—2026 年面试题集中反复出现的主题，并优先保留能区分“会用”与“懂原理、能排障”的问题；S 级应做到 2 分钟内完整口述，A 级应能应对两轮追问，B 级用于拉开高级候选人的差距。[^1][^2][^3][^4]

当前版本语境以 Spring Framework 7.0.x、Spring Boot 4.1.x 为新版本主线，同时保留 Spring 5/Boot 2 与 Spring 6/Boot 3 的迁移知识。Spring Framework 7.0.9 是当前稳定维护版本，Spring Boot 4.1.0 已正式发布，而 4.2 仍是里程碑版本；回答时不要把 milestone 当成生产稳定版。[^5][^6][^7]

建议统一使用“**结论 → 原理链路 → 项目例子 → 边界/坑**”回答。以下不包含 Spring AI。

## S级：最高频核心题

### 1. Spring 与 Spring Boot 有什么区别？

**口述答案：** Spring Framework 提供 IoC、AOP、事务、MVC、数据访问等基础能力；Spring Boot 是建立在 Spring 之上的工程化约定层，核心价值是自动配置、starter 依赖、内嵌运行时、外部化配置和生产监控。Boot 没有替代 Spring，底层仍然创建并管理 Spring `ApplicationContext`；它主要解决“如何快速、统一、可运维地组装 Spring 应用”。[^8][^9][^10]

**追问：** 不用 Boot 能否开发微服务？可以，只是依赖、Servlet、配置、监控和打包需要自行组装。  
**易错点：** 不要只答“Boot 不用 XML”；Java Config 在普通 Spring 中也能使用。

### 2. `@SpringBootApplication` 到底做了什么？

**口述答案：** 它组合了 `@SpringBootConfiguration`、`@EnableAutoConfiguration` 和 `@ComponentScan`。前者标识主配置并便于测试发现，第二个导入自动配置，第三个从启动类所在包向下扫描组件；因此启动类通常放在根包，否则 Controller、Service、Entity 或 Repository 可能漏扫。[^11][^12]

**追问：** 能否拆开使用？可以，三个能力都不是强制绑定的。  
**易错点：** `@SpringBootApplication` 不会“扫描整个 classpath”。

**补充说明：**  `@SpringBootConfiguration` 相当于 Spring 的 `@Configuration`，如果一个 Class 外层有他们， 这个 Class 的代理有ConfigurationClassPostProcessor 触发，Spring 在启动阶段就会强制通过 CGLIB 为它生成子类代理。而 `@Service / @Component` 修饰的 class 代理由 BeanPostProcessor（如 AnnotationAwareAspectJAutoProxyCreator）触发， 不一定是CGLIB。 前者是用继承 override 方法的方式，后者典型的 AOP 是通过委托代理的方式，只有通过反射发现方法被 @Transaction /@Aysnc 修饰时，这几个方法会加上 AOP 处理逻辑，然后才调用代理类里的委托对象的方法，委托对象就是原始Class 的对象。后者现象：Class的方法 A 内部直接调用 带有事务注释的方法 B， 这个事务不起作用，因为Class 的代理对象方法 A是直接调用原始对象的 A 方法，原始对象的 A 方法直接调用原始对象的 B 方法。 但是`@Configuration`修饰的 Class就会不一样，它的代理对象没有任何委托对象，它override 了 Class 的所有方法。 一般 configuration 修饰的 class 里放的是重要的 Bean，能保证是单例，即使一个 Bean 里直接调用另外一个方法（@Bean 修饰方法）产生的 Bean，后者Bean 永远是单例， 因为这个 Class 会被代理类覆盖，没有原始类了，加了annotation 修饰方法都 override 了， 代理 Class 的内部直接调用（继承自原始 Class）也是调用了 override 的方法。 


### 3. Spring Boot 自动配置原理是什么？

**口述答案：** 启动时 `@EnableAutoConfiguration` 导入候选自动配置；现代 Boot 从各依赖 JAR 的 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 读取类名。自动配置类使用 `@ConditionalOnClass`、`@ConditionalOnBean`、`@ConditionalOnMissingBean`、`@ConditionalOnProperty` 等条件决定是否生效，并通常在用户提供同类型 Bean 后退让。[^13][^10]

**追问：** 旧版本如何发现？Boot 2.6 以前常通过 `spring.factories` 的 `EnableAutoConfiguration` 键加载；面试时应明确这是旧机制。  
**易错点：** “按包扫描自动配置类”是错误说法；自动配置也不等于不能覆盖。

**补充说明：**  这里的退让就是 ConditionalOn 符合提交才会自动加载默认的配置，也就是说如果用户没有自定义，就用默认的，或者像有了数据链接，才启用事务配置。


### 4. Bean 从定义到可用经历哪些关键阶段？

**口述答案：** 高层链路是：读取并注册 `BeanDefinition` → `BeanFactoryPostProcessor` 修改定义 → 实例化 → 属性填充/依赖注入 → Aware 回调 → `BeanPostProcessor#postProcessBeforeInitialization` → `@PostConstruct` → `InitializingBean#afterPropertiesSet` → 自定义 init → `postProcessAfterInitialization`，AOP 代理通常在后处理阶段产生；容器关闭时执行 `@PreDestroy`、`DisposableBean#destroy` 和自定义 destroy。多个初始化机制并用时，官方顺序是 `@PostConstruct`、`afterPropertiesSet`、自定义 init。[^14][^15]

**追问：** 代理对象何时出现？通常由 `BeanPostProcessor` 在初始化后包装。  
**易错点：** prototype Bean 的销毁回调默认不由容器完整托管。

**补充说明：** BeanDefinition 是一个 interface，有多种实现，里面记录着类的信息，是个基础结构图纸”（记录类名、作用域、是否懒加载、依赖关系等） `BeanFactoryPostProcessor` 修改定义比如典型的PropertySourcesPlaceholderConfigurer（BeanFactoryPostProcessor的实现类）遍历DefaultListableBeanFactory 里的 Map<String, BeanDefinition> beanDefinitionMap 发现某个 BeanDefinition 里的属性写着 ${db.url}，从application.properties读取并修改BeanDefinition。 后期当 BeanPostProcessor 处理对应的 Class 的时候，通过反射更改url 属性值。  ApplicationContext 是BeanFactory的子接口，同时内部有BeanFactory的实现DefaultListableBeanFactory，这是装饰者模式，DefaultListableBeanFactory负责 bean 的创建，AOP 这些具体脏活， ApplicationContext本身做了很多扩展，比如国际化支持，事件发布机制， 应用环境感知，所以是BeanFactory的超集。 DefaultListableBeanFactory 里除了包含beanDefinitionMap，还包含Map<String, Object> singletonObjects，List<BeanFactoryPostProcessor> beanFactoryPostProcessors 和 List<BeanPostProcessor> beanPostProcessors， 这些 processor 在 ApplicationContext 的 refresh 里被提前初始化（在普通 bean 初始化前要准备好），其中beanprocessor 实现类CommonAnnotationBeanPostProcessor 专门给 带有 @Transactional @PostConstruct 这个类进行代理类处理。

Bean A 经历的阶段：
 1. 读取并注册 BeanDefinition
    │
 2. BeanFactoryPostProcessor 修改定义
    │
 3. 实例化 (new A_origin)
    │
 4. 属性填充 / 依赖注入 (populateBean)  <─── [循环依赖时：Lambda 在这里被触发，提前做 AOP]
    │
 5. Aware 接口回调 (BeanNameAware, BeanFactoryAware 等) （ApplicationContextAware 会注入 Spring 容器上下文 context 到 Bean 的成员变量里）
    │
 6. BeanPostProcessor#postProcessBeforeInitialization (前置处理，如 @PostConstruct)
    │
 7. InitializingBean#afterPropertiesSet (标准初始化接口)
    │
 8. 自定义 init-method (如 @Bean(initMethod="init") 或 XML 指定的 init-method)
    │
 9. BeanPostProcessor#postProcessAfterInitialization (后置处理)  <─── [正常无循环依赖时：AOP 代理在这里产生！]





### 5. IoC 和 DI 怎么解释才不像背概念？

**口述答案：** IoC 是控制权反转：对象不再自行 `new` 或查找依赖，而由容器负责创建、组装和生命周期；DI 是 IoC 的主要实现方式，依赖通过构造器、工厂方法或属性传入。工程上优先构造器注入，因为必需依赖显式、对象可保持不可变、测试更直接；可选依赖才考虑 setter 或 `ObjectProvider`。[^16][^17]

**追问：** 为什么不推荐字段注入？隐藏依赖、难做纯单元测试、难保证不可变，也更容易形成循环依赖。  Dependency Injection 的缩写，中文翻译为 “依赖注入”。 IoC（Inversion of Control，控制反转）
**易错点：** IoC 不等同于 DI，DI 是实现 IoC 的一种方式。  

### 6. `BeanFactory` 与 `ApplicationContext` 有何区别？

**口述答案：** `BeanFactory` 是 IoC 的底层容器接口，提供实例化和依赖装配；`ApplicationContext` 是其超集，自动注册后处理器，并增加事件、国际化、资源访问、AOP 集成和应用层上下文能力。业务应用几乎总用 `ApplicationContext`；直接使用裸 `BeanFactory` 时，很多后处理器需要手工注册。[^18][^16]

**追问：** “BeanFactory 延迟加载、ApplicationContext 全部立即加载”准确吗？不准确，是否延迟还受 scope、`lazy-init` 和获取方式影响。 Prototype（多例模式）： 不管BeanFactory或者ApplicationContext，getBean 时候才加载，懒加载。@Lazy注释的 service 也是懒加载。如果 B 是懒加载，被 A 立即加载 autowire，B 也变成了立即加载。BeanFactory调用preInstantiateSingletons，懒加载也变成了立即加载。 
**易错点：** 不要只用“一个轻量、一个重量”回答。

**补充说明：**  ApplicationContext 的refresh 方法会调用 DefaultListableBeanFactory 的preInstantiateSingletons方法， 这样就会立即把所有非懒加载的单例 Bean 全造出来！ApplicationContext的事件发布和国际化由 ApplicationContext实现了ApplicationEventPublisher接口，spring 会自动注入ApplicationEventPublisher eventPublisher， service 里可以发布各种ApplicationEvent，对应注册过的 Listener 会监听得到消息处理。 个人觉得大部分情况都被 MQ 取代，这个实在同一个 JVM 内处理。这里用到了观察者模式（Observer Pattern）。国际化ApplicationContext 本身继承了 MessageSource 接口，它内置了文本翻译和占位符替换能力。自动注入的messageSource可以从多语言版本messages.properties 中拿取多语言，例如messageSource.getMessage("welcome.msg", new Object[]{username}, locale); 
模式的补充：`ApplicationContext` 是其超集， 拥有了`BeanFactory`不具备的其他功能，视为装饰者模式，而 业务 AOP 实现的时候也有委托对象，代理类也原始类的子类，但是它不提供新功能，而是专注于在老功能上加上切面，这种叫代理模式，虽然实现上相似。


### 7. 同一接口有多个 Bean，Spring 如何决定注入哪个？

**口述答案：** 先按类型找候选，再结合 `@Qualifier`、`@Primary`、新版本的 `@Fallback`、注入点名称和优先级缩小范围。`@Primary` 是单值注入时的首选；`@Qualifier` 是对候选集合做语义筛选；Spring 6.2 的 `@Fallback` 把 Bean 标成后备项，当只剩一个普通候选时它自然胜出。[^19][^20]   

**追问：** 注入 `List<Strategy>` 怎么排序？使用 `@Order` 或 `Ordered`。  
**易错点：** `@Qualifier("x")` 不应简单理解成严格的 Bean id 引用。

**补充说明：**   @Qualifier (精准匹配) > @Primary (首选) > 普通 Bean > @Fallback (兜底) 。注入 `List<Strategy>` 意思是符合Strategy接口的 bean 都被注入，策略模式（Strategy Pattern）。Qualifier 的本质是“分类/标签”（Qualifier = Tag/Qualifier),List<Strategy>加@Qualifier("specificTag")只选有@Qualifier("specificTag")的 bean。

### 8. Spring 如何处理循环依赖？为什么构造器循环依赖失败？

**口述答案：** 构造器循环依赖在对象尚未实例化时就互相等待，容器会抛出 `BeanCurrentlyInCreationException`。经典单例 setter/字段循环依赖可借助提前暴露引用解决，但它增加半初始化对象和代理一致性风险；Boot 2.6 起默认禁止循环引用。正确方案是重构职责，必要时才用 `@Lazy` 或 `ObjectProvider` 延迟获取。[^21][^22][^17]

**追问：** 三级缓存是什么？应能解释 singletonObjects、earlySingletonObjects、singletonFactories 的角色，以及第三级允许提前创建代理引用。  
**易错点：** 不要把“Spring 能解决循环依赖”说成无条件成立，prototype 和构造器环都不能靠经典三级缓存解决。

**补充说明：** @Scope("prototype") 修饰的ServiceA, 依赖@Scope("prototype") 修饰的ServiceB, 构造 A 的时候需要新的 B，新的 B 又要新的 A，这样无线循环，所以检查到prototypesCurrentlyInCreation存在，就报错。 AbstractAutowireCapableBeanFactory 源码中，实例化 Bean 后会调用 addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean))， singletonFactories 存的是Map<String, ObjectFactory<?>>类型的 entry，如果 A 实例有循环依赖 B，尝试初始化 B 的时候，B 会从一二三里查，查到三级的时候发现 A 的 lambda（ObjectFactory），就会调用，后续 A 如果有 AOP，生成代理对象（注意是 proxy，后续回到 A，接着是 Bean A 的后处理，而不是该proxy，只是处理的最后准备产生代理的时候发现代理已经提前生产了。） 放入二级缓存，B 的依赖解决了，B 完成放入一级缓存，再回到 A 的依赖解决，后续处理完了从二级移入一级缓存。如果 A 没有循环依赖，三级缓存的 lambda 不会被别人取出来调用，A 接着创建放入了一级。
第三级的意义：保存了 原始 Bean 的引用 + AOP 代理逻辑， 这是个暂存的缓存，不用和其他缓存混合，逻辑分离。 当 A 循环依赖 B 和 C 的时候，B 完成，C 从二级缓存就能拿到 A 的代理半成品，逻辑上就知道它是个 A 的 proxy，没有是 A 原始 Bean 的歧义。

1. A 实例化 (new A_origin)
   └── 栈帧局部变量 bean 指向 A_origin
   
2. 存入三级缓存
   └── singletonFactories.put("A", () -> getEarlyBeanReference("A", mbd, A_origin))
       (此时 Lambda 捕获了 A_origin，包含了：A_origin + AOP 代理逻辑)

3. 发现依赖 B，挂起 A 的方法栈，去创建 B
   
4. B 实例化，发现依赖 A，去三级缓存调用 A 的 Lambda
   └── 触发 lambda.getObject() -> 执行 getEarlyBeanReference()
       ├── 检查 A 是否需要 AOP
       │    ├── 需要  => 生成 A_proxy (内部 target 指向 A_origin)
       │    └── 不需要 => 直接返回 A_origin
       │
       └── 【状态转移】：
           1. 把生成的 A_proxy 存入二级缓存 (earlySingletonObjects)
           2. 彻底删除三级缓存里的 Lambda (singletonFactories.remove("A"))

5. B 成功拿到二级缓存里的 A_proxy，B 完成初始化并出栈

6. 恢复 A 的方法栈，A_origin 继续填充属性（将 B 注入给 A_origin），A 完成初始化


### 9. Spring AOP 使用 JDK 代理还是 CGLIB？

**口述答案：** Spring AOP 是运行时代理。目标实现接口时可使用 JDK 动态代理，代理接口方法；没有接口时使用 CGLIB 子类代理，也可强制类代理。CGLIB 不能增强 `final` 类、`final` 方法和 `private` 方法，因为它们无法被继承或覆盖；Spring 7 还增强了对单 Bean 指定代理类型的能力。[^23]

**追问：** AOP 与 AspectJ 区别？Spring AOP 主要拦截 Spring Bean 的方法调用，AspectJ 可做编译期或加载期织入，连接点范围更广。  
**易错点：** “Boot 2 默认 CGLIB”不等于 Spring Framework 永远只用 CGLIB。

**补充说明：** AspectJ 是 Java 编程语言的一个面向切面编程（AOP）的独立扩展与完整实现，字节码操作（通过 ajc 编译器修改 .class 文件），不依赖 spring编译期直接注入字节码，运行期性能极高。Spring AOP 是动态代理（JDK 动态代理 / CGLIB），SPringAOP 可以直接用 AspectJ 的注解，底层依然是Spring 的动态代理。Spring AOP 也可以不用 AspectJ 注解，比如有个 XML 定义切面，或者用自己的Advice / Advisor 接口



### 10. 为什么同类内部调用会让 `@Transactional`、`@Async`、`@Cacheable` 失效？

**口述答案：** 默认代理模式只拦截从代理外部进入的调用；`this.method()` 已经进入目标对象，绕过代理，所以对应 advice 不执行。首选修复是拆分 Bean、把调用边界设计清楚；其次可注入自身代理或用 `AopContext.currentProxy()`，但后两者增加耦合，应谨慎使用。[^24][^23]

**追问：** 改成 CGLIB 能解决吗？不能，自调用仍通过 `this` 而非代理。  
**易错点：** 不要只记住事务，缓存、异步、重试等基于代理的注解同样受影响。

### 11. `@Transactional` 的实现链路和默认行为是什么？

**口述答案：** Spring 通过 AOP 代理拦截方法，读取事务属性，通过 `PlatformTransactionManager` 获取或加入事务，执行业务后提交，异常时按回滚规则处理。默认传播是 `REQUIRED`，隔离级别为数据库默认、读写事务、超时沿用事务系统；默认只对 `RuntimeException` 和 `Error` 回滚，不对普通 checked exception 回滚。Spring 6.2 可通过 `@EnableTransactionManagement(rollbackOn=ALL_EXCEPTIONS)` 全局改为所有异常回滚。[^25][^24]

**追问：** 注解放接口、类还是方法？通常放具体实现类的 public 业务方法，方法级配置优先。  
**易错点：** 仅添加注解不一定激活事务；非 Spring 管理对象也不会生效。

### 12. `REQUIRED`、`REQUIRES_NEW`、`NESTED` 有何区别？

**口述答案：** `REQUIRED` 无事务就新建，有事务就加入，同一物理事务中的内部逻辑若标记 rollback-only，外层提交时可能得到 `UnexpectedRollbackException`。`REQUIRES_NEW` 挂起外部事务并启动独立物理事务，会额外占连接；`NESTED` 通常用同一 JDBC 物理事务的 savepoint 做局部回滚，依赖事务管理器和资源支持。[^26]

**追问：** 连接池为什么可能被 `REQUIRES_NEW` 打满？外层事务占一条连接，内层还要再借一条。  
**易错点：** `NESTED` 不是另开事务；`REQUIRES_NEW` 也不能通过同类自调用生效。

**补充说明：** `REQUIRES_NEW` 会导致 spring 剥离当前资源（主要是数据库连接 Connection、事务名称、隔离级别等）生成一个SuspendedResourcesHolder变量，从连接池获得新的 connection ，绑定到ThreadLocal中，处理完释放后，在从SuspendedResourcesHolder变量恢复。


### 13. 哪些情况会导致“事务没有回滚”？

**口述答案：** 高频原因包括：同类自调用、对象不是 Spring Bean、方法不可被代理、异常被 catch 后未重新抛出、抛出 checked exception 却未配 `rollbackFor`、事务管理器选错、数据库表或操作不支持事务、异步线程越过事务边界。排查时先确认调用是否经过代理，再看异常类型、传播级别、事务管理器和 SQL 日志。[^27][^28][^24]

**追问：** catch 后如何回滚？重抛符合规则的异常，配置 `rollbackFor`，或显式 `setRollbackOnly()`。  
**易错点：** 数据库事务不能撤回已发送的 HTTP 请求、邮件或已被外部系统接收的消息。

### 14. 描述一次 Spring MVC 请求的完整流程。

**口述答案：** 请求先到前端控制器 `DispatcherServlet`；它通过 `HandlerMapping` 找到 handler 和拦截器链，再由合适的 `HandlerAdapter` 完成参数解析、类型转换并调用 Controller。返回值经返回值处理器和 `HttpMessageConverter` 写响应，或经 `ViewResolver` 渲染；异常则交给 `HandlerExceptionResolver` 链处理。[^29][^30][^31]

**追问：** 为什么需要 `HandlerAdapter`？让 `DispatcherServlet` 不依赖具体 handler 调用方式。  
**易错点：** 不要把 `DispatcherServlet` 说成直接反射调用所有 Controller。

**补充说明：**  `HandlerMapping` 就是对 @controller （最常见的 handler）修饰 或者 实现了Controller 接口 或者 HttpRequestHandler（常用于 RPC）接口的Class 的描述信息进行汇总，知道 URL 请求导航到哪个类哪个方法。结果封装成HandlerExecutionChain，它包含 handler 和拦截器链（interceptorList）。 至于一个请求需要怎么调用handler和interceptorList， 就让对应的HandlerAdapter来解决，HandlerAdapter需要解析参数，替换请求变量，返回结果封装处理。


### 15. `@Controller`、`@RestController`、`@ResponseBody` 的关系？

**口述答案：** `@Controller` 表示 MVC 控制器，默认返回值可能参与视图解析；`@ResponseBody` 表示把返回值经消息转换器写入响应体；`@RestController` 是 `@Controller + @ResponseBody` 的组合注解，因此类中方法默认直接输出响应体。[^32]

**追问：** 返回 `String` 时三者有什么差别？普通 `@Controller` 可能把它当视图名，`@ResponseBody` 场景则写字符串内容。  
**易错点：** REST 并不等于只返回 JSON，具体媒体类型由协商和 converter 决定。

**补充说明：** @ResponseBody 决定了数据流向的分水岭：有@ResponseBody --> 走 HttpMessageConverter（如 Jackson）直出数据（JSON/Text），绕开视图解析器。无@ResponseBody  --> 走 ViewResolver，试图把返回值渲染成 HTML 页面。 ViewResolver 是 Spring 提供的接口规范，通常由你引入的模板引擎（如 Thymeleaf/FreeMarker）的 Spring Boot Starter 自动配置类 在项目启动时注册到容器中。也可以自己实现ViewResolver，注册成 bean。
ResponseEntity<XXDto>  ≈   @ResponseBody XXDto  +  动态 HTTP 状态码 + 自定义 HTTP 响应头  ，非常适合做下载，它提供更丰富、更底层的 HTTP 响应控制能力。HttpEntityMethodProcessor 会处理它的 httpstatus 和 header ，放到 HttpServletResponse 里，委托HttpMessageConverter处理对应的 DTO。  @ResponseBody可以组合 @ResponseStatus(HttpStatus.OK)来设置 status code。@ControllerAdvice修饰的异常统一处理类里的方法也和 controller 一个样，@RestControllerAdvice /相当于 @ControllerAdvice + @ResponseBody


### 16. Spring MVC 如何做全局异常处理？

**口述答案：** 常用 `@RestControllerAdvice`/`@ControllerAdvice` 配合 `@ExceptionHandler`，底层由 `ExceptionHandlerExceptionResolver` 解析。完整 resolver 链还包括处理 `@ResponseStatus` 的 `ResponseStatusExceptionResolver` 和映射 Spring MVC 异常到状态码的 `DefaultHandlerExceptionResolver`；若都未处理，异常可能继续交给 Servlet 容器或 Boot 的 `/error` 机制。[^33]

**追问：** 如何统一错误协议？定义稳定 error code、message、trace id、details，并对校验、业务、系统异常分层映射。  
**易错点：** 不要把堆栈、SQL、内部类名直接返回客户端。

### 17. Filter、Interceptor、AOP 怎么选？

**口述答案：** Filter 属于 Servlet 容器链，适合安全、CORS、压缩、原始请求响应包装；Interceptor 属于 Spring MVC handler 链，能看到将被调用的 Controller，适合登录上下文、接口审计、耗时；AOP 面向 Bean 方法，适合事务、缓存、业务审计等跨切面逻辑。选择依据是拦截层级，不是哪个“更高级”。[^34][^35][^36]

**追问：** 异常时 `postHandle` 和 `afterCompletion` 谁更可靠？后者用于完成清理；前者在 handler 抛异常时通常不会执行。  
**易错点：** WebFlux 不应直接套 Servlet Filter，应使用 `WebFilter`。

**补充说明：** Filter.doFilter 方法有个入参是FilterChain， Filter.doFilter 方法() {xxxx1...; filterChain.doFilter(req, resp); xxxxx2....} , 必须调用filterChain.doFilter， fitlerChain 维护了Filter list 和当前执行到 filter 的 index，调用 filterChain 的 doFilter 才会触发下一个 filter。xxxx1...是进入下一个 filter 前的逻辑内容， xxxxx2....是 调用完后面 所有 filter 后执行的逻辑。 fitlerChain 在 filter 迭代完后就会调用DispatcherServlet.service.
DispatcherServlet 后面就是有 handlerMapping 里拿 Interceptor 和 handler,  让 handlerAdapter 去执行，大概顺序： prehandle---> handle(Controller里的方法)---> postHandle ---->Render View / 输出 Response Body--->afterCompletion . interceptor chain 是动态构建的， 每次请求从handlerMapping里得到Interceptor。



### 18. Spring Boot 外部化配置与优先级怎么讲？

**口述答案：** Boot 可从 properties、YAML、环境变量、系统属性、命令行等来源构建 `Environment`，后加载或更高优先级来源可覆盖低优先级值。简单值可用 `@Value`，成组配置优先用类型安全的 `@ConfigurationProperties`，支持构造器绑定、校验和默认值；`spring.config.location` 替换默认位置，`spring.config.additional-location` 是追加位置。[^37]
彻底废弃 jar 包内外的默认 application.yml，只加载 /opt/app/prod.yml java -jar app.jar --spring.config.location=file:/opt/app/prod.yml
保留 jar 包内默认配置，但用外部 /opt/app/override.yml 中的同名配置覆盖它 java -jar app.jar --spring.config.additional-location=file:/opt/app/override.yml 追加位置的配置文件优先级最高



**追问：** 为什么推荐 `@ConfigurationProperties`？结构化、可校验、元数据友好、比大量 SpEL 字符串更易维护。  
**易错点：** 不要在面试中死背一长串优先级而讲不清“后者覆盖前者”的实际排障方法。

### 19. Profile 与 Conditional 有何区别？

**口述答案：** `@Profile` 是环境维度的条件化注册，本质上是特定条件；Boot 的 Conditional 更细粒度，可根据类、Bean、属性、资源和 Web 应用类型决定配置是否生效。Profile 适合 dev/test/prod 这类环境切换，但业务功能开关通常更适合 `@ConditionalOnProperty` 或专用配置中心。[^22][^13]
dev环境的配置 Class：
@Configuration
@Profile("dev")

@Bean
@ConditionalOnMissingBean(DataSource.class) // 条件 1: 缺少 DataSource
@ConditionalOnProperty(name = "db.enabled", havingValue = "true") // 条件 2: 配置项开关打开
 public DataSource defaultDataSource() {}


**追问：** Native/AOT 下有什么变化？影响 Bean 是否存在的 profile 和条件可能在构建期决定。  
**易错点：** 不要让多个 Profile 组合成难以推理的“配置矩阵”。

**补充说明：**   Just In Time: JVM 在运行时通过动态反射、CGLIB 动态生成字节码，并把热点代码编译成机器码。启动慢、内存占用高。
[.java 源文件] ──(javac 编译)──> [.class 字节码] ──(JVM 逐行解释)──> [CPU 机器码] (性能低) ==》
                             ┌──> [解释器逐行解释执行] (冷代码/刚启动)
[.class 字节码] ──(JVM 运行)─┤
                             └──> [JIT 编译器编译] ──> [CPU 字节码直接变成 CPU 机器码] (热点代码/性能极高)

AOT（Ahead-Of-Time 预先编译）：在编译期分析整个应用的代码拓扑，直接把 Java 字节码编译为操作系统原生的二进制可执行文件（Native Image）（借助 GraalVM）。毫秒级启动、内存占用低。
它彻底把所有 .class 字节码提前全部编译成 CPU 原生机器码。
它利用静态分析（Reachability Analysis），裁掉所有用不到的类和代码（Tree Shaking）。
它把一个极简的、微型化的 JVM 运行时环境——Substrate VM，直接静态链接（Static Link）到了可执行文件里。


### 20. Starter 与依赖管理分别解决什么问题？

**口述答案：** Starter 是一组有明确场景的依赖描述，例如 Web、Actuator、Data；依赖管理/BOM 统一控制版本，避免每个库手写版本和依赖冲突。Starter 不等于自动配置，但通常把业务库、自动配置模块及必要基础设施一起引入；Boot 4 进一步模块化，迁移时可暂用 `spring-boot-starter-classic` 作为过渡。[^9][^38]

**追问：** 为什么不建议随意覆盖 Boot 管理的版本？可能破坏其已验证的依赖组合。  
**易错点：** “加 starter 就一定生成 Bean”错误，仍需满足自动配置条件。

### 21. `SpringApplication.run()` 大致做了什么？

**口述答案：** 它准备 Environment、推断应用类型、创建合适的 `ApplicationContext`，应用初始化器和监听器，加载主配置与自动配置，刷新容器，启动 Web Server，最后执行 Runner 并发布生命周期事件。回答源码时应围绕 `prepareEnvironment → createApplicationContext → prepareContext → refreshContext → afterRefresh` 展开，而不是背所有私有方法。[^39][^11]

**追问：** Web Server 何时启动？通常在 Context refresh 过程中由 WebServerApplicationContext 创建。  
**易错点：** `main` 方法并不是直接启动 Tomcat 后再创建 Spring 容器。

**补充说明：**  
推断类型：看 Classpath 决定是普通应用还是 Web 应用。是 Web Servlet 或者 Web Reactive 或者 Non-Web/CommandLine
准备环境（Environment）：把 YAML、环境变量、命令行参数加载进来。构建Environment
创建容器（Context）：根据类型 new 出对应的空 ApplicationContext。web 应用就是AnnotationConfigServletWebServerApplicationContext
刷新容器（Refresh）：（最核心） 扫描 @Component、创建所有 Bean、做依赖注入，并把内嵌 Tomcat 跑起来。
开业回调（Runner）：发布启动成功事件，执行 CommandLineRunner。afterRefresh & 执行 Runner

### 22. Actuator 有什么作用？生产上如何安全使用？

**口述答案：** Actuator 提供 health、metrics、loggers、prometheus、配置与线程等管理/诊断端点，默认 HTTP 前缀通常是 `/actuator`。生产应最小化暴露端点、通过 Spring Security 鉴权、必要时使用独立管理端口/网络，并对敏感值脱敏；健康检查不能等价为业务一定健康。[^40][^41][^42]

**追问：** Kubernetes 用哪些探针？`/actuator/health/liveness` 和 `/actuator/health/readiness`。  
**易错点：** 不要暴露 `env`、`configprops`、`heapdump` 给公网。

### 23. Spring MVC 与 WebFlux 如何选择？

**口述答案：** MVC 基于 Servlet 阻塞模型，通常用较大的线程池承受阻塞调用；WebFlux 基于 Reactive Streams 和非阻塞 I/O，通常以少量 event-loop 线程处理高并发。若主要依赖 JPA/JDBC 和阻塞 SDK，MVC 往往更合适；只有调用链端到端非阻塞、并发连接多且延迟等待占比高时，WebFlux 才更可能发挥优势。[^43]

**追问：** WebFlux 一定更快吗？不一定，它优化的是并发资源利用模型，不会让 CPU 计算或阻塞数据库自动变快。  
**易错点：** 在 event loop 上调用阻塞 API 会拖垮整个吞吐。

### 24. Spring 6 / Boot 3 的核心迁移点是什么？

**口述答案：** 面试中应首先回答 Java 17 基线与 Java EE API 从 `javax.*` 迁移到 `jakarta.*`，然后补充 Spring Security 配置从继承适配器转向声明 `SecurityFilterChain` Bean、AOT/native 支持进入主线。旧项目升级应先清点 JDK、Servlet 容器、JPA/Validation、第三方 starter 与序列化兼容性，而不是只做包名替换。[^35][^22]

**追问：** 为什么这是破坏性迁移？包名、规范版本、容器和生态依赖同时改变。  
**易错点：** 不要把 Boot 2 直接跳 Boot 4 作为默认方案。

### 25. Spring 7 / Boot 4 的重要变化是什么？

**口述答案：** Spring 7 仍以 Java 17 为最低基线，但升级到 Jakarta EE 11、Servlet 6.1，并要求现代 Tomcat 11/Jetty 12.1；移除 `javax.annotation` 和 `javax.inject` 支持。Boot 4 基于 Spring 7，重点包括更细的模块化、Jackson 3、声明式 HTTP 客户端/API 版本能力、`BeanRegistrar` 与更新的可观测性/安全生态；当前应优先从最新 Boot 3.5 维护版过渡再升级。[^44][^38][^45][^46]

**追问：** 迁移第一步？升级到最新 3.5.x、跑完整测试、清理弃用 API，再处理 Boot 4 模块与配置变化。  
**易错点：** Boot 4 最低 Java 版本仍是 17，不要误答成 21 或 25。

## A级：高级原理题

### 26. `BeanFactoryPostProcessor` 与 `BeanPostProcessor` 有何区别？

**口述答案：** 前者处理 Bean 的定义元数据，发生在普通 Bean 实例化之前；后者处理已经创建的 Bean 实例，能在初始化前后包装对象，依赖注入和 AOP 都大量利用它。后处理器必须尽早创建，因此用 `@Bean` 声明时通常应为 `static` 且少依赖，避免配置类及其依赖过早实例化而错过完整后处理。[^47][^14]

**追问：** `ConfigurationClassPostProcessor` 属于哪类？`BeanDefinitionRegistryPostProcessor`，也是 BeanFactory 后处理体系。  
**易错点：** 不要把两者都说成“修改 Bean”。

**补充说明：**  ConfigurationClassPostProcessor 实现了（Implement）BeanDefinitionRegistryPostProcessor 接口， 负责把所有的 Bean 注册成 BeanDefinition。把我们写的 @Configuration、@ComponentScan、@Import、@Bean 等注解“翻译”并注册成容器里的 BeanDefinition。

### 27. `FactoryBean` 与普通 Bean 有何区别？

**口述答案：** `FactoryBean<T>` 自身是一个受容器管理的工厂，正常按名称获取时拿到的是它生产的对象；使用 `&beanName` 才拿到工厂本身。它适合复杂对象创建和框架集成，例如代理、会话工厂；不要与 `BeanFactory` 混淆，后者是整个 IoC 容器接口。[^14]

**追问：** `isSingleton()` 表示什么？表示 `FactoryBean` 产物是否按单例语义缓存，不是工厂 Bean 自身的 scope。  
**易错点：** 普通 `@Bean` 工厂方法不等于实现了 `FactoryBean`。

**补充说明：** 
@Component("myService")
public class MyFactoryBean implements FactoryBean<MyService> {
    @Override
    public MyService getObject() {
        return new MyServiceImpl(); // 返回真正的业务对象
    }

    @Override
    Class<?> getObjectType() {
        return MyService.class;
    }
}
context.getBean("myService") 或 @Autowired MyService：获得的是 getObject() 返回的 MyServiceImpl 真实对象。
context.getBean("&myService")（在 Bean Name 前加 &）：获得的是 MyFactoryBean 工厂对象本身。
为什么需要它？ 绕过 Spring 默认的反射实例化限制，用 Java 代码自由定制对象的创建全过程。
什么时候用？ 集成第三方框架、生成动态代理对象（MyBatis(UserMapper)/Dubbo(@DubboReference)/Feign）、配置复杂连接/会话对象（SqlSessionFactory）。



### 28. `@Configuration(proxyBeanMethods=true/false)` 有何区别？

**口述答案：** `true` 的 full mode 通常通过 CGLIB 增强配置类，配置类内部直接调用另一个 `@Bean` 方法也能返回容器中的单例；`false` 的 lite mode 不拦截方法调用，内部直接调用会像普通 Java 方法一样创建新对象。没有跨 `@Bean` 方法调用时，`false` 更简单并利于启动与 AOT；依赖最好通过方法参数注入，而不是方法互调。

**追问：** `@Component` 内的 `@Bean` 属于哪种？lite mode。  
**易错点：** `proxyBeanMethods=false` 不会让这些 `@Bean` 停止注册。

### 29. Singleton Bean 是线程安全的吗？

**口述答案：** 不是。singleton 只表示每个容器通常一个实例，不提供并发安全保证。无状态 Service 通常天然安全；若在字段中保存请求数据、可变集合或非线程安全格式化器，就必须消除共享状态、使用线程安全结构或缩小 scope。

**追问：** `ThreadLocal` 能解决吗？只能在严格清理并正确传播的线程模型下使用，异步、线程池和 WebFlux 中尤其容易泄漏或丢上下文。  
**易错点：** 不要把 Spring 单例与 GoF 单例实现方式混为一谈。

### 30. singleton 中注入 prototype 为什么不会每次得到新对象？

**口述答案：** singleton 只创建一次，普通依赖注入也只发生一次，因此注入进去的是当时创建的那个 prototype 实例。若每次业务调用都需要新实例，应注入 `ObjectProvider<PrototypeBean>`、`Provider`，使用 lookup method，或配置 scoped proxy。[^48]

**追问：** 哪种方式最容易测试？通常 `ObjectProvider` 或显式工厂接口。  
**易错点：** prototype 表示每次向容器请求创建，不表示容器会自动追踪并销毁所有实例。

### 31. `@Lazy` 的作用和代价是什么？

**口述答案：** 它可以延迟 Bean 创建，注入点上常通过代理推迟解析，也能临时打断某些循环依赖。代价是把启动期错误延迟到首次请求、增加首调用延迟，并可能掩盖架构耦合；因此不应把它当作通用启动优化策略。

**追问：** Boot 全局 lazy initialization 适合生产吗？需评估首请求抖动和故障发现时机，通常配合预热与关键 Bean 排除。  
**易错点：** 延迟初始化不等于减少总初始化成本。

### 32. Spring 事件机制适合什么场景？

**口述答案：** `ApplicationEventPublisher` 与 `@EventListener` 适合同 JVM 内的解耦通知，默认同步执行，监听器异常和耗时会影响发布线程。事务相关副作用可用 `@TransactionalEventListener` 绑定提交前后阶段；跨服务可靠事件不能只靠内存事件，应使用 outbox、消息队列与幂等消费。

**追问：** AFTER_COMMIT 监听器里再写数据库有什么问题？原事务已提交，若需要新事务应明确使用新的事务边界。  
**易错点：** Spring Event 不是可靠消息系统。

### 33. `@Async` 的原理与常见坑是什么？

**口述答案：** 启用 `@EnableAsync` 后，代理把方法提交到 `TaskExecutor`，调用方立即返回；因此它受自调用限制，事务和安全上下文也不会天然跨线程。必须配置有界线程池、队列、拒绝策略和监控；1，返回值优先使用 `CompletableFuture`，get会抛出异常；`void` 方法（没有返回值可以拿异常）异常需 `AsyncUncaughtExceptionHandler` 处理。[^49]

**追问：** `@Async` 方法上事务如何理解？事务在线程内按代理边界新建，不会继承调用线程事务。  
**易错点：** 使用默认执行器却不知道并发上限和拒绝行为。

**补充说明：** 
@Configuration
@EnableAsync // 开启异步支持
public class AsyncConfig implements AsyncConfigurer {

    // 1. 配置异步线程池
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("Async-Worker-");
        executor.initialize();
        return executor;
    }

    // 2. 注册自定义的异常处理器
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() { 
        return new CustomAsyncExceptionHandler(); //CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler
    }
}


### 34. Spring Cache 的核心注解与击穿风险？

**口述答案：** `@Cacheable` 命中时跳过方法，`@CachePut` 总执行并更新缓存，`@CacheEvict` 删除，`@Caching` 组合多个操作。缓存抽象不自动解决分布式一致性、穿透、击穿和雪崩；需要合理 key、TTL 随机化、空值策略、single-flight/锁，以及数据库更新与缓存失效顺序设计。Spring 6.1+ 还可适配 `CompletableFuture` 与 reactive 返回值，但底层缓存必须支持相应异步能力。[^50][^51]

**追问：** 为什么同类调用失效？仍是代理边界。  
**易错点：** 不要在同一方法上无条件混用 `@Cacheable` 和 `@CachePut`。

**补充说明：** 
@Service
public class UserService {

    // 1. 基本用法：更新数据后，删除 Key 为 userId 的缓存
    @CacheEvict(value = "users", key = "#userId")  // Redis key 例如： users:1001
    public void updateUser(Long userId, UserDto userDto) {
        userRepository.update(userId, userDto);
    }

    // 2. 清空整个缓存空间 (allEntries = true)
    // 适用于批量操作或影响面广的变更（如更新全量配置），将 "users" 缓存库里的所有 Key 彻底清空
    @CacheEvict(value = "users", allEntries = true)  // 前缀 users:* 批量删除
    public void reloadAllUsers() {
        // 重置用户全局配置或批量更新
    }

    // 3. 在方法执行前触发清除 (beforeInvocation = true)
    // 默认是 false（方法成功执行完后才清缓存）。
    // 若设为 true，即使后续方法报错抛异常，缓存也必定会被清除。
    @CacheEvict(value = "users", key = "#userId", beforeInvocation = true)
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}

// Caching 组合
@Caching(
    put = {
        @CachePut(value = "users", key = "#user.id"),
        @CachePut(value = "users_by_phone", key = "#user.phone")
    },
    evict = {
        @CacheEvict(value = "users_list", allEntries = true) // 同步清空全量列表缓存
    }
)
public User updateUserPhone(User user) {
    return userRepository.save(user);
}



### 35. 事务隔离级别与传播行为有什么不同？

**口述答案：** 隔离级别解决同一数据库中并发事务互相可见的问题，如脏读、不可重复读、幻读；传播行为解决方法调用链遇到已有事务时是加入、新建、挂起还是不用。最终效果受数据库实现、MVCC、锁与事务管理器影响，不能只凭注解名称推断。

**追问：** `ISOLATION_DEFAULT` 是什么？沿用底层数据库/连接默认隔离级别。  
**易错点：** 把 `REQUIRES_NEW` 误当成更高隔离级别。

### 36. `readOnly=true` 是否保证禁止写入？

**口述答案：** 它主要是事务语义声明和优化提示，Spring/ORM/驱动可能据此调整 flush 或连接只读状态，但不同数据库和事务管理器的强制程度不同。它不能替代数据库只读账号、权限与副本路由规则；面试中应说“可能优化，不能视为绝对安全边界”。

**追问：** 读写分离如何基于它实现？可在事务开启前通过路由数据源读取事务属性并选择主从，但要处理延迟、一致性和嵌套事务。  
**易错点：** 方法执行到一半才切换数据源通常已经太晚，连接可能已绑定。
**补充说明：**  @Transactional(readOnly=true) mysql InnoDB 允许在 readonly 里写数据，不能认为这个就是真的只读。 Hibernate ，事务管理器可以通过readOnly=true做一写少量优化。


### 37. 什么时候使用 `TransactionTemplate`？

**口述答案：** 当事务边界需要按代码分段、异常处理复杂、一个流程中要先提交再执行外部调用，或代理式声明事务难以表达时，可用 `TransactionTemplate`。Spring 官方对 imperative 流程推荐 `TransactionTemplate`，reactive 流程使用 `TransactionalOperator`；底层也可直接使用 `PlatformTransactionManager`。[^28]

**追问：** 编程式事务是否“更底层所以更好”？不是，默认优先声明式，只有边界确实动态时再使用。  
**易错点：** 不要把大段远程调用包在长数据库事务中。

### 38. 多数据源事务怎么处理？

**口述答案：** 每个本地资源通常对应自己的事务管理器，`@Transactional(transactionManager="...")` 需要明确选择。跨库强一致可使用 JTA/XA，但复杂且成本高；微服务中更常用本地事务加 outbox、Saga、补偿和幂等实现最终一致性。Spring 的声明式事务上下文本身不会跨远程调用传播。[^27]

**追问：** 链式事务管理器可靠吗？它无法提供真正原子提交，部分提交时仍需补偿。  
**易错点：** 同时开启两个本地事务不等于分布式事务。
**补充说明：** JTA （Java Transaction API）一套 java 分布式事务的官方接口 （Spring @Transactional 是本地事务 ），需要具体的实现配合，它的核心角色： TransactionManager  和 XAResource（代表一个参与分布式事务的资源，比如 mysql， Oracle 实现了 XA 协议），一般微服务不用它，太笨重，性能差，改用 TCC， Saga。



### 39. MVC 参数解析和响应写出如何扩展？

**口述答案：** 请求参数由 `HandlerMethodArgumentResolver`、数据绑定、Converter/Formatter 和校验体系协作解析；响应由 `HandlerMethodReturnValueHandler` 与 `HttpMessageConverter` 写出。自定义用户上下文、分页对象可实现 argument resolver；协议级序列化优先扩展 converter/Jackson 配置，而不是在 Controller 手工拼 JSON。

**追问：** Converter 与 Formatter 区别？Converter 通用类型转换，Formatter 更偏文本与 locale。  
**易错点：** 自定义 MVC 配置时不要无意替换掉 Boot 默认 converter 集合。

### 40. 内容协商是什么？

**口述答案：** 内容协商根据请求的 `Accept`、可生产媒体类型和可用 `HttpMessageConverter` 选择响应格式；请求体解析则关注 `Content-Type`。无法找到可接受输出通常是 406，无法支持输入媒体类型通常是 415。生产 API 应显式控制 `consumes`/`produces`，避免依赖模糊默认值。

**追问：** 为什么浏览器请求偶尔返回 XML？classpath 中存在 XML converter 且协商条件允许。  
**易错点：** `Accept` 与 `Content-Type` 的方向不要答反。

**补充说明：** Accept 是 HTTP 请求头（Request Header） 中的一个字段。它是客户端（如浏览器、App、Postman）向服务端发起的“数据格式需求声明”。Accept: application/json ---> 客户端说：“请给我返回 JSON 格式.”内容协商（Content Negotiation）就是 服务端根据客户端提交的 Accept 请求头，动态决定返回哪种数据格式的过程
Content-Type（内容类型/媒体类型 MediaType）用于标明当前传输的实体正文（Body）到底是什么格式的数据, 接收端用它来决定怎么去读取 body。



### 41. `RestTemplate`、`RestClient`、`WebClient` 怎么选？

**口述答案：** `RestClient` 是现代同步 fluent 客户端；`WebClient` 是非阻塞 reactive 客户端；`RestTemplate` 是旧的同步模板 API，在 Spring 7 文档中已明确建议转向 `RestClient`。MVC 应用可以使用 `RestClient`，端到端 reactive 场景用 `WebClient`；不要为了“新”在阻塞链路中强行 WebFlux 化。[^52]

**追问：** 如何获得 tracing 自动传播？优先用 Boot 自动配置的 `RestClient.Builder`、`WebClient.Builder` 或 `RestTemplateBuilder` 构建客户端。[^53]
**易错点：** WebClient 调用 `.block()` 后并不会保持端到端非阻塞。
**补充说明：**  RestClient 是专门为“同步阻塞（Imperative）”架构设计的现代化客户端（用来替代 RestTemplate）；而 WebClient 是专门为“异步非阻塞（Reactive）”架构设计的反应式客户端。 fluent 客户端就是流式客户端，特点是方法链（Method Chaining）。
在虚拟线程时代（Java 21 虚拟线程（Virtual Threads / Project Loom），传统的“同步阻塞”调用性价比极高，直接搭配 RestClient 是最干净、最推荐的方案。
// 阻塞等待，方法直接返回实体 User 对象
User user = restClient.get()
    .uri("/users/{id}", 1001)
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .body(User.class); // 当前线程会被阻塞，直到收到 HTTP 响应

// 非阻塞，立刻返回一个 Mono<User> 句杯（Publisher）
Mono<User> userMono = webclient.get()
    .uri("/users/{id}", 1001)
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .bodyToMono(User.class); // 线程不会等待，后续通过 Reactive 链式订阅处理


### 42. HTTP Interface Client 是什么？

**口述答案：** 它允许用带 HTTP 映射注解的 Java 接口描述远程服务，再生成代理执行请求，可选择同步或 reactive 风格。优点是减少客户端样板代码并统一契约；仍需设计超时、重试、熔断、错误映射和可观测性，不能把它误认为本地方法调用。[^54]

**追问：** 与 OpenFeign 的关系？目标相似，但 HTTP Interface 是 Spring Framework 原生能力，底层可由 Spring HTTP 客户端体系承载。  
**易错点：** 接口代理不自动提供服务发现、熔断和业务幂等。

**补充说明：**
public interface UserClient { 
    @GetExchange("/users/{id}")  //------**** Spring 会利用JDK 动态代理，在你调用这个方法时，自动把参数拼成 URL 并发起 HTTP 请求 ****-------
    User getUser(@PathVariable("id") Long id); // 看上去像本地方法
}

@Configuration
public class ClientConfig {

    @Bean
    public UserClient userClient(RestClient restClient) {
        // 1. 用 RestClient 适配成适配器
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        // 2. 创建代理工厂
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        // 3. 生成代理实现类
        return factory.createClient(UserClient.class);
    }
}

@Service
public class MyService {
    @Autowired
    private UserClient userClient; // 直接注入接口

    public void doSomething() {
        User user = userClient.getUser(1001); // 像调用本地方法一样，底层由 RestClient 发起请求
    }
}



### 43. Spring 7 的 API Versioning 有什么新能力？

**口述答案：** Spring 7 在 MVC/WebFlux 引入统一 API 版本策略，可从请求头、查询参数、媒体类型参数或 URL path 解析版本，并在 `@RequestMapping` 上声明固定版本或基线版本；客户端的 `RestClient`、`WebClient` 和 HTTP Service Client 也支持插入版本。未支持或缺少必需版本可映射为 400，且可配置弃用提示。[^55][^56][^57]

**追问：** URL 版本还是 Header 版本更好？取决于网关、缓存、客户端和演进策略，关键是统一并可观测。  
**易错点：** 这是 Spring 7 新能力，不要说成所有旧版本都有。

### 44. Spring Security 请求链路怎么描述？

**口述答案：** Servlet 请求先进入容器 FilterChain，其中 `DelegatingFilterProxy` 委托给 Spring Security 的 `FilterChainProxy`；后者按 `RequestMatcher` 选择第一个匹配的 `SecurityFilterChain`，再按顺序执行 CSRF、防护、认证和授权等过滤器。现代配置发布一个或多个 `SecurityFilterChain` Bean，而不是继承旧的 `WebSecurityConfigurerAdapter`。[^58][^35]

**追问：** 多条链的坑？如果没有兜底链，未匹配请求可能不受保护；必须结合 `@Order` 与 matcher 检查。  
**易错点：** 认证是“你是谁”，授权是“你能做什么”。
**补充说明：** Servlet 是 java web 容器的规范，Spring security 把自己的实现 filter 添加到容器 filterchain 中。 传统方式是容器先启动，同一个 JVM 里去 load web 应用 jar 包，就加载了 Spring MVC。 Spring boot 不一样，内嵌了 web容器， Spring boot main 方法先启动， 后构建容器对象，添加容器的 context，load 容器的 class，初始化。（在第 21 问题解释里 ）refresh context 的过程中拉起内嵌 web 容器。 security 放在 filter chain 的原因是安全检查需要放在 interceptor chain 前面， 因为到了interceptor 已经到了内部了，都到了具体路径上的 handler， 如果是静态文件，放在 interceptor 都拦截不到了。



### 45. `@SpringBootTest` 与测试切片如何选择？

**口述答案：** `@SpringBootTest` 创建完整 Boot `ApplicationContext`，默认 `MOCK` Web 环境不启动真实服务器；需要端到端 HTTP 时用 `RANDOM_PORT`。`@WebMvcTest`、`@WebFluxTest` 等切片只加载目标层和相关自动配置，反馈更快、定位更清晰；纯业务逻辑仍应优先普通 JUnit 单元测试。[^59][^60]

**追问：** Controller 测试缺少 Service 怎么办？注入测试替身，Boot 4 应关注新的 Mockito 测试 Bean 注解与测试模块拆分。  
**易错点：** 所有测试都上 `@SpringBootTest` 会导致慢、脆弱且上下文缓存命中率差。

## B级：新版本与生产题

### 46. Testcontainers 和 Boot Service Connection 的价值是什么？

**口述答案：** Testcontainers 在测试期间启动真实的 MySQL、MongoDB、Kafka 等容器，减少 H2 与生产数据库行为不一致的问题。Boot 的 service connection 能从容器连接信息自动创建连接详情，减少动态属性样板代码；使用时需引入 `spring-boot-testcontainers`。[^61]

**追问：** 为什么仍需要少量单元测试？容器集成测试更慢，不能替代快速业务逻辑反馈。  
**易错点：** 不固定镜像版本会让 CI 结果漂移。

### 47. Spring AOT 与 Native Image 是什么关系？

**口述答案：** AOT 在构建期分析 `ApplicationContext`，生成 Java 代码、代理字节码和反射/资源/序列化 Runtime Hints；Native Image 依赖这一步把动态行为显式化。收益通常是更快启动和更低内存，代价是构建更慢、动态 classpath/Bean 变更受限，并需要为反射和资源补 hints。[^22]

**追问：** AOT 只能用于 native 吗？也可通过 `spring.aot.enabled=true` 在 JVM 上使用。  
**易错点：** Native 不保证所有运行负载都更快，峰值吞吐和诊断体验要实测。

**补充说明：**  编译时，Spring AOT 插件会把你的 Bean 定义、属性注入全部翻译成普通的、静态的 Java 代码， 这样就不用在 spring 启动的时候动态的去做了， 把它放到了编译期，但是就不能支持动态。加上Native Image 就不需要 JVM 了， 
第一步（Spring AOT 阶段）：你的源代码 + Spring AOT 插件 ----> 生成了一堆静态的 .java 源代码（比如把动态解析变成了直接 new 对象）。
第二步（常规编译阶段）：这些生成的 Java 源码，和你的业务代码一起，被标准的 Java 编译器（javac）编译成 标准的 Java 字节码（.class 文件）。这就是为什么加上 spring.aot.enabled=true 后，它依然能在普通的 JVM 上跑——因为 JVM 运行的依然是标准字节码，只是这些字节码变得非常“纯粹和静态”，不需要再在运行时做复杂的反射扫描了。
第三步（GraalVM Native Image 阶段）：只有到了这一步，GraalVM 的 native-image 编译器才会把上面生成的字节码（.class）一口气翻译成可以直接在操作系统上跑的二进制机器码（Machine Code）。

java -Dspring.aot.enabled=true -jar target/your-application.jar 加上 -Dspring.aot.enabled=true 后，Spring 容器会直接加载编译期预先生成好的静态初始化代码，跳过繁琐的运行时反射扫描，从而大幅缩短冷启动时间。它能做到几毫秒到几十毫秒内完成启动并立即响应请求，这比如用 java 写命令行软件，云上快速拉起应用，这让 Java 重新拥有了跟 Go/Python 叫板的资格，可以完美无缝地跑在 AWS Lambda、阿里云函数计算（FC）等 Serverless 平台上。



### 48. Spring 7 `BeanRegistrar` 解决什么问题？

**口述答案：** `BeanRegistrar` 提供一等的编程式 Bean 注册，可通过 `BeanRegistry` 与 `Environment` 在循环、条件和自定义 DSL 中灵活注册多个 Bean，并用 `@Import` 引入。它比在单个 `@Bean` 方法中偷偷注册其他 Bean 更规范，而且支持 AOT/native 场景。[^62][^63][^64]

**追问：** 何时用？框架、starter、多租户/多客户端批量注册；普通业务 Bean 仍优先组件扫描或 `@Bean`。  
**易错点：** 不要为了炫新 API 把简单配置改成命令式注册。

### 49. Boot 可观测性体系怎么回答？

**口述答案：** 可观测性通常包括日志、指标和追踪。Boot 以 Micrometer Observation 统一产生 metrics 与 traces，通过 `ObservationRegistry` 自定义观测；低基数标签可进入指标和追踪，高基数标签应只进入 trace，避免 Prometheus 时序爆炸。[^65]

**追问：** 指标如何导出 Prometheus？加入对应 registry 并暴露 `/actuator/prometheus`。[^66]
**易错点：** 把 userId、orderId 作为 metrics tag 会造成高基数灾难。

**补充说明：**  @Observed 添加后， Metrics 视角：它会自动帮你在后台累加这个方法的调用次数、成功率、耗时直方图（Histogram）。 Traces 视角：它会自动把当前线程的 Trace ID 和 Span ID 注入进去，记录这个方法在整个分布式链路中的上下文。


### 50. 如何实现链路追踪与上下文传播？

**口述答案：** Boot 通过 Micrometer Tracing 桥接 OpenTelemetry 或 Brave；使用自动配置的 HTTP client builder 可自动传播 trace context，日志也可加入 correlation id。线程池、`@Async` 和 reactive pipeline 跨线程时要检查上下文传播配置，不能默认 ThreadLocal 永远有效。[^65][^53]

**追问：** 采样率为什么不总设 100%？流量大时会增加成本和后端压力，应结合错误优先、尾部采样或动态策略。  
**易错点：** trace 成功上报不代表业务日志自动包含所有业务标识。

**补充说明：**   MDC（Mapped Diagnostic Context，基于 Map 键值对的结构）映射诊断上下文 诞生于 Log4j。 当 Spring Security / 追踪过滤器拦截到请求、拿到 Trace ID 后，它会立刻把 traceId = abc123xyz 写入当前线程的 MDC 容器里。
只要你的项目引入了 Micrometer 依赖，Spring 就会自动把这个追踪 Filter 注册到 Tomcat 的最外层。不管是谁写的 Controller，请求进来时 Filter 必然执行，单号必然被种到线程里ThreadLocal/CurrentTraceContext
采样（Sampling）”决定的不是“要不要生成 ID”，而是“要不要把这条链路的数据上报并保存到大盘（如 Zipkin、Jaeger）里”。以 W3C 的 traceparent 为例：
00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01  最后两位 01代表已采样， 后续服务都得采样，和Root Span 保持一致。这样log 里一直有tranceid。

### 51. Spring Boot 的优雅停机如何工作？

**口述答案：** Boot 对 Tomcat、Jetty、Reactor Netty 的 Servlet/reactive 应用默认支持 graceful shutdown：关闭上下文时先停止接收新请求，并在 `spring.lifecycle.timeout-per-shutdown-phase` 的宽限期内等待在途请求完成。JVM shutdown hook 会关闭上下文并触发生命周期回调。[^67][^39]

**追问：** Kubernetes 还需要什么？合理的 readiness 切换、preStop、terminationGracePeriod 与应用超时配合。  
**易错点：** IDE 的强制停止可能不会发送正确 SIGTERM，因此看不到优雅停机。

### 52. Liveness 与 Readiness 有什么区别？

**口述答案：** Liveness 表示进程是否需要重启，不能轻易依赖外部数据库，否则下游故障可能触发全量重启风暴；Readiness 表示当前实例是否应接收流量，可以结合必要依赖。Boot Actuator 基于 `ApplicationAvailability` 提供相应健康组和端点。[^42]

**追问：** 数据库挂了放哪一个？通常影响 readiness，而不是直接判死 liveness；仍需结合业务降级能力。  
**易错点：** 两个探针使用同一套依赖检查会失去区分意义。

### 53. 如何编写自定义自动配置与 starter？

**口述答案：** 自动配置类使用 `@AutoConfiguration`，通过条件注解控制生效，并在 `AutoConfiguration.imports` 中登记；常用 `@ConditionalOnClass` 判断能力存在、`@ConditionalOnMissingBean` 允许用户覆盖、`@ConditionalOnProperty` 提供开关。starter 负责聚合依赖，autoconfigure 模块负责代码，二者可分离以减少不必要依赖。[^13]

**追问：** 为什么自动配置类不应被普通 component scan 扫到？否则可能绕过导入顺序和条件模型而意外生效。  
**易错点：** 继续只配置旧版 `spring.factories` 会在现代实现中漏加载。

### 54. 自动配置没生效怎么排查？

**口述答案：** 使用 `--debug` 查看 Condition Evaluation Report，确认候选类是否被导入、哪个条件 negative match、是否被 exclude、用户 Bean 是否让配置 back off；再检查配置前缀、classpath 依赖、应用类型与包扫描。Actuator 的 `conditions`、`beans`、`configprops` 也可辅助，但生产必须限制访问。[^10][^68][^42]

**追问：** 只看到“Bean 不存在”怎么办？继续追踪其所在自动配置的全部条件和配置属性，而不是直接复制一个 Bean。  
**易错点：** 随意加 `@ComponentScan` 扫第三方包可能引入更多隐性配置。

### 55. Virtual Threads 在 Boot 中如何使用？

**口述答案：** Java 21+ 可设置 `spring.threads.virtual.enabled=true` 让 Boot 的相关任务执行使用虚拟线程；它特别适合大量阻塞 I/O，但不会让数据库连接池、外部服务或 CPU 无限扩容。虚拟线程是 daemon thread，只有虚拟线程时 JVM 可能退出，因此调度型应用可设置 `spring.main.keep-alive=true`。[^39]

**追问：** 还需要 WebFlux 吗？两者解决问题方式不同；阻塞式代码可先评估 MVC+虚拟线程，端到端 reactive 仍有背压和流式处理优势。  
**易错点：** 开启虚拟线程后，传统线程池大小配置可能不再按预期生效。

### 56. 如何做 Spring Boot 性能与启动优化？

**口述答案：** 先测量再优化：使用 startup 指标、JFR、Actuator/Micrometer 与 SQL/APM 定位瓶颈。常见方向包括减少无用 starter 和扫描范围、修复 Bean 提前初始化、控制连接池、避免启动期远程调用、评估 lazy initialization、AOT/native，并把吞吐、P99、内存与启动时间分开衡量；Boot 会暴露 `application.started.time` 和 `application.ready.time`。[^66]

**追问：** 启动慢先看什么？Bean 创建耗时、classpath、实体/仓库扫描、迁移脚本、DNS/远程连接和日志。  
**易错点：** “排除所有自动配置”通常不是可维护的优化。

### 57. 如何排查接口突然变慢？

**口述答案：** 先区分 CPU、锁、GC、线程池/连接池排队、数据库慢 SQL、下游超时与网络问题。结合 RED 指标、trace、线程 dump、连接池指标和 SQL 执行计划定位；Spring 层重点看 MVC 工作线程是否耗尽、事务是否过长、`@Async` 队列是否堆积、WebFlux event loop 是否被阻塞、HTTP client 是否配置连接/读取超时。

**追问：** 为什么不能先扩线程池？瓶颈若是数据库连接或下游容量，扩线程只会增加排队和雪崩。  
**易错点：** 平均耗时正常不能证明系统正常，应看 P95/P99 和错误率。

### 58. `@Scheduled` 的常见生产问题是什么？

**口述答案：** 调度方法默认可能共享有限调度线程，长任务会阻塞其他任务；多实例部署会导致每个实例都执行，必须使用分布式锁、leader election 或外部调度平台保证语义。任务还应做到幂等、可重试、可观测，并区分 fixedRate、fixedDelay 与 cron 的时间语义；启用需 `@EnableScheduling`。[^49]

**追问：** fixedRate 与 fixedDelay？前者按计划起点间隔，后者按上次完成后延迟；并发行为还取决于 scheduler 配置。  
**易错点：** 单机测试只执行一次，不代表集群也只执行一次。

### 59. Boot 4 升级项目应该怎样落地？

**口述答案：** 先升级到最新 Boot 3.5.x，清除弃用 API并建立测试基线；再确认 Java 17+、Servlet 6.1、Tomcat 11/Jetty 12.1、Jakarta EE 11、Spring 7 与第三方 starter 兼容性。之后处理 Boot 4 模块拆分、Jackson 3、测试模块、配置项变更；可暂用 classic starter 和 `spring-boot-properties-migrator` 过渡，验证完成后移除。[^38][^46]

**追问：** WAR 部署注意什么？容器版本必须满足 Servlet 6.1，且 Boot 4 的外部 Tomcat 依赖方式有调整。  
**易错点：** 只要编译成功不代表序列化格式、安全链、数据库方言和运行容器兼容。

### 60. 设计一个可靠的下单接口，Spring 层面会考虑什么？

**口述答案：** 入口使用幂等键和数据库唯一约束防重复订单；本地库存/订单写入放在短事务中，外部支付不能直接依赖数据库回滚。跨系统一致性使用 outbox+消息、状态机/Saga 和补偿；HTTP client 配置连接、响应和总超时，重试只用于明确可重试且幂等的操作；通过 Actuator/Micrometer 建立指标、trace 和告警，并为实例提供正确 readiness 与优雅停机。[^42][^27][^65]

**追问：** 用户超时重试如何防止重复扣款？业务幂等键必须贯穿订单与支付请求，不能只依赖 Controller 防抖。  
**易错点：** 在一个超长 `@Transactional` 方法里调用库存、支付、物流三个远程服务，不会得到跨服务原子性。

## 面试冲刺顺序

- **第一轮：** 1—25，每题录音控制在 90—120 秒，尤其练自动配置、Bean 生命周期、AOP、自调用、事务传播和 MVC 链路。
- **第二轮：** 26—45，要求能画出 Bean 创建链、事务代理链、MVC 请求链和 Security Filter Chain。
- **第三轮：** 46—60，用真实项目案例回答升级、性能、可观测性、幂等与一致性，不要只背定义。
- **旧经验转新版本：** 重点补 `javax → jakarta`、`WebSecurityConfigurerAdapter → SecurityFilterChain`、`spring.factories → AutoConfiguration.imports`、`RestTemplate → RestClient/HTTP Interface`、Boot 2 → 3 → 4 的迁移路径。

## 自测标准

- 能先给结论，再解释底层对象和调用链。
- 能指出代理边界、线程边界、事务边界和网络边界。
- 能说出至少一个失败场景及排查步骤。
- 能区分旧版本做法、当前稳定做法与预览功能。
- 能把答案落到自己做过的订单、库存、支付、缓存、任务或接口性能案例中。

---

## References

1. [www.secondtalent.com › interview-guide › spring-bootTop 35 Spring Boot Interview Questions and Answers [2026]](https://www.secondtalent.com/interview-guide/spring-boot/) - 35 Spring Boot interview questions with answers for Spring Boot 4.1: auto-configuration, @Transactio...

2. [www.indeed.com › spring-boot-interview-questions55 Spring Boot Interview Questions (With 5 Sample Answers)](https://www.indeed.com/career-advice/interviewing/spring-boot-interview-questions) - Explore 55 Spring Boot interview questions, discover how to prepare for interviews and review exampl...

3. [Spring & Spring Boot Interview Questions 2025 Edition - LinkedIn](https://www.linkedin.com/posts/java-by-madhu-vundavalli_java-spring-springboot-activity-7412488184233811968-PskK) - 🚀 Most Asked Spring & Spring Boot Interview Questions (2025 Edition) If you’re preparing for a Java ...

4. [www.crackr.dev › java-interview-questions › springJava Spring Interview Questions: 153 Questions (2026)](https://www.crackr.dev/interview-guides/java-interview-questions/spring) - 1,715 real Java interview questions ranked by how often they are asked. Sourced from analysis of 600...

5. [Releases](https://spring.io/blog/category/releases/) - Level up your Java code and explore what Spring can do for you.

6. [Spring Boot 4.1.0 available now](https://spring.io/blog/2026/06/10/spring-boot-4) - This release includes a number of improvements, new features and dependency upgrades. It also includ...

7. [Releases · spring-projects/spring-framework](https://github.com/spring-projects/spring-framework/releases) - This maintenance release fixes a high number of CVEs. You can learn more about this in the "Spring a...

8. [Spring Framework Documentation](https://docs.spring.io/spring-framework/reference/index.html) - Spring Framework Documentation ; Core. IoC Container, Events, Resources, i18n, Validation, Data Bind...

9. [Build Systems](https://docs.spring.io/spring-boot/reference/using/build-systems.html)

10. [Auto-configuration](https://docs.spring.io/spring-boot/reference/using/auto-configuration.html) - You need to opt-in to auto-configuration by adding the @EnableAutoConfiguration or @SpringBootApplic...

11. [Structuring Your Code :: Spring Boot](https://docs.spring.io/spring-boot/reference/using/structuring-your-code.html)

12. [Using the @SpringBootApplication Annotation](https://docs.spring.io/spring-boot/reference/using/using-the-springbootapplication-annotation.html) - Many Spring Boot developers like their apps to use auto-configuration, component scan and be able to...

13. [Creating Your Own Auto-configuration](https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html) - Auto-configuration can be associated to a “starter” that provides the auto-configuration code as wel...

14. [Container Extension Points](https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html)

15. [Customizing the Nature of a Bean :: Spring Framework](https://docs.spring.io/spring-framework/reference/core/beans/factory-nature.html)

16. [Introduction to The Spring IoC Container and Beans](https://docs.spring.io/spring-framework/reference/core/beans/introduction.html)

17. [Dependency Injection :: Spring Framework](https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html) - Circular dependencies If you use predominantly constructor injection, it is possible to create an un...

18. [The BeanFactory API :: Spring Framework](https://docs.spring.io/spring-framework/reference/core/beans/beanfactory.html)

19. [Fine-tuning Annotation-based Autowiring with Qualifiers](https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/autowired-qualifiers.html)

20. [Fine-tuning Annotation-based Autowiring with @Primary or @Fallback](https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/autowired-primary.html)

21. [docs.spring.io · spring-framework · referenceComposing Java-based Configurations :: Spring Framework](https://docs.spring.io/spring-framework/reference/core/beans/java/composing-configuration-classes.html)

22. [Ahead of Time Optimizations](https://docs.spring.io/spring-framework/reference/core/aot.html) - AOT is a mandatory step to transform a Spring application to a native executable, so it is automatic...

23. [docs.spring.io · reference · coreProxying Mechanisms :: Spring Framework](https://docs.spring.io/spring-framework/reference/core/aop/proxying.html)

24. [Using @Transactional](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html) - self-invocation (in effect, a method within the target object calling another method of the target o...

25. [docs.spring.io · spring-framework · reference<tx:advice/> Settings :: Spring Framework](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/txadvice-settings.html)

26. [Transaction Propagation :: Spring Framework](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-propagation.html) - Such an independent inner transaction can also declare its own isolation level, timeout, and read-on...

27. [Declarative Transaction Management :: Spring Framework](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html)

28. [Programmatic Transaction Management](https://docs.spring.io/spring-framework/reference/data-access/transaction/programmatic.html) - You can specify transaction settings (such as the propagation mode, the isolation level, the timeout...

29. [docs.spring.io · spring-framework · referenceProcessing :: Spring Framework](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet/sequence.html)

30. [docs.spring.io · web · webmvcDispatcherServlet :: Spring Framework](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet.html)

31. [Special Bean Types](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet/special-bean-types.html) - The main purpose of a HandlerAdapter is to shield the DispatcherServlet from such details. HandlerEx...

32. [@Controller](https://docs.spring.io/spring-framework/reference/7.1/web/webflux/controller/ann.html)

33. [Exceptions :: Spring Framework](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet/exceptionhandlers.html)

34. [Interception :: Spring Framework](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet/handlermapping-interceptor.html) - HandlerMapping implementations support handler interception which is useful when you want to apply f...

35. [Architecture](https://docs.spring.io/spring-security/reference/7.2/servlet/architecture.html)

36. [www.springjavalab.com › 2025 › 12Top Spring Boot Interview Questions for 2–5 Years Experience ...](https://www.springjavalab.com/2025/12/spring-boot-interview-questions-for-2-5-years-experience.html) - Spring Boot interview questions for 2–5 years experience covering REST APIs, JPA, transactions, secu...

37. [Externalized Configuration :: Spring Boot](https://docs.spring.io/spring-boot/reference/features/external-config.html) - Spring Boot lets you externalize your configuration so that you can work with the same application c...

38. [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide/443adfd8fca24699b1b1c5ce999cea39d0edf371) - Spring Boot helps you to create Spring-powered, production-grade applications and services with abso...

39. [SpringApplication :: Spring Boot](https://docs.spring.io/spring-boot/reference/features/spring-application.html) - Commencing graceful shutdown. application as a native image. One side effect of virtual threads is t...

40. [Monitoring and Management Over HTTP :: Spring Boot](https://docs.spring.io/spring-boot/reference/actuator/monitoring.html)

41. [Production-ready Features](https://docs.spring.io/spring-boot/reference/actuator/index.html) - Observability Loggers Metrics Tracing Auditing … monitor and manage your application. Auditing, heal...

42. [Endpoints :: Spring Boot](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html) - Observability Loggers Metrics Tracing Auditing … metrics Shows “metrics” information for the current...

43. [Overview :: Spring Framework](https://docs.spring.io/spring-framework/reference/web/webflux/new-framework.html) - Concurrency Model Both Spring MVC and Spring WebFlux support annotated controllers, but there is a k...

44. [This Year in Spring – December 30th, 2025](https://spring.io/blog/2025/12/30/this-year-in-spring-december-30th-2025/) - Level up your Java code and explore what Spring can do for you.

45. [Spring Framework 7.0 Release Notes](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-7.0-Release-Notes) - Spring Framework 7.0 retains a JDK 17 baseline. It also introduces a Jakarta EE 11 baseline and embr...

46. [Spring Boot 4.0 Migration Guide - GitHub](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide/978a4fc25d4785ec8624aae2883b355873c7d805) - Spring Boot helps you to create Spring-powered, production-grade applications and services with abso...

47. [Providing a Scope for...](https://docs.spring.io/spring-framework/reference/core/beans/classpath-scanning.html)

48. [docs.spring.io › core › beansBean Scopes :: Spring Framework](https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html)

49. [Task Execution and Scheduling :: Spring Framework](https://docs.spring.io/spring-framework/reference/integration/scheduling.html) - Annotation Support for Scheduling and Asynchronous Execution Spring provides annotation support for ...

50. [Declarative Annotation-based Caching](https://docs.spring.io/spring-framework/reference/integration/cache/annotations.html) - As of 6.1, cache annotations take CompletableFuture and reactive return types into account, automati...

51. [Declarative Annotation-based Caching](https://docs.spring.io/spring-framework/reference/7.1/integration/cache/annotations.html)

52. [REST Clients](https://docs.spring.io/spring-framework/reference/7.1/integration/rest-clients.html)

53. [Tracing :: Spring Boot](https://docs.spring.io/spring-boot/reference/actuator/tracing.html) - Spring Boot Actuator provides dependency management and auto-configuration for Micrometer Tracing, a...

54. [HTTP Interface Client :: Spring Framework](https://docs.spring.io/spring-framework/reference/6.2/web/webflux-http-interface-client.html)

55. [API Versioning :: Spring Framework](https://docs.spring.io/spring-framework/reference/7.0-SNAPSHOT/web/webflux-versioning.html)

56. [API Versioning :: Spring Framework](https://docs.spring.io/spring-framework/reference/web/webflux-versioning.html) - WebFlux supports API versioning. Client support for API versioning is available also in RestClient ,...

57. [Mapping Requests :: Spring Framework](https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-requestmapping.html)

58. [Kotlin Configuration](https://docs.spring.io/spring-security/reference/7.2/servlet/configuration/kotlin.html)

59. [Testing :: Spring Boot](https://docs.spring.io/spring-boot/reference/testing/index.html)

60. [Testing Spring Boot Applications](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html) - In addition to @SpringBootTest a number of other annotations are also provided for testing more spec...

61. [Testcontainers :: Spring Boot](https://docs.spring.io/spring-boot/reference/testing/testcontainers.html)

62. [Programmatic Bean Registration](https://docs.spring.io/spring-framework/reference/7.0-SNAPSHOT/core/beans/java/programmatic-bean-registration.html)

63. [Programmatic Bean Registration](https://docs.spring.io/spring-framework/reference/core/beans/java/programmatic-bean-registration.html) - As of Spring Framework 7, a first-class support for programmatic bean registration is provided via t...

64. [Spring Framework 7.0 Release Notes · spring-projects ... - GitHub](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-7.0-Release-Notes/d2df491db0e5856e31b1694e5c6efdf727bcb517) - Spring Framework. Contribute to spring-projects/spring-framework development by creating an account ...

65. [Observability :: Spring Boot](https://docs.spring.io/spring-boot/reference/actuator/observability.html) - For metrics and traces, Spring Boot uses Micrometer Observation. To create your own observations (wh...

66. [Metrics :: Spring Boot](https://docs.spring.io/spring-boot/reference/actuator/metrics.html) - Micrometer provides a hierarchical mapping to JMX, primarily as a cheap and portable way to view met...

67. [Graceful Shutdown :: Spring Boot](https://docs.spring.io/spring-boot/reference/web/graceful-shutdown.html) - Graceful shutdown is enabled by default with all three embedded web servers (Jetty, Reactor Netty, a...

68. [Developer Tools :: Spring Boot](https://docs.spring.io/spring-boot/reference/using/devtools.html) - The report shows the changes to your application's auto-configuration as you make changes such as ad...

