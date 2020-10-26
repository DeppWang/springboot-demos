package wang.depp.session.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Created by DEPP WANG on 23/10/2020
 */
@Slf4j
@Component
public class RedisUtils {
    private static StringRedisTemplate stringRedisTemplate;

    @Autowired
    private StringRedisTemplate autowiredStringRedisTemplate;

    @PostConstruct
    private void init() {
        stringRedisTemplate = this.autowiredStringRedisTemplate;
    }

    public static void setKey(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(addKeyPrefix(key), value, timeout, unit);
    }

    public static String getKey(String key) {
        return stringRedisTemplate.opsForValue().get(addKeyPrefix(key));
    }

    public static Boolean deleteKey(String key) {
        return stringRedisTemplate.opsForValue().getOperations().delete(addKeyPrefix(key));
    }
    
    public static Long incrementKey(String key) {
        return stringRedisTemplate.opsForValue().increment(addKeyPrefix(key));
    }

    private static String addKeyPrefix(String key) {
        return String.format("session:%s", key);
    }
}
