package com.example.springboot_redisson;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public final class RedisLockerUtil {

//    private RedisLockerUtil() {
//    }
//
//    @Autowired
//    private RedissonClient redissonClient;
//
//    private static RedisLockerUtil redisLockerUtil;
//
//    @PostConstruct
//    private void init() {
//        redisLockerUtil = this;
//    }
//
//
//    public static RLock getLock(String key) {
//        return redisLockerUtil.redissonClient.getLock(key);
//    }
//

}
