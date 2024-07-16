## Redisson 与 SpringBoot 的版本对应关系

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>3.17.5</version>
</dependency>
```
| redisson-spring-boot-starter | Spring Boot version |
| ---------------------------- | ------------------- |
| 3.17.5                       | 2.7.2               |
|        3.25.1                      |    3.3.0                 |
- 应该是
- redisson-spring-boot-starter 3.1x 对应 Spring Boot 2.x
- redisson-spring-boot-starter 3.2x 对应 Spring Boot 3.x

查看方式：
1. 切换到指定 Tag
2. 搜索 `<spring-boot.version>` 寻找对应版本
- https://github.com/search?q=repo%3Aredisson%2Fredisson%20%3Cspring-boot.version%3E&type=code

###  redisson-spring-data 与 Spring Boot 的对应关系

Redisson 使用 redisson-spring-boot-starter，redisson-spring-boot-starter 依赖 redisson-spring-data，redisson-spring-data 与 Spring Boot 的对应关系
| redisson-spring-data module name | Spring Boot version |
| -------------------------------- | ------------------- |
| redisson-spring-data-16          | 1.3.y               |
| redisson-spring-data-17          | 1.4.y               |
| redisson-spring-data-18          | 1.5.y               |
| redisson-spring-data-2x          | 2.x.y               |
| redisson-spring-data-3x          | 3.x.y               |
- 来源: https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter


## 无需额外使用配置类初始化配置文件，@Autowire 自动注入

```java
@Component
public class RedisLock {

    @Autowired
    private RedissonClient redissonClient;

    public void getLock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
    }
}
```

```Java
@SpringBootApplication
public class SpringbootRedissonApplication {


    public static void main(String[] args) {
       ApplicationContext context = SpringApplication.run(SpringbootRedissonApplication.class, args);
       // Component 使用
       RedisLock redisLock = context.getBean(RedisLock.class);
       redisLock.getLock("test");
    }

}
```

```properties
spring.application.name=springboot-redisson
# Redis configuration
spring.redis.database=11
spring.redis.host=127.0.0.1
spring.redis.port=6379
spring.redis.password=12345
spring.redis.timeout=6000
```

## Redisson 如果做到不用配置类，可自动使用的

使用 RedissonAutoConfiguration 与  RedissonProperties 实现
- https://github.com/redisson/redisson/blob/redisson-3.17.5/redisson-spring-boot-starter/src/main/java/org/redisson/spring/starter/RedissonAutoConfiguration.java
- https://github.com/redisson/redisson/blob/redisson-3.17.5/redisson-spring-boot-starter/src/main/java/org/redisson/spring/starter/RedissonProperties.java

## Link
- https://github.com/redisson/redisson
- https://springdoc.cn/spring-boot-redisson/