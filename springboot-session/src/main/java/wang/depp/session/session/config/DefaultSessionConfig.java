package wang.depp.session.session.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于匹配 session-config.xml，做默认 xml 配置
 */
public class DefaultSessionConfig implements SessionConfig {

    private Map<String, SessionConfigEntry> allConfigEntries = new HashMap<>();

    private Map<String, String> globalConfigInfo = new HashMap<>();

    @Override
    public SessionConfigEntry getSessionConfigEntry(String name) {
        return allConfigEntries.get(name);
    }

    @Override
    public void addSessionConfigEntry(SessionConfigEntry sessionConfigEntry) {
        this.allConfigEntries.put(sessionConfigEntry.getName(), sessionConfigEntry);
    }

    @Override
    public void addGlobalInfo(String key, String value) {
        globalConfigInfo.put(key, value);
    }

    @Override
    public Collection<String> getAttributeNames() {
        return allConfigEntries.keySet();
    }

    @Override
    public Collection<SessionConfigEntry> getAllSessionConfigAttributes() {
        return this.allConfigEntries.values();
    }

    @Override
    public String getGlobalConfigInfo(String key) {
        return globalConfigInfo.get(key);

    }
}
