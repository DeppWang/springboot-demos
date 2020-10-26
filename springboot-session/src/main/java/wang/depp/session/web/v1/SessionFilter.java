package wang.depp.session.web.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Component
public class SessionFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(SessionFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
//        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        // 将输出数据，默认使用 StandardSessionFacade
        logger.info("session: {}", httpServletRequest.getSession());
        logger.info("sessionID: {}", httpServletRequest.getSession().getId());
//        logger.info("sessionID: {}", httpServletResponse.getSession().getId());
        filterChain.doFilter(servletRequest, servletResponse);

    }
}
