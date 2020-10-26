package wang.depp.session.session.store;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.depp.session.session.WrapperSessionServletRequest;
import wang.depp.session.session.WrapperSessionServletResponse;
import wang.depp.session.session.config.SessionConfig;
import wang.depp.session.session.config.SessionConfigEntry;
import wang.depp.session.session.utils.BlowfishUtils;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * CookieStore 只存放 Cookie，不写入 Redis，每次请求时新建 CookieStore 对象
 **/
public class CookieStore implements SessionStore {

    private static Logger logger = LoggerFactory.getLogger(CookieStore.class);

    private static final String URL_ENCODING = "UTF-8";
    /**
     * undecodedCookies means the navtive cookies,which directly comes from HttpRequest,
     * the session Framework need to process it deeply.
     */
    private Map<String, String> undecodedCookies = new HashMap<>();
    private Map<String, Attribute> attributes = new HashMap<>();


    private final WrapperSessionServletRequest wrapperRequest;
    private final WrapperSessionServletResponse wrapperResponse;
    /**
     * record the modified cookie.
     */
    private Set<String> modifiedCookies = new HashSet<>();
    private String DEFAULT_PATH = "/";
    private static final String BLOWFISH_ENCRYPT_KEY = "blowfish_key";
    //
    private String blowfishKey;


    public CookieStore(WrapperSessionServletRequest wrapperRequest, WrapperSessionServletResponse wrapperResponse, SessionConfig sessionConfig) {
        this.wrapperRequest = wrapperRequest;
        this.wrapperResponse = wrapperResponse;
    }


    @Override
    public void setAttribute(SessionConfigEntry sessionConfigEntry, Object value) {
        final String stringValue = ObjectUtils.toString(value, null);
        this.attributes.put(sessionConfigEntry.getName(), new Attribute(sessionConfigEntry, stringValue));
        this.modifiedCookies.add(sessionConfigEntry.getName());
    }

    @Override
    public Object getAttribute(SessionConfigEntry sessionConfigEntry) {
        Attribute value = this.attributes.get(sessionConfigEntry.getName()); // 先从 attributes 从找，再从 undecodedCookies 中找
        if (null == value) {
            value = decodeSimpleCookie(sessionConfigEntry, undecodedCookies);
            attributes.put(sessionConfigEntry.getName(), value); // 将从 undecodedCookies 找到的加到 attributes
        }
        return value.getValue();
    }


    private Attribute decodeSimpleCookie(SessionConfigEntry sessionConfigEntry, Map<String, String> undecodedCookies) {
        final String undecodedCookieValue = undecodedCookies.get(sessionConfigEntry.getKey());
        return new Attribute(sessionConfigEntry, decodeCookieValue(undecodedCookieValue, sessionConfigEntry));

    }

    // 从 request 中获取到浏览器传递的 cookie
    private void initCookies() {
        Cookie[] cookies = getCookiesFromRequest();
        if (null == cookies) {
            return;
        }
        for (Cookie cookie : cookies) {
            undecodedCookies.put(cookie.getName(), cookie.getValue());
        }
    }


    private Cookie[] getCookiesFromRequest() {
        if (wrapperRequest == null)
            return new Cookie[0];
        return wrapperRequest.getCookies();
    }

    @Override
    public void commit() {
        String[] tempCookieArray = modifiedCookies.toArray(new String[0]); // 判断 cookie 是否更新
        /*the combined cookie may be processed during the process of combined cookie*/
        for (String modifiedCookieName : tempCookieArray) {
            if (modifiedCookies.contains(modifiedCookieName)) {
                final SessionConfigEntry sessionConfigEntry = getSessionConfig().getSessionConfigEntry(modifiedCookieName);
                if (sessionConfigEntry == null) {
                    logger.error("write the cookie,name=" + modifiedCookieName + " which can't be configured");
                    continue;
                }
                Cookie cookie = buildCookie(sessionConfigEntry);
                if (null == cookie) {
                    continue;
                }
                wrapperResponse.addCookie(cookie); // 将 cookie 封装返回给浏览器
            }
        }
        /**
         * remove the blowfishUtils threadlocal cache
         */
        BlowfishUtils.remove();

    }


    private Cookie buildCookie(SessionConfigEntry sessionConfigEntry) {
        return buildSingleCookie(sessionConfigEntry);

    }


    private Cookie buildSingleCookie(SessionConfigEntry sessionConfigEntry) {
        final Object cookieValue = getAttribute(sessionConfigEntry);
        boolean isRemoved = null == cookieValue;
        String value = encodeCookieValue(cookieValue == null ? StringUtils.EMPTY : cookieValue.toString(), sessionConfigEntry);
        /**
         * it just remove the cookie which has existed.
         */
        if (isRemoved && !undecodedCookies.containsKey(sessionConfigEntry.getKey())) {
            return null;
        }
        removeFromModifiedCookie(sessionConfigEntry);
        return buildCookie(sessionConfigEntry, isRemoved, sessionConfigEntry.getKey(), value);

    }

    private Cookie buildCookie(SessionConfigEntry sessionConfigEntry, boolean removed, String key, String value) {
        String path = sessionConfigEntry.getPath();
        boolean isHttpOnly = sessionConfigEntry.isHttpOnly();
        int maxAge = removed ? 0 : sessionConfigEntry.getLifeCycle();
        Cookie jimuboxCookie = new Cookie(key, value);
        jimuboxCookie.setHttpOnly(isHttpOnly);
        if (StringUtils.isNotBlank(sessionConfigEntry.getDomain())) {
            jimuboxCookie.setDomain(sessionConfigEntry.getDomain());
        }
        jimuboxCookie.setPath(StringUtils.isBlank(path) ? DEFAULT_PATH : path);
        jimuboxCookie.setMaxAge(maxAge);
        return jimuboxCookie;
    }

    private void removeFromModifiedCookie(SessionConfigEntry configEntry) {
        this.modifiedCookies.remove(configEntry.getName());
    }

    public SessionConfig getSessionConfig() {
        return wrapperRequest.getSession().getSessionConfig();
    }


    private String encodeCookieValue(String cookieValue, SessionConfigEntry sessionConfigEntry) {
        if (StringUtils.isBlank(cookieValue)) {
            return cookieValue;
        }
        if (sessionConfigEntry.isEncrypt()) {
            cookieValue = BlowfishUtils.encryptBlowfish(cookieValue, getBlowfishKey());
        }

        try {
            cookieValue = URLEncoder.encode(cookieValue, URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error("urlencode failure,the value is :" + cookieValue, e);
            return null;
        }
        return cookieValue;

    }


    private String decodeCookieValue(String cookieValue, SessionConfigEntry sessionConfigEntry) {
        if (StringUtils.isBlank(cookieValue)) {
            return cookieValue;
        }
        try {
            cookieValue = URLDecoder.decode(cookieValue, URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error("urldecode failure,the value is :" + cookieValue, e);
            return null;
        }
        if (sessionConfigEntry.isEncrypt()) {
            cookieValue = BlowfishUtils.decryptBlowfish(cookieValue, getBlowfishKey());
        }

        return cookieValue;
    }

    @Override
    public void init() {
        initCookies();
        initEncryptKey();
    }

    private void initEncryptKey() {
        this.blowfishKey = getBlowfishKey();

    }

    public String getBlowfishKey() {
        if (!StringUtils.isBlank(blowfishKey)) {
            return blowfishKey;
        }
        final String blowfish_key = this.getSessionConfig().getGlobalConfigInfo(BLOWFISH_ENCRYPT_KEY);
        if (StringUtils.isBlank(blowfish_key)) {
            throw new IllegalStateException("blowfish key not configured,the blowfish_key must be configured");
        }
        this.blowfishKey = BlowfishUtils.decryptBlowfish(blowfish_key, "tiger");
        return blowfishKey;
    }

    private class Attribute {

        SessionConfigEntry sessionConfigEntry;

        Object value;

        private Attribute(SessionConfigEntry sessionConfigEntry, Object value) {
            this.sessionConfigEntry = sessionConfigEntry;
            this.value = value;
        }

        public SessionConfigEntry getSessionConfigEntry() {
            return sessionConfigEntry;
        }

        public Object getValue() {
            return value;
        }
    }

    @Override
    public void setSessionCacheContainer(SessionCacheContainer sessionCacheContainer) {
        //do nothing
    }
}

