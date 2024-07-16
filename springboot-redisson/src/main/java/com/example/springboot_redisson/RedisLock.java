package com.example.springboot_redisson;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisLock {

    @Autowired
    private RedissonClient redissonClient;

    public void getLock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
    }
}
