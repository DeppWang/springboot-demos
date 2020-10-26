package wang.depp.session.session.store;

public interface SessionCacheContainer {
    void deleteSession(String sessionId);

    String getSessionValue(String sessionId);

    void setSessionValue(String sessionId, String sessionValue, int expireSeconds);
}
