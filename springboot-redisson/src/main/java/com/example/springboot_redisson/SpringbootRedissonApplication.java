package com.example.springboot_redisson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringbootRedissonApplication {


	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(SpringbootRedissonApplication.class, args);
		// Component 使用
		RedisLock redisLock = context.getBean(RedisLock.class);
		redisLock.getLock("test");
	}

}
