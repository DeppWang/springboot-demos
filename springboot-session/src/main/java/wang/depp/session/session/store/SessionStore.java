package wang.depp.session.session.store;


import wang.depp.session.session.config.SessionConfigEntry;

public interface SessionStore extends SessionCacheContainerAware {

    /**
     * 查询属性
     *
     * @param sessionConfigEntry
     * @return
     */
    Object getAttribute(SessionConfigEntry sessionConfigEntry);

    /**
     * 设置属性
     *
     * @param sessionConfigEntry
     * @param value
     */
    void setAttribute(SessionConfigEntry sessionConfigEntry, Object value);

    /**
     * 提交 SessionStore 中存储的数据
     */
    void commit();

    /**
     * 初始化 sessionStore
     */
    void init();
}
