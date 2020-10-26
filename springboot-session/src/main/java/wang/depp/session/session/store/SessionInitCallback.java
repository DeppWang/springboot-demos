package wang.depp.session.session.store;

import wang.depp.session.session.WrapperSession;
import wang.depp.session.session.WrapperSessionServletRequest;
import wang.depp.session.session.WrapperSessionServletResponse;

public interface SessionInitCallback {

    /**
     * 做一个 Session 同步，当用户登录系统（passport）分离时，将 Session 同步到 passport 中
     */
    void afterSessionCreated(WrapperSession wrapperSession, WrapperSessionServletRequest krqRequest, WrapperSessionServletResponse krqReponse);

    String getSessionIdDomain();
}
