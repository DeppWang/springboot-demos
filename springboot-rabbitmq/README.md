> AMQP 是一种使用广泛的独立于语言的消息协议，它的全称是 Advanced Message Queuing Protocol，即高级消息队列协议，它定义了一种二进制格式的消息流，任何编程语言都可以实现该协议。实际上，Artemis 也支持 AMQP，但实际应用最广泛的 AMQP 服务器是使用 [Erlang](https://www.erlang.org/) 编写的 [RabbitMQ](https://www.rabbitmq.com/)。

## 使用

1、MacOS [brew 安装](https://www.rabbitmq.com/install-homebrew.html) RabbitMQ

1. 下载安装

```shell
brew install rabbitmq
```

2. 启动 rabbit server

```shell
brew services start rabbitmq
```

2、RabbitMQ 服务端地址默认为 [http://localhost:15672](http://localhost:15672/)，默认账号密码均为 guest

```ascii
                                      ┌───────┐
                                 ┌───>│Queue-1│
                  ┌──────────┐   │    └───────┘
              ┌──>│Exchange-1│───┤
┌──────────┐  │   └──────────┘   │    ┌───────┐
│Producer-1│──┤                  ├───>│Queue-2│
└──────────┘  │   ┌──────────┐   │    └───────┘
              └──>│Exchange-2│───┤
                  └──────────┘   │    ┌───────┐
                                 └───>│Queue-3│
                                      └───────┘
```

Exchange 和 Queue 都必须配置，使用 binding 绑定 Exchange 和 Queue，Exchange 和 Queue 的关系为「一 对多」时， Exchange 和 Queue 可用 routeKey 关联。

3、客户端使用

1. 依赖

```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-amqp</artifactId>
		</dependency>
```

- stater 包，相关 Bean 将由 autoconfigure 来自动配置。

1. 服务器参数

```yml
spring:
    rabbitmq:
        host: localhost
        port: 5672
        username: guest
        password: guest
```

发送方配置消息类型。默认只能发送 String 和字节码数据。使用 Jackson2JsonMessageConverter 后可发送对象数据。

```java
	@Bean
	MessageConverter createMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
```

发送方发送

```java
@Component
public class MessagingService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    RabbitTemplate rabbitTemplate;

    public void sendMessage(Message msg) {
        logger.info("msg: {}", msg);
        rabbitTemplate.convertAndSend("login", "", msg);
    }
}
```

接收方监听

```Java
@Component
public class QueueMessageListener {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	static final String QUEUE_FILE = "waitfile";

	@RabbitListener(queues = QUEUE_FILE)
	public void onRegistrationMessageFromMailQueue(Message message) {
		logger.info("queue {} received waitfile message: {}", QUEUE_FILE, message.message);
	}
}
```

## 注意

发送后无法取得返回结果。可再建立一个 Queue 用于发送结果？

当跨应用（进程）发送时，只需消息对象包含同样的参数。

服务器安装 RabbitMQ 服务端。

## 应用

合作方 jacaranda 调用我们接口，推送相应数据，我们将用户相关 json 数据发送到消息队列，相关数据由接收方®再做进一步处理。

## 参考

[廖雪峰集成 RabbitMQ](https://www.liaoxuefeng.com/wiki/1252599548343744/1282385960239138)