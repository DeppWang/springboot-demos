package wang.depp.session.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import wang.depp.session.web.v1.UserController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Order(1)
@Component
public class MvcInterceptor implements HandlerInterceptor {

	final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
		logger.info("postHandle {}.", request.getRequestURI());
		if (modelAndView != null) {
			modelAndView.addObject("user", request.getSession().getAttribute(UserController.KEY_USER));
			modelAndView.addObject("__time__", LocalDateTime.now());
		}
//		response.getWriter(); // 当前后端分离时，调用 write() 写入返回数据；此处使用 getSession().commit(); 将 session 写入 Redis。
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		logger.info("afterCompletion {}: exception = {}", request.getRequestURI(), ex);
	}
}
