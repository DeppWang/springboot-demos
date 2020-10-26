package wang.depp.session.web.v2;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import wang.depp.session.entity.User;
import wang.depp.session.utils.JsonUtils;
import wang.depp.session.utils.MatchUtils;
import wang.depp.session.utils.RedisUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//@Component
public class BaseController {

    final Logger logger = LoggerFactory.getLogger(getClass());

    final long EXPIRE_TIME = 1800;

    // 获取当前 HttpServletRequest
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }

    protected void setAttribute(String name, Object value) {
        String sessionId = getRequest().getSession().getId();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(name, value);
        logger.info("sessionId: {}", sessionId);
        logger.info("attributes: {}", JsonUtils.getJson(attributes));
        RedisUtils.setKey(sessionId, JsonUtils.getJson(attributes), EXPIRE_TIME, TimeUnit.SECONDS);
    }

    protected Object getAttribute(String name) {
        String sessionId = getRequest().getSession().getId();
        logger.info("sessionId: {}", sessionId);
        if (MatchUtils.isEmpty(sessionId)) {
            return null;
        }
        String attributesJson = RedisUtils.getKey(sessionId);
        if (MatchUtils.isEmpty(attributesJson)) {
            return null;
        }
        Map<String, Object> attributes = JsonUtils.fromJson(attributesJson, Map.class);
//        JSONObject jsonObject = JSONObject.parseObject(attributesJson);
        if (MatchUtils.isEmpty(attributes)) {
            return null;
        }
        logger.info("attributes: {}", attributes);
        logger.info("user: {}", attributes.get(name).toString());
        return attributes.get(name);
//        return JsonUtils.fromJson(attributes.get(name).toString(), User.class);
    }

    protected User getKeyUser(String name) {
        Object user = getAttribute(name);
//        return (User)user;
        return JsonUtils.fromJson(user.toString(), User.class); // 使用 (User)user; 将报转换错误，因为本身是 user 本身没有指定为 User
    }

    protected void removeAttribute(String name) {
        String sessionId = getRequest().getSession().getId();
        logger.info("sessionId: {}", sessionId);
        if (MatchUtils.isEmpty(sessionId)) {
            return;
        }
        String attributesJson = RedisUtils.getKey(sessionId);
        if (MatchUtils.isEmpty(attributesJson)) {
            return;
        }
        Map<String, Object> attributes = JsonUtils.fromJson(attributesJson, HashMap.class);
        if (MatchUtils.isEmpty(attributes)) {
            return;
        }
        logger.info("attributes: {}", attributes);
        attributes.remove(name);
        RedisUtils.setKey(sessionId, JsonUtils.getJson(attributes), EXPIRE_TIME, TimeUnit.SECONDS);
        // 简单的话，直接删除 sessionId，可以删除 attributes 的 __user__
//        RedisUtils.deleteKey(sessionId);
    }

}
