package wang.depp.session.session;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.depp.session.session.config.SessionConfigFactory;
import wang.depp.session.session.store.SessionCacheContainer;
import wang.depp.session.session.config.SessionConfig;
import wang.depp.session.session.store.SessionConstants;
import wang.depp.session.session.store.SessionInitCallback;
import wang.depp.session.session.store.SessionStore;
import wang.depp.session.session.utils.SessionLogger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter 相关属性将一直存活
 */
public class WrapperSessionFilter implements Filter {
    final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * session-config.xml 的相关信息
     */
    private SessionConfig sessionConfig;

    private FilterConfig filterConfig;

    /**
     * 可配置的文件
     */
    private static final String DEFAULT_CONFIG_FILE_NAME = "session-config.xml";

    /**
     * 配置不需要进行 session 过滤的 URL
     */
    private String[] forbiddenUrlSuffixes;

    public void setForbiddenUrlSuffixes(String[] forbiddenUrlSuffixes) {
        this.forbiddenUrlSuffixes = forbiddenUrlSuffixes;
    }

    public int getSessionExpireTime() {
        return sessionExpireTime;
    }

    public void setSessionExpireTime(int sessionExpireTime) {
        this.sessionExpireTime = sessionExpireTime;
    }

    public String getSiteTag() {
        return siteTag;
    }

    public void setSiteTag(String siteTag) {
        this.siteTag = siteTag;
    }

    public SessionInitCallback getSessionPostHandler() {
        return sessionPostHandler;
    }

    public void setSessionPostHandler(SessionInitCallback sessionPostHandler) {
        this.sessionPostHandler = sessionPostHandler;
    }

    public SessionCacheContainer getSessionCacheContainer() {
        return sessionCacheContainer;
    }

    public void setSessionCacheContainer(SessionCacheContainer sessionCacheContainer) {
        this.sessionCacheContainer = sessionCacheContainer;
    }

    /**
     * 默认的 session 超期时间，30 分钟
     */
    private int sessionExpireTime = 1800;

    private String siteTag;

    private SessionInitCallback sessionPostHandler;

    private SessionCacheContainer sessionCacheContainer;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;

//        String sessionCacheContainerClass = filterConfig.getInitParameter("sessionCacheContainer");
//        if (StringUtils.isBlank(sessionCacheContainerClass)) {
//            throw new RuntimeException(" 没有配置 session 数据容器，请在 filter 的 init parameter 参数中配置 ");
//        } else {
//            try {
//                sessionCacheContainer = (SessionCacheContainer) Class.forName(sessionCacheContainerClass).newInstance();
//            } catch (Exception e) {
//                throw new RuntimeException(" 不能初始化 session 数据容器 ",e);
//            }
//        }
//
//
//        String sessionPostHandlerClass = filterConfig.getInitParameter("sessionPostHandler");
//        if (StringUtils.isNotBlank(sessionPostHandlerClass)) {
//            try {
//                sessionPostHandler = (SessionInitCallback) Class.forName(sessionPostHandlerClass).newInstance();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        initSessionConfig(sessionPostHandler == null ? "" : sessionPostHandler.getSessionIdDomain());
    }

    private void initSessionConfig(String domain) {
        sessionConfig = SessionConfigFactory.readSessionConfig(DEFAULT_CONFIG_FILE_NAME, domain);


//        if (StringUtils.isNotBlank(filterConfig.getInitParameter("forbiddenUrlSuffixes"))) {
//            forbiddenUrlSuffixes = filterConfig.getInitParameter("forbiddenUrlSuffixes").split(",");
//        }
//
//        final String sessionExpireTime = filterConfig.getInitParameter("sessionExpireTime");
//        this.siteTag = filterConfig.getInitParameter("siteTag");
//
//        if (StringUtils.isNotBlank(sessionExpireTime)) {
//            try {
//                this.sessionExpireTime = Integer.parseInt(sessionExpireTime) * 60;
//            } catch (NumberFormatException e) {
//                SessionLogger.logError("session 框架超期时间设置有误，请确认是否是数字类型 ", e);
//            }
//        }


    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        // 将输出数据，默认使用 StandardSessionFacade
        logger.info("sessionFacade: {}", httpServletRequest.getSession());

        // use custom redis-session replace default tomcat-session
        WrapperSessionServletRequest krqRequest = new WrapperSessionServletRequest((HttpServletRequest) request);
        logger.info("wrapperSession: {}", krqRequest.getSession()); // 此时为 null

        // ignore URL lists
        if (letitgo(request, response, chain, krqRequest)) {
            return;
        }

        // 包含 Session 的
        WrapperSessionServletResponse krqResponse = new WrapperSessionServletResponse((HttpServletResponse) response);
        // 每次请求都将创建一个 wrapperSession
        WrapperSession wrapperSession = createWrapperSession(krqRequest, krqResponse);
        logger.info("wrapperSession2: {}", krqRequest.getSession());


        try {
            chain.doFilter(krqRequest, krqResponse);
//            wrapperSession.commit(); // 返回请求是，response 的 getWriter() 将调用，所以此处可省略，避免重复调用
        } catch (Exception ex) {
            SessionLogger.logError("session framework occur exception", ex);
            throw new RuntimeException(ex);
        }
    }


    /**
     * 判断是否需要经过 sessionFilter 处理
     *
     * @param request
     * @param response
     * @param chain
     * @param krqRequest
     * @return
     * @throws IOException
     * @throws ServletException
     */
    private boolean letitgo(ServletRequest request, ServletResponse response, FilterChain chain, WrapperSessionServletRequest krqRequest) throws IOException, ServletException {
        if (null == forbiddenUrlSuffixes) {
            return false;
        }
        String requestURI = krqRequest.getRequestURI();
        for (String forbiddenSuffix : forbiddenUrlSuffixes) {
            if (StringUtils.isNotBlank(requestURI)) {
                if (requestURI.endsWith(forbiddenSuffix)) {
                    chain.doFilter(request, response);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void destroy() {
    }


    private WrapperSession createWrapperSession(WrapperSessionServletRequest krqRequest, WrapperSessionServletResponse krqResponse) {
        WrapperSession wrapperSession = new WrapperSession(sessionConfig, filterConfig.getServletContext(), sessionExpireTime, siteTag); // 每次请求重新生成 wrapperSession

        // SessionStore 包含 CookieStore 和 SessionStore
        Map<StoreType, SessionStore> sessionStores = new HashMap<>();
        // cookieStore 也包含 krqRequest 和 krqResponse
        sessionStores.put(StoreType.cookie, StoreType.cookie.newSessionStore(krqRequest, krqResponse, sessionConfig));

        SessionStore cacheStore = StoreType.cache.newSessionStore(krqRequest, krqResponse, sessionConfig);
        sessionStores.put(StoreType.cache, cacheStore);

        cacheStore.setSessionCacheContainer(sessionCacheContainer);

        wrapperSession.setSessionStores(sessionStores);

        // 给 request 与 response 均设置 session
        krqRequest.setSession(wrapperSession);
        krqResponse.setSession(wrapperSession);

        // 主要是给 session-config.xml 中的 cookie 设置默认值（从 request 中读取）
        wrapperSession.init();

        // visit time，只是做一个记录
        wrapperSession.setAttribute(SessionConstants.LAST_VISIT_TIME, Long.toString(System.currentTimeMillis() / 1000)); // 更新缓存时间

        if (sessionPostHandler != null) {
            sessionPostHandler.afterSessionCreated(wrapperSession, krqRequest, krqResponse);
        }

        return wrapperSession;
    }

}