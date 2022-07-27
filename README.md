# mall-learning学习记录
## 简介
跟着项目mall-learning的学习路线进行学习，分四个章节学习，架构篇、业务篇、技术要点篇、部署篇。

项目地址：https://github.com/macrozheng/mall-learning
## 架构篇
### mall整合SpringBoot+MyBatis搭建基本骨架
主要使用SpringBoot+MyBatis搭建基本骨架，重点使用了Mybatis的mbg工程，自动根据数据库字段生成了model，以及单表的mapper接口
，mapper接口中生成了单表的CRUD接口，还有mapper接口对应的sql映射文件。
### mall整合Swagger-UI实现在线API文档
Swagger-UI：Swagger-UI是HTML, Javascript, CSS的一个集合，可以动态地根据注解生成在线API文档。

常用注解：

* @Api：用于修饰Controller类，生成Controller相关文档信息
* @ApiOperation：用于修饰Controller类中的方法，生成接口方法相关文档信息
* @ApiParam：用于修饰接口中的参数，生成接口参数相关文档信息
* @ApiModelProperty：用于修饰实体类的属性，当实体类是请求参数或返回结果时，直接生成相关文档信息

注意：Swagger对生成API文档的范围有三种不同的选择
* 生成指定包下面的类的API文档
* 生成有指定注解的类的API文档
* 生成有指定注解的方法的API文档

具体使用：
引入Swagger-UI依赖以后，进行Swagger配置，在配置当中，可以根据上面三种不同的范围进行生成API文档
。配置完成以后，则在开发过程中，进行响应注解信息的标注，则可以在线生成API文档。

其中，@ApiModelProperty注解是标注在实体类中的属性，而使用Mybatis的mbg工程生成的model中如果人为的进行添加，则太麻烦了。
解决办法是使用CommentGenerator。CommentGenerator为MyBatis Generator的自定义注释生成器，修改addFieldComment方法使其生成Swagger的@ApiModelProperty注解来取代原来的方法注释，添加addJavaFileComment方法，使其能在import中导入@ApiModelProperty，否则需要手动导入该类，在需要生成大量实体类时，是一件非常麻烦的事。

访问Swagger-UI接口文档地址
项目跑起来以后，访问在线接口即可查看Api文档信息，接口地址：http://localhost:8080/swagger-ui.html
在代码中标注的注解信息将以Api文档的形式进行展示。并且还可以测试接口，查看接口的返回结果。

### mall整合Redis实现缓存功能

在本节中，整合Redis实现缓存功能，以短信验证码的案例进行实践。

springboot整合其他的依赖啥啥的，通用步骤都是一样的，引入starter，没有则引入相应的依赖。
Redis官方有starter，因此自动配置了需要的功能。

在使用redis时，这里进行了封装，将redis常用的功能封装为接口RedisService，在其实现类中实现具体功能，实现类中使用StringRedisTemplate
来完成具体的功能，就是封装了一下，方便操作。

这里灵活使用了配置文件进行配置，比如redis存储验证码的失效时间，可以在配置文件中灵活设置，使用@Value注解来完成注入
。


### mall整合SpringSecurity和JWT实现认证和授权

[//]: # (![img.png]&#40;img.png&#41;)


#### 如何整合SpringSecurity和JWT实现认证和授权？
首先引入对应的场景依赖，接着配置SpringSecurity，配置包括例如：拦截和放行的请求路径，
缓存的禁用，JWT filter等等，还有SpringSecurity使用的UserDetailsService
，其实就是一个根据用户名返回用户信息的lambda，也就是UserDetails，这里对其进行了扩展，使用了
AdminUserDetails。

**JWT的使用**：首先封装工具类，JwtTokenUtil，里面封装了关于token的使用。添加过滤器OncePerRequestFilter
，在用户名和密码校验前添加的过滤器，如果请求中有jwt的token且有效，会取出token中的用户名，然后调用SpringSecurity的API进行登录操作。

**SpringSecurity的使用**：
添加了RestAuthenticationEntryPoint，当未登录或者token失效访问接口时，自定义的返回结果。
添加了RestfulAccessDeniedHandler，当访问接口没有权限时，自定义的返回结果。

#### SpringSecurity和JWT实现认证和授权的原理：
首先当客户端发起请求时，RestAuthenticationEntryPoint会调用方法处理响应结果，也就是响应给客户端未登录或者token失效。
这时客户端会进行登录，登录主要做两个事情，第一个是使用UserDetailsService根据用户名加载对应的UserDetails，
密码验证通过后，进行授权认证，授权认证是授予一个token的认证，简单说是可以使用token。接下来返回token给客户端。到此，完成了认证和授权，后续的请求
中都会在请求头中携带token信息，之后用户每次调用接口都在http的header中添加一个叫Authorization的头，值为JWT的token
，后台程序通过对Authorization头中信息的解码及数字签名校验来获取其中的用户信息，从而实现认证和授权。


### mall整合Elasticsearch实现商品搜索

Elasticsearch 是一个分布式、可扩展、实时的搜索与数据分析引擎。 它能从项目一开始就赋予你的数据以搜索、分析和探索的能力，可用于实现全文搜索和实时数据统计。

具体使用：首先下载配置Elasticsearch，可以把它看成是一个数据库，接下来结合Spring Data Elasticsearch进行开发。
定义搜索信息实体类，在定义实体类时，结合注解@Document，@Field的注解来进行标识，这样是为了数据搜索服务。定义es操作接口，实现一些数据库操作。
接下来就是具体业务开发了。

es是一个文档型数据库，在关系型数据库mysql中数据库，表，列，每一行，在es中对应者索引，类型，字段，文档。
在使用搜索功能时，首先需要定义一个类型对象，对应于mysql中的表，在es中指的是类型。这个对象是一个媒介，mysql中的数据查出来封装在这个对象中，导入es
，这时es中就已经有了数据，可以继续下一步了。搜素功能主要是借助EsProductRepository extends ElasticsearchRepository<EsProduct, Long>
，这个接口的方法不需要实现，只要按着规范命名，即可在自动装配时以json查询的形式实现。因此只需在我们的业务逻辑中调用相应方法即可实现搜索功能。

es搜索的原理：主要借助于索引，es会在存储数据时建立每一个字段的索引，借助于索引，我们可以很快的根据我们字段内容找到我们需要的类型对象。

### mall整合Mongodb实现文档操作

#### 业务场景
实现用户商品浏览记录的创建，根据用户ID获取浏览记录，以及根据ID删除指定的浏览记录

#### 开发流程
1. 定义用户历史浏览记录的文档对象，即MemberReadHistory，在这个类上要标注@Document，对于类中的属性，文档对象的ID域添加@Id注解，需要检索的字段添加@Indexed注解。
2. 定义dao层接口MemberReadHistoryRepository来操作mongodb数据库CRUD，这个接口只需继承MongoRepository<MemberReadHistory,String>，即可实现基本的CRUD功能，甚至都不需要方法的实现，对于业务需要的功能，则可以自定义衍生查询，按照名字定义好抽象方法即可，也是不需要实现的。
3. 定义service层接口MemberReadHistoryService
4. 定义controller层MemberReadHistoryController

#### 要点解析
无

#### 注意事项
无

### mall整合RabbitMQ实现延迟消息

#### 业务场景
* 用户进行下单操作（会有锁定商品库存、使用优惠券、积分一系列的操作）；
* 生成订单，获取订单的id；
* 获取到设置的订单超时时间（假设设置的为60分钟不支付取消订单）；
* 按订单超时时间发送一个延迟消息给RabbitMQ，让它在订单超时后触发取消订单的操作；
* 如果用户没有支付，进行取消订单操作（释放锁定商品库存、返还优惠券、返回积分一系列操作）

#### 开发流程
1. RabbitMQ的安装与配置，设置用户，虚拟主机等等。
2. 修改application.yml文件，在spring节点下添加Mongodb相关配置。
3. 添加消息队列的枚举配置类QueueEnum，用于延迟消息队列及处理取消订单消息队列的常量定义，包括交换机名称、队列名称、路由键名称。
4. 添加RabbitMQ的配置，RabbitMqConfig，用于配置交换机、队列及队列与交换机的绑定关系。
5. 添加延迟消息的发送者CancelOrderSender，用于向订单延迟消息队列（mall.order.cancel.ttl）里发送消息。订单生成后就会使用发送者发送消息。
6. 添加取消订单消息的接收者CancelOrderReceiver，用于从取消订单的消息队列（mall.order.cancel）里接收消息。
接收者会监听对应的消费队列，当队列有消息时，便会进行消费，也就是执行其中的handle方法来完成消费。
7. 接下来就是service，controller层的业务逻辑开发

#### 要点解析

整个业务流程原理简析：当订单生成后，便发送一个延迟消息到消息队列mall.order.cancel.ttl，当消息到期后，将根据配置中的路由器和路由键进行转发，
也就是会转发到订单取消实际消费队列mall.order.cancel，这时CancelOrderReceiver会监听到队列里有东西了，直接进行消费处理，也就是取消订单。

交换机，路由键，以及队列三者关系：交换机根据路由键将消息转发到队列。队列与交换机根据路由键进行绑定。

#### 注意事项
在配置rabbitmq时，出现了一点小麻烦。

