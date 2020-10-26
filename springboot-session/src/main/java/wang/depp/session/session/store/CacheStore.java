package wang.depp.session.session.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import wang.depp.session.session.utils.SessionLogger;
import wang.depp.session.session.WrapperSessionServletRequest;
import wang.depp.session.session.WrapperSessionServletResponse;
import wang.depp.session.session.config.SessionConfig;
import wang.depp.session.session.config.SessionConfigEntry;

import java.util.HashMap;
import java.util.Map;

public class CacheStore implements SessionStore, SessionCacheContainerAware {

    private final WrapperSessionServletRequest wrapperRequest;

    private volatile Map<String, Object> attributes;

    private volatile boolean isAlreadyReaded = false; // 已经从 Redis 中读取缓存？当前 CacheStore 对象只会从 Redis 中读取一次。

    private SessionConfig sessionConfig;

    private SessionCacheContainer sessionCacheContainer;


    public CacheStore(WrapperSessionServletRequest wrapperRequest, WrapperSessionServletResponse wrapperResponse, SessionConfig sessionConfig) {
        this.wrapperRequest = wrapperRequest;
        this.sessionConfig = sessionConfig;
    }

    @Override
    public Object getAttribute(SessionConfigEntry sessionConfigEntry) {
        loadCache(); // 先从 Redis 写入 attributes，当 readFromCache() 方法调用后，此时将不再从 Redis 中获取。如果当前对象一直存活，再直接写入到 attribute，将不用从 Redis 中读取
        return attributes.get(sessionConfigEntry.getName());
    }

    @Override
    public void setAttribute(SessionConfigEntry sessionConfigEntry, Object value) {
        loadCache(); // 设置前，先从 Redis 写入 attributes
        if (null == value) { // 如果不存在，删除
            attributes.remove(sessionConfigEntry.getName());
        } else {
            attributes.put(sessionConfigEntry.getName(), value);  // 如果存在，将更新
        }
    }

    private void loadCache() {
        if (StringUtils.isNotBlank(wrapperRequest.getSession().getId()) && !isAlreadyReaded) { // 当读取过一次后，就不再从 Redis 中读取
            isAlreadyReaded = readFromCache();
        }
    }

    @Override
    public void init() {
        this.attributes = new HashMap<>();
    }

    /**
     * 当 session 提交的时候，需要更新 session 有效期，如果发现还没有从缓存服务器读取 session 数据，
     * 则先读取然后再写入，如果读取的时候不成功，那么就有可能会覆盖掉用户的 session 数据，因此
     * session 缓存服务器的可用性非常重要
     *
     *
     */
    @Override
    public void commit() {

        // 如果超时失效，则从 redis 中删除
        if (!wrapperRequest.getSession().isValid()) {
            sessionCacheContainer.deleteSession(wrapperRequest.getSession().getId());
            return;
        }

        if (!isAlreadyReaded) {
            if (!readFromCache()) {
                SessionLogger.logError("Session 缓存服务器已经挂了，请速度检查！");
            }
        }
        writeToCache();
    }

    private void writeToCache() {
        if (attributes.entrySet().size() > 0) {
            ObjectMapper mapper = new ObjectMapper();
            String value = null;
            try {
                value = mapper.writeValueAsString(attributes);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            sessionCacheContainer.setSessionValue(wrapperRequest.getSession().getId(), value, wrapperRequest.getSession().getMaxInactiveInterval());
        }
    }

    private boolean readFromCache() { // 从 Redis 中读取数据后缓存到 CacheStore(lastVisitTime)
        try {
            String json = sessionCacheContainer.getSessionValue(wrapperRequest.getSession().getId());
            if (json != null) {
                ObjectMapper mapper = new ObjectMapper();
                Map map = mapper.readValue(json, Map.class);
                for (Object key : map.keySet()) {
                    try {
                        Object value = map.get(key);
                        if (value.getClass() == String.class || value.getClass() == Boolean.class) {
                            attributes.put((String) key, value);
                        } else {
                            SessionConfigEntry sessionConfigEntry = sessionConfig.getSessionConfigEntry((String) key);
                            if (sessionConfigEntry != null) {
                                attributes.put((String) key, mapper.readValue(
                                        mapper.writeValueAsString(value), Class.forName(sessionConfigEntry.getType()))); // 需要知道类型才能转换回相应的对象
                            }
                        }
                    } catch (Exception ex) {
                        SessionLogger.logError(" 从缓存中读取 " + key + " 数据错误 ", ex);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            SessionLogger.logError(" 从缓存中读取 session 数据错误 ", e);
            return false;
        }
    }

    @Override
    public void setSessionCacheContainer(SessionCacheContainer sessionCacheContainer) {
        this.sessionCacheContainer = sessionCacheContainer;
    }
}