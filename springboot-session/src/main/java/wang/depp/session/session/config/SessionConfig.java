package wang.depp.session.session.config;

import java.util.Collection;

public interface SessionConfig {

    SessionConfigEntry getSessionConfigEntry(String name);

    /**
     * query all session config attribute names
     *
     * @return
     */
    Collection<String> getAttributeNames();

    Collection<SessionConfigEntry> getAllSessionConfigAttributes();

    /**
     * gain the global config infomation,for example the encrypt key and so on
     *
     * @param key
     * @return
     */
    String getGlobalConfigInfo(String key);

    void addGlobalInfo(String key, String value);

    void addSessionConfigEntry(SessionConfigEntry sessionConfigEntry);
}

