package wang.depp.session.session;

import wang.depp.session.session.WrapperSessionServletRequest;
import wang.depp.session.session.WrapperSessionServletResponse;
import wang.depp.session.session.config.SessionConfig;
import wang.depp.session.session.store.CacheStore;
import wang.depp.session.session.store.CookieStore;
import wang.depp.session.session.store.SessionStore;


/**
 **/
public enum StoreType {

    cookie {
        @Override
        SessionStore newSessionStore(WrapperSessionServletRequest krqRequest, WrapperSessionServletResponse krqResponse, SessionConfig sessionConfig) {
            return new CookieStore(krqRequest, krqResponse, sessionConfig);
        }
    },

    /**
     * cache store
     */
    cache {
        @Override
        SessionStore newSessionStore(WrapperSessionServletRequest krqRequest, WrapperSessionServletResponse krqResponse, SessionConfig sessionConfig) {
            return new CacheStore(krqRequest, krqResponse, sessionConfig);
        }
    };

    abstract SessionStore newSessionStore(WrapperSessionServletRequest krqRequest, WrapperSessionServletResponse krqResponse, SessionConfig sessionConfig);
}
