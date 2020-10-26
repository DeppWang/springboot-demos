package wang.depp.session.web.v3;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

//@Component
public class BaseController {

    // 获取当前 HttpServletRequest
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }

    public static void setAttribute(String name, Object value) {
        getRequest().getSession().setAttribute(name, value);
    }

    public static Object getAttribute(String name) {
        return getRequest().getSession().getAttribute(name);
    }

    public static void removeAttribute(String name) {
        getRequest().getSession().removeAttribute(name);
    }
}
