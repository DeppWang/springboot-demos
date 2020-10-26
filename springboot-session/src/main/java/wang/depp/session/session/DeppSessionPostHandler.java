package wang.depp.session.session;

import org.springframework.beans.factory.annotation.Value;
import wang.depp.session.session.store.SessionInitCallback;

public class DeppSessionPostHandler implements SessionInitCallback {

    @Value("${demo.domain}")
    private String gatewayDomain;

    @Override
    public void afterSessionCreated(WrapperSession jimuboxSession, WrapperSessionServletRequest krqRequest, WrapperSessionServletResponse krqReponse) {

    }

    @Override
    public String getSessionIdDomain() {
        return gatewayDomain;
    }
}
