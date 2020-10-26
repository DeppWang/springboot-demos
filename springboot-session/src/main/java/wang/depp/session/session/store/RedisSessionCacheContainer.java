package wang.depp.session.session.store;


import wang.depp.session.utils.RedisUtils;
import java.util.concurrent.TimeUnit;


public class RedisSessionCacheContainer implements SessionCacheContainer {

    @Override
    public void deleteSession(String sessionId) {
        RedisUtils.deleteKey(sessionId);
    }

    @Override
    public String getSessionValue(String sessionId) {
        return RedisUtils.getKey(sessionId);
    }

    @Override
    public void setSessionValue(String sessionId, String sessionValue, int expireSeconds) {
        RedisUtils.setKey(sessionId, sessionValue, expireSeconds, TimeUnit.SECONDS);
    }
}