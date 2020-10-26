package wang.depp.session.session.config;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import wang.depp.session.session.utils.SessionLogger;
import wang.depp.session.session.StoreType;
import wang.depp.session.session.exception.SessionConfigParseException;

import java.io.InputStream;
import java.util.List;

public class SessionConfigFactory {

    private static SessionConfig sessionConfig;

    public static final String LIFECYCLE = "lifecycle";
    public static final String STORE_TYPE = "storeType";
    public static final String DOMAIN = "domain";
    public static final String PATH = "path";
    public static final String HTTPONLY = "httponly";
    public static final String ENCRYPT = "encrypt";
    public static final String READONLY = "readonly";


    /**
     * 从 session-config.xml 中读取配置信息
     * @param sessionConfigFileName
     * @param domain
     * @return
     * @throws SessionConfigParseException
     */
    public static SessionConfig readSessionConfig(String sessionConfigFileName, String domain) throws SessionConfigParseException {
        if (null != sessionConfig) {
            return sessionConfig;
        }

        final InputStream configFileInputSteam = SessionConfigFactory.class.getClassLoader().getResourceAsStream(sessionConfigFileName);
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(configFileInputSteam);
            sessionConfig = new DefaultSessionConfig();
            List list = document.selectNodes("/sessionConfig/defaultConfig");
            Element defaultConfig = null;
            if (null != list && !list.isEmpty()) {
                defaultConfig = (Element) list.get(0);
            }
            initEntries(document, sessionConfig, defaultConfig, domain);
            initGlobalInfo(document, sessionConfig);
            return sessionConfig;
        } catch (DocumentException e) {
            SessionLogger.logError("parse session config file error", e);
            throw new SessionConfigParseException("parse session config file error", e);
        }
    }


    private static void initGlobalInfo(Document document, SessionConfig sessionConfig) {
        final List<Element> globalInfos = document.selectNodes("/sessionConfig/globalinfo/key");
        for (Element globalInfo : globalInfos) {
            sessionConfig.addGlobalInfo(globalInfo.attributeValue("name"), globalInfo.getText());
        }
    }

    private static void initEntries(Document document, SessionConfig sessionConfig, Element defaultConfig, String domain) {

        String defaultLifecycle = "-1";
        String defaultStoreType = "cookie";

        if (null != defaultConfig) {
            defaultLifecycle = defaultConfig.elementText(LIFECYCLE);
            defaultStoreType = defaultConfig.elementText(STORE_TYPE);
        }

        final List<Element> entries = document.selectNodes("/sessionConfig/entries/entry");
        for (Element element : entries) {

            String lifecycle = StringUtils.isBlank(element.elementText(LIFECYCLE)) ? defaultLifecycle : element.elementText(LIFECYCLE);
            String storeType = StringUtils.isBlank(element.elementText(STORE_TYPE)) ? defaultStoreType : element.elementText(STORE_TYPE);

            SessionConfigEntry configEntry = new SessionConfigEntry();
            configEntry.setDomain(domain);

            configEntry.setPath(element.elementText(PATH));
            configEntry.setLifeCycle(Integer.valueOf(lifecycle));
            configEntry.setHttpOnly("true".equals(element.elementText(HTTPONLY)));
            configEntry.setReadOnly("true".equals(element.elementText(READONLY)));
            configEntry.setEncrypt("true".equals(element.elementText(ENCRYPT)));
            configEntry.setStoreType("cookie".equals(storeType) ? StoreType.cookie : StoreType.cache);
            configEntry.setKey(element.elementText("key"));
            configEntry.setName(element.attributeValue("name"));
            configEntry.setType(element.elementText("type"));

            Element map = element.element("map");
            if (map != null) {
                configEntry.setType("java.util.Map");
                List<Element> fields = map.elements("field");
                for (Element field : fields) {
                    configEntry.addMapFiled(new MapField(field.elementText("name"), field.elementText("type")));
                }
            }

            sessionConfig.addSessionConfigEntry(configEntry);
        }
    }
}
