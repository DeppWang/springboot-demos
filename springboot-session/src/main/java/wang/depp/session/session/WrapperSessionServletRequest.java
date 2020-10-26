package wang.depp.session.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 包含 wrapperSession 的自定义 request
 */
public class WrapperSessionServletRequest extends HttpServletRequestWrapper {

    private WrapperSession wrapperSession;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public WrapperSessionServletRequest(HttpServletRequest request) {
        super(request);
    }

    public void setSession(WrapperSession wrapperSession) {
        this.wrapperSession = wrapperSession;
    }

    @Override
    public WrapperSession getSession() {
        return wrapperSession;
    }

    @Override
    public WrapperSession getSession(boolean create) {
        return getSession();
    }
}
