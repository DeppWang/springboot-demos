package wang.depp.session.session;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 包含自定义 Session 的 Response
 **/
public class WrapperSessionServletResponse extends HttpServletResponseWrapper {

    private WrapperSession session;


    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response
     * @throws IllegalArgumentException if the response is null
     */
    public WrapperSessionServletResponse(HttpServletResponse response) {
        super(response);
    }

    // 什么时候调用？返回给前端时调用。
    @Override
    public PrintWriter getWriter() throws IOException {
        getSession().commit(); // 延长 session 的时间
        return super.getWriter();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        getSession().commit();
        return super.getOutputStream();
    }

    public void setSession(WrapperSession session) {
        this.session = session;
    }

    public WrapperSession getSession() {
        return session;
    }
    // 重定向时，将不走 getWriter()
    @Override
    public void sendRedirect(String location) throws IOException {
        getSession().commit();
        super.sendRedirect(location);
    }
}
