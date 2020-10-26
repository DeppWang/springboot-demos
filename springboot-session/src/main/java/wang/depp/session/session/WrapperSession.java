package wang.depp.session.session;

import org.apache.commons.lang3.StringUtils;
import wang.depp.session.session.config.MapField;
import wang.depp.session.session.config.SessionConfig;
import wang.depp.session.session.config.SessionConfigEntry;
import wang.depp.session.session.exception.ModifyReadOnlyAttributeException;
import wang.depp.session.session.store.CacheStore;
import wang.depp.session.session.store.SessionConstants;
import wang.depp.session.session.store.SessionStore;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WrapperSession implements HttpSession {

    private Map<StoreType, SessionStore> sessionStores;
    private SessionConfig sessionConfig;

    private long createTime;
    private String sessionId;
    private long lastAccessedTime;
    private ServletContext servletContext;

    // Redis 半个小时过期
    private int maxInactiveInterval = 1800;
    private String siteTag = "";

    private boolean valid = true;


    public WrapperSession(SessionConfig sessionConfig, ServletContext servletContext, int maxInactiveInterval, String st) {
        this.createTime = System.currentTimeMillis();
        this.lastAccessedTime = createTime;
        this.sessionConfig = sessionConfig;
        this.servletContext = servletContext;
        this.maxInactiveInterval = maxInactiveInterval;
        this.siteTag = st;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public long getCreationTime() {
        return this.createTime;
    }

    @Override
    public String getId() {
        return this.sessionId;
    }

    @Override
    public long getLastAccessedTime() {
        return this.lastAccessedTime;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }

    @Override
    @Deprecated
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAttribute(String name) {
        final SessionConfigEntry sessionConfigEntry = sessionConfig.getSessionConfigEntry(name); // 根据 name 找到对应 sessionConfigEntry
        if (sessionConfigEntry == null) {
            return null;
        }
        final SessionStore sessionStore = sessionStores.get(sessionConfigEntry.getStoreType());
        if (sessionStore == null) {
            return null;
        }

        Object attribute = sessionStore.getAttribute(sessionConfigEntry); // 根据 sessionConfigEntry 从 CookieStore 和 SessionStore 中获取
        if (attribute == null) {
            return attribute;
        }

        if (sessionStore instanceof CacheStore &&
                attribute instanceof Map &&
                sessionConfigEntry.getType().equals("java.util.Map") &&
                sessionConfigEntry.getMapFieldList().size() > 0) {
            Map map = (Map) attribute;
            if (map.size() == 0) {
                return attribute;
            }

            List<MapField> mapFieldList = sessionConfigEntry.getMapFieldList();
            for (MapField mapField : mapFieldList) {

                try {
                    if (map.get(mapField.getName()) != null) {
                        map.put(mapField.getName(),convertToType(Class.forName(mapField.getType()),map.get(mapField.getName())));
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return attribute;
    }


    private  <T> Object convertToType(Class<T> t, Object source) {

        if (source instanceof Double) {

            if (t == Integer.class) {
                return ((Double) source).intValue();
            }

            if (t == Long.class) {
                return ((Double) source).longValue();
            }
        }

        return source;
    }

    @Override
    @Deprecated
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration getAttributeNames() {
        return Collections.enumeration(sessionConfig.getAttributeNames());
    }

    @Override
    @Deprecated
    public String[] getValueNames() {
        final Collection<String> attributeNames = sessionConfig.getAttributeNames();
        return attributeNames.toArray(new String[attributeNames.size()]);
    }

    /**
     * force 是否持久。setAttribute 都将在在 SessionStore 中设置（会从 Redis 中读取，如存在则覆盖），写入缓存由 commit 实现
     * @param name
     * @param value
     */
    @Override
    public void setAttribute(String name, Object value) {
        setAttribute(name, value, false);
    }

    @Override
    @Deprecated
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }


    @Override
    public void removeAttribute(String name) {
        setAttribute(name, null);
    }

    @Override
    @Deprecated
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        for (SessionConfigEntry configEntry : sessionConfig.getAllSessionConfigAttributes()) {
            if (configEntry.getLifeCycle() <= 0) {
                removeAttribute(configEntry.getName(), true);
            }
        }
        valid = false;
    }

    @Override
    public boolean isNew() {
        return true;
    }

    public void init() {
        initSessionStore();
        this.sessionId = getSessionId(); // 从 CookieStore 的 attributes 中获取 sessionId
        generateTrackId();
    }
    // 默认从浏览器请求中获取 trackId，如果请求中没有，重新生成一个
    private void generateTrackId() {
        String trackId = (String) getAttribute(SessionConstants.TRACK_ID);
        if (StringUtils.isBlank(trackId)) {
            trackId = generateNewTrackId();
            setAttribute(SessionConstants.TRACK_ID, trackId, true);
        }
    }

    public SessionConfig getSessionConfig() {
        return sessionConfig;
    }

    private void setAttribute(String name, Object value, boolean force) {
        final SessionConfigEntry sessionConfigEntry = sessionConfig.getSessionConfigEntry(name);
        if (sessionConfigEntry == null) {
            return;
        }
        if (!force && sessionConfigEntry.isReadOnly()) {
            throw new ModifyReadOnlyAttributeException("not support to change the readonly attribute,the name" + name);
        }
        final SessionStore sessionStore = sessionStores.get(sessionConfigEntry.getStoreType());
        sessionStore.setAttribute(sessionConfigEntry, value);
    }
    // 默认从浏览器请求中获取 sessionId(js)，如果请求中没有，重新生成一个
    public String getSessionId() {
        String sessionId = (String) getAttribute(SessionConstants.SESSION_ID);
        if (StringUtils.isBlank(sessionId)) {
            sessionId = generateNewSessionId();
            setAttribute(SessionConstants.SESSION_ID, sessionId, true);
        }

        return sessionId;
    }

    private String generateNewTrackId() {
        return UUID.randomUUID().toString();
    }

    // 自定义 SessionId，不使用默认的 SessionId
    private String generateNewSessionId() {
        return UUID.randomUUID().toString() + "-" + siteTag;
    }

    public void setSessionStores(Map<StoreType, SessionStore> sessionStores) {
        this.sessionStores = sessionStores;
    }

    public void commit() {
        for (SessionStore sessionStore : sessionStores.values()) {
            sessionStore.commit();
        }
    }


    private void initSessionStore() {
        for (SessionStore sessionStore : sessionStores.values()) {
            sessionStore.init(); // 分别调用子类的 init() 方法
        }
    }

    /**
     * 用于失效 session 信息时使用
     *
     * @param name
     * @param force
     */
    private void removeAttribute(String name, boolean force) {
        setAttribute(name, null, true);
    }


}
